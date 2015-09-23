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
 * @Version: 0.1 2015��1��13�� C ����<br>
 */
public class Util {

    public static String cpToData(Context ctx,String path,String destpath) {
       cutString(exec_grep("cp " + path + " " + destpath +"update.zip "));

       Toast.makeText(ctx, "�������", Toast.LENGTH_SHORT).show();
	   return "";
    }
    
    public static String delOTA(Context ctx) {
        cutString(exec_grep("rm " + "/data/update.zip "));
        cutString(exec_grep("rm " + "/sdcard/update.zip "));

        Toast.makeText(ctx, "�������", Toast.LENGTH_SHORT).show();
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
     * ִ�� linux ����
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
     * ִ�� linux ����ɴ���������
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
     * ��� Ŀ¼�Ƿ����
     */
    public static void checkDir() {
        File file = new File("/sdcard/feedback/");
        if (!file.exists()) {
            Util.exec_grep("mkdir /sdcard/feedback");
            Util.exec_grep("mkdir /sdcard/feedback/recovery");
        }
    }

    /**
     * �� log �ļ� ����� zip
     * 
     * @param mProgressDialog
     * @param model
     * @param version
     * @param uuid
     */
    public static void getZip(final ProgressDialog mProgressDialog, final String flag, final String model,
            final String version, final String uuid,final String path) {
        // �������� �ȴ� 3�� ����ռ�����
        new Thread(new Runnable() {

            public void run() {
                try {
                    Thread.sleep(3000);

                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                    String time = df.format(new Date());

                    String fileName = flag + "_" + model + "_" + version + "_" + uuid + "_" + time;

                    // ������е� log �ļ� getFilesDir().getPath()
                    //Util.filesToZip("/sdcard/feedback", "/sdcard/feedback", fileName);
                    Util.filesToZip(path, path, fileName);
                    
                    // �ϴ� ����
                    Util.uploadZip(mProgressDialog, path,fileName);

                    // mProgressDialog.cancel();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * �������sourceFilePathĿ¼�µ�Դ�ļ�,�����fileName���Ƶ�ZIP�ļ�,����ŵ�zipFilePath��
     * 
     * @param sourceFilePath
     *            ��ѹ�����ļ�·��
     * @param zipFilePath
     *            ѹ������·��
     * @param fileName
     *            ѹ�����ļ�������
     * @return flag
     */
    public static boolean filesToZip(String sourceFilePath, String zipFilePath, String fileName) {
        boolean flag = false;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        

        try {
	        File zipFile = new File(zipFilePath + "/" + fileName + ".zip");
	        if (zipFile.exists()) {
	        	Logger.i(zipFilePath + " Ŀ¼�´�������Ϊ��" + fileName + ".zip" + " ����ļ�");
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
            // �ر���
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
        	Logger.i("��ѹ��Ŀ¼��" + sourceFilePath + " ������");
        } else {
            File[] sourceFiles = sourceFile.listFiles();
            if (null == sourceFiles || sourceFiles.length < 1) {
            	Logger.i("��ѹ�����ļ�Ŀ¼��" + sourceFilePath + " ���治�����ļ�,����ѹ��.");
            } else {
                
                for (int i = 0; i < sourceFiles.length; i++) {

                    //Log.v("shper", "�ļ���" + sourceFiles[i].getAbsolutePath());
                    
                	if(sourceFiles[i].isDirectory()){
                        //Log.v("shper", "�ļ�1��" + sourceFiles[i].getAbsolutePath());
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

                        //Log.v("shper", "�ļ�2��" + sourceFiles[i].getAbsolutePath());
                        String tmpName = "";
                        if(sub){
                        	//tmpName = sourceFiles[i].getAbsolutePath().substring(0,sourceFiles[i].getAbsolutePath().lastIndexOf("/")+1);
                        	tmpName = subFolderName + "/" + sourceFiles[i].getName();
                        }else{
                        	tmpName = sourceFiles[i].getName();
                        }
                        // ����ZIPʵ��,����ӽ�ѹ����
                        Logger.i("tmpName��" + tmpName);
                        ZipEntry zipEntry = new ZipEntry(tmpName);
                        zos.putNextEntry(zipEntry);
                        // ��ȡ��ѹ�����ļ���д��ѹ������
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
     * �ϴ� zip ��
     * 
     * @param file
     */
    public static void uploadZip(ProgressDialog mProgressDialog,String zipFilePath, String filename) {

        // ��ȡ�ļ�
        Logger.d(filename);
        //File file = new File(Environment.getExternalStorageDirectory(), "/feedback/" + filename + ".zip");

        File file = new File(zipFilePath + "/" + filename + ".zip");
        // File file = new File("/sdcard/feedback/" + filename);

        // ��ʼ�ϴ�����
        PicTask.putPic(null, filename, file);

        // �ϴ���� ȡ�� Dialog
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        }

        // ɾ�������ļ�
        Logger.d("ɾ������ �ļ�");
        file.delete();
        //Util.exec_grep("rm -r /sdcard/feedback/*");
        //Util.exec_grep("rm -r /sdcard/feedback/recovery/*");
    }

    /**
     * �ü� ��ȡ getprop ����
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
     * ��� �ַ����Ƿ�Ϊ��
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
