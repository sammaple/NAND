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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * @Author: Shper
 * @Description: TODO
 * @Since: JDK 8.0
 * @Version: 0.1 2015��1��13�� C ����<br>
 */
public class Util {

    /*
     * ro.product.model
     */
    public static String cpToData(Context ctx,String path) {
       cutString(exec_grep("cp " + path + " /data/update.zip "));

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
        if (file.exists()) {
            Util.exec_grep("mkdir /sdcard/feedback");
        }
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
        File sourceFile = new File(sourceFilePath);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;

        if (sourceFile.exists() == false) {
            Log.v("shper", "��ѹ��Ŀ¼��" + sourceFilePath + " ������");
        } else {
            try {
                File zipFile = new File(zipFilePath + "/" + fileName + ".zip");
                if (zipFile.exists()) {
                    Log.v("shper", zipFilePath + " Ŀ¼�´�������Ϊ��" + fileName + ".zip" + " ����ļ�");
                } else {
                    File[] sourceFiles = sourceFile.listFiles();
                    if (null == sourceFiles || sourceFiles.length < 1) {
                        Log.v("shper", "��ѹ�����ļ�Ŀ¼��" + sourceFilePath + " ���治�����ļ�,����ѹ��.");
                    } else {
                        fos = new FileOutputStream(zipFile);
                        zos = new ZipOutputStream(new BufferedOutputStream(fos));
                        byte[] bufs = new byte[1024 * 10];
                        for (int i = 0; i < sourceFiles.length; i++) {
                            // ����ZIPʵ��,����ӽ�ѹ����
                            ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());
                            zos.putNextEntry(zipEntry);
                            // ��ȡ��ѹ�����ļ���д��ѹ������
                            fis = new FileInputStream(sourceFiles[i]);
                            bis = new BufferedInputStream(fis, 1024 * 10);
                            int read = 0;
                            while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
                                zos.write(bufs, 0, read);
                            }
                        }
                        flag = true;
                    }
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
                    if (null != bis)
                        bis.close();
                    if (null != zos)
                        zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }

        return flag;
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
