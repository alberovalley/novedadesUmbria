package com.alberovalley.utils;

import android.util.Log;

import com.alberovalley.novedadesumbria.utils.AppConstants;

public class AlberoLog {

    /**
     * Wrapper class for Log methods
     * Needs an AppConstants Interface with the LOGTAG constant in it
     */

    // ============================================================================================
    // METHODS
    // ============================================================================================

    public static void v(String message) {
        Log.v(AppConstants.LOGTAG, message);
    }

    public static void v(Object obj, String message) {
        AlberoLog.v(obj.getClass().getSimpleName() + message);
    }

    public static void v(String message, Throwable tr) {
        Log.v(AppConstants.LOGTAG, message, tr);
    }

    public static void v(Object obj, String message, Throwable tr) {
        AlberoLog.v(obj.getClass().getSimpleName() + message, tr);
    }

    public static void d(String message) {
        Log.d(AppConstants.LOGTAG, message);
    }

    public static void d(Object obj, String message) {
        AlberoLog.d(obj.getClass().getSimpleName() + message);
    }

    public static void d(String message, Throwable tr) {
        Log.d(AppConstants.LOGTAG, message, tr);
    }

    public static void d(Object obj, String message, Throwable tr) {
        AlberoLog.d(obj.getClass().getSimpleName() + message, tr);
    }

    public static void i(String message) {
        Log.i(AppConstants.LOGTAG, message);
    }

    public static void i(Object obj, String message) {
        AlberoLog.i(obj.getClass().getSimpleName() + message);
    }

    public static void i(String message, Throwable tr) {
        Log.i(AppConstants.LOGTAG, message, tr);
    }

    public static void i(Object obj, String message, Throwable tr) {
        AlberoLog.i(obj.getClass().getSimpleName() + message, tr);
    }

    public static void w(String message) {
        Log.w(AppConstants.LOGTAG, message);
    }

    public static void w(Object obj, String message) {
        AlberoLog.w(obj.getClass().getSimpleName() + message);
    }

    public static void w(String message, Throwable tr) {
        Log.w(AppConstants.LOGTAG, message, tr);
    }

    public static void w(Object obj, String message, Throwable tr) {
        AlberoLog.w(obj.getClass().getSimpleName() + message, tr);
    }

    public static void e(String message) {
        Log.e(AppConstants.LOGTAG, message);
    }

    public static void e(Object obj, String message) {
        AlberoLog.e(obj.getClass().getSimpleName() + message);
    }

    public static void e(String message, Throwable tr) {
        Log.e(AppConstants.LOGTAG, message, tr);
    }

    public static void e(Object obj, String message, Throwable tr) {
        AlberoLog.e(obj.getClass().getSimpleName() + message, tr);
    }

    public static void wtf(String message) {
        Log.wtf(AppConstants.LOGTAG, message);
    }

    public static void wtf(String message, Throwable tr) {
        Log.wtf(AppConstants.LOGTAG, message, tr);
    }

    public static void wtf(Object obj, String message, Throwable tr) {
        AlberoLog.wtf(obj.getClass().getSimpleName() + message, tr);
    }
}
