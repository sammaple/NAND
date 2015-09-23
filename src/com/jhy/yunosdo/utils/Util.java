package com.jhy.yunosdo.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;








import com.jhy.yunosdo.MainActivity;
import com.jhy.yunosdo.net.PicTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * @Author: Shper
 * @Description: TODO
 * @Since: JDK 8.0
 * @Version: 0.1 2015年1月13日 C 创建<br>
 */
public class Util {

    public static String cpToData(Context ctx,String path,String destpath) {
       cutString(exec_grep("cp " + path + " " + destpath +"update.zip "));

       Toast.makeText(ctx, "拷贝完成", Toast.LENGTH_SHORT).show();
	   return "";
    }
    
    public static String delOTA(Context ctx) {
        cutString(exec_grep("rm " + "/data/update.zip "));
        cutString(exec_grep("rm " + "/sdcard/update.zip "));

        Toast.makeText(ctx, "清理完成", Toast.LENGTH_SHORT).show();
 	   return "";
     }
    
    /*
     * ro.product.model
     */
    public static String getModel() {
        return cutString(exec_grep("getprop | grep ro.product.model"));
    }

    /*
     * ro.build.version.release
     */
    public static String getVersion() {
        return cutString(exec_grep("getprop | grep ro.build.version.release"));
    }

    /*
     * ro.aliyun.clouduuid
     */
    public static String getUUID() {
        return cutString(exec_grep("getprop | grep ro.aliyun.clouduuid"));
    }

    /*
     * dhcp.eth0.ipaddress
     */
    public static String getIP() {
        return cutString(exec_grep("getprop | grep dhcp.eth0.ipaddress"));
    }

