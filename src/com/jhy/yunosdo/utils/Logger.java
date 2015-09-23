package com.jhy.yunosdo.utils;

import android.util.Log;

/**
 * @Author: Shper
 * @Description: TODO
 * @Since: JDK 8.0
 * @Version: 0.1 2015å¹?æœ?0æ—?C åˆ›å»º<br>
 */
public class Logger {

    public static final String TAG = "UserFeedback";
    private static final String MYSTR = "[SHPER] ";

    /**
     * Send an INFO log message.
     * 
     * @param paramStr
     */
    public static void i(String paramStr) {
        Log.i(TAG, MYSTR + paramStr);
    }

    /**
     * Send an ERROR log message.
     * 
     * @param paramStr
     * @param tr
     */
    public static void e(String paramStr, Throwable tr) {
        Log.e(TAG, MYSTR + paramStr, tr);
    }

    /**
     * Send an ERROR log message.
     * 
     * @param paramStr
     */
    public static void e(String paramStr) {
        Log.e(TAG, MYSTR + paramStr);
    }

    /**
     * Send an DEBUG log message.
     * 
     * @param paramStr
     */
    public static void d(String paramStr) {
        Log.d(TAG, MYSTR + paramStr);
    }

    /**
     * Send an DEBUG log message.
     * 
     * @param paramStr
     */
    public static void d(Object paramStr) {
        Log.d(TAG, MYSTR + paramStr);
    }

    /**
     * Send an WARN log message.
     * 
     * @param paramStr
     */
    public static void w(String paramStr) {
        Log.w(TAG, MYSTR);
    }

}