    /*
     * 执行 linux 命令
     */
    public static String exec(String cmd) {
        String rst = "";
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((rst = br.readLine()) != null) {
                return rst;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return checkNULL(rst);
    }

    /*
     * 执行 linux 命令，可带大量尝试
     */
    public static String exec_grep(String cmd) {
        String rst = "";
        try {
            ProcessBuilder pb = new ProcessBuilder("sh", "-c", cmd);
            Process p = pb.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((rst = br.readLine()) != null) {
                return rst;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return checkNULL(rst);
    }

    /**
     * 检查 目录是否存在
     */
    public static void checkDir() {
        File file = new File("/sdcard/feedback/");
        if (!file.exists()) {
            Util.exec_grep("mkdir /sdcard/feedback");
            Util.exec_grep("mkdir /sdcard/feedback/recovery");
        }
    }

    /**
     * 将 log 文件 打包成 zip
     * 
     * @param mProgressDialog
     * @param model
     * @param version
     * @param uuid
     */
    public static void getZip(final ProgressDialog mProgressDialog, final String flag, final String model,
            final String version, final String uuid,final String path) {
        // 创建进程 等待 3秒 打包收集数据
        new Thread(new Runnable() {

            public void run() {
                try {
                    Thread.sleep(3000);

                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                    String time = df.format(new Date());

                    String fileName = flag + "_" + model + "_" + version + "_" + uuid + "_" + time;

                    // 打包所有的 log 文件 getFilesDir().getPath()
                    //Util.filesToZip("/sdcard/feedback", "/sdcard/feedback", fileName);
                    Util.filesToZip(path, path, fileName);
                    
                    // 上传 数据
                    Util.uploadZip(mProgressDialog, path,fileName);

                    // mProgressDialog.cancel();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 将存放在sourceFilePath目录下的源文件,打包成fileName名称的ZIP文件,并存放到zipFilePath。
     * 
     * @param sourceFilePath
     *            待压缩的文件路径
     * @param zipFilePath
     *            压缩后存放路径
     * @param fileName
     *            压缩后文件的名称
     * @return flag
     */
    public static boolean filesToZip(String sourceFilePath, String zipFilePath, String fileName) {
        boolean flag = false;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        

        try {
	        File zipFile = new File(zipFilePath + "/" + fileName + ".zip");
	        if (zipFile.exists()) {
	        	Logger.i(zipFilePath + " 目录下存在名字为：" + fileName + ".zip" + " 打包文件");
	        } else {
	        	fos = new FileOutputStream(zipFile);
	        	zos = new ZipOutputStream(new BufferedOutputStream(fos));

	            zip(sourceFilePath,zos,false,"");
	        }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            // 关闭流
            try {
                if (null != zos)
                    zos.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        

        return true;
    }

	private static void zip(String sourceFilePath,  ZipOutputStream zos, boolean sub,String subFolderName) throws IOException{
		
        byte[] bufs = new byte[1024 * 10];

        FileInputStream fis = null;
        BufferedInputStream bis = null;

        File sourceFile = new File(sourceFilePath);
        
        if (sourceFile.exists() == false) {
        	Logger.i("待压缩目录：" + sourceFilePath + " 不存在");
        } else {
            File[] sourceFiles = sourceFile.listFiles();
            if (null == sourceFiles || sourceFiles.length < 1) {
            	Logger.i("待压缩的文件目录：" + sourceFilePath + " 里面不存在文件,无需压缩.");
            } else {
                
                for (int i = 0; i < sourceFiles.length; i++) {

                    //Log.v("shper", "文件：" + sourceFiles[i].getAbsolutePath());
                    
                	if(sourceFiles[i].isDirectory()){
                        //Log.v("shper", "文件1：" + sourceFiles[i].getAbsolutePath());
                        if(sub){
                    		zip(sourceFiles[i].getAbsolutePath(),zos,true,subFolderName+"/"+sourceFiles[i].getName());
                        }else{
                    		zip(sourceFiles[i].getAbsolutePath(),zos,true,sourceFiles[i].getName());
                        }
                	}else{

                        if(!sub){
                    		if(sourceFiles[i].getName().endsWith(".zip")){
                    			continue;
                    		}
                        }

                        //Log.v("shper", "文件2：" + sourceFiles[i].getAbsolutePath());
                        String tmpName = "";
                        if(sub){
                        	//tmpName = sourceFiles[i].getAbsolutePath().substring(0,sourceFiles[i].getAbsolutePath().lastIndexOf("/")+1);
                        	tmpName = subFolderName + "/" + sourceFiles[i].getName();
                        }else{
                        	tmpName = sourceFiles[i].getName();
                        }
                        // 创建ZIP实体,并添加进压缩包
                        Logger.i("tmpName：" + tmpName);
                        ZipEntry zipEntry = new ZipEntry(tmpName);
                        zos.putNextEntry(zipEntry);
                        // 读取待压缩的文件并写进压缩包里
                        fis = new FileInputStream(sourceFiles[i]);
                        bis = new BufferedInputStream(fis, 1024 * 10);
                        int read = 0;
                        while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
                            zos.write(bufs, 0, read);
                        }

                        if (null != bis)
                            bis.close();
                	}
                }
            }
       }

}
    

    /**
     * 上传 zip 包
     * 
     * @param file
     */
    public static void uploadZip(ProgressDialog mProgressDialog,String zipFilePath, String filename) {

        // 获取文件
        Logger.d(filename);
        //File file = new File(Environment.getExternalStorageDirectory(), "/feedback/" + filename + ".zip");

        File file = new File(zipFilePath + "/" + filename + ".zip");
        // File file = new File("/sdcard/feedback/" + filename);

        // 开始上传数据
        PicTask.putPic(null, filename, file);

        // 上传完成 取消 Dialog
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }

        // 删除所有文件
        Logger.d("删除所有 文件");
        file.delete();
        //Util.exec_grep("rm -r /sdcard/feedback/*");
        //Util.exec_grep("rm -r /sdcard/feedback/recovery/*");
    }

    /**
     * 裁剪 获取 getprop 数据
     * 
     * @param str
     * @return
     */
    public static String cutString(String str) {
        if (null == str || "" == str) {
            return "";
        }

        String tmp[] = str.split(":");
        tmp[1] = tmp[1].trim();
        return tmp[1].substring(1, tmp[1].length() - 1);

    }

    /**
     * 检测 字符串是否为空
     * 
     * @param str
     * @return
     */
    public static String checkNULL(String str) {
        if (null == str) {
            return "";
        } else {
            return str;
        }
    }

}
