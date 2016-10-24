package com.dalimao.library.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;



import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.MEDIA_MOUNTED;


/**
 * Created by liuguangli on 15/12/17.
 */
public class DeviceInfoUtil {
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    public static final int PORTRAIT = 1;
    public static final int LANDSCAPE = 2;
    private static final String TAG = "DeviceInfoUtil";
    private static  DisplayMetrics displayMetrics;
    private static String sExternalSdCardPath = null;
    private static String imei = "";

    private static String imsi = "";
    private static String exSdCardPath = "";
    public static DisplayMetrics getScreenSize(Context context){
        if (displayMetrics == null){
            try {

                displayMetrics = new DisplayMetrics();
                displayMetrics.widthPixels = 1280;
                displayMetrics.heightPixels = 720;
                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                wm.getDefaultDisplay().getMetrics(displayMetrics);
            } catch (Exception e){
                Log.e(TAG,e.getMessage());
            }

        }
        return displayMetrics;
    }

    public static float getDesity(Context context){
        getScreenSize(context);
        return displayMetrics.density;
    }

    public static int getScreenLongSize(Context context){
        getScreenSize(context);
        return Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }
    public static int getScreenShortSize(Context context){
        getScreenSize(context);
        return Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }
    public static int getOrientation(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics.widthPixels = 1280;
        displayMetrics.heightPixels = 720;
        try {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(displayMetrics);
        } catch (Exception e){
            //do nothing
        }

        if (displayMetrics.heightPixels>displayMetrics.widthPixels){
            return PORTRAIT;
        } else {
            return LANDSCAPE;
        }
    }
    /**
     * @return
     */
    public static int getSystemVersion(){

        return Build.VERSION.SDK_INT;
    }
    /**
     */
    public static String getBuildModel(){

        return Build.MANUFACTURER+Build.MODEL;
    }

    /**
     * @param context 获取sd卡路径
     * @return
     */
    public static String getExternalSDCardPath(Context context) {
        // ArrayList<String> paths = (ArrayList<String>) getExtSDCardPaths();
        // if (paths.size() != 0)
        // return paths.get(0) + "/";
        if (sExternalSdCardPath != null)
            return sExternalSdCardPath;
        String externalStorageState;
        File externalDir = null;// 获取手机根目录
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        }
        if (MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
            externalDir = Environment.getExternalStorageDirectory();
        }
        String path = null;
        if (externalDir == null || !externalDir.exists() || !externalDir.canWrite()) {
            externalDir = context.getCacheDir();
            if (externalDir != null) {
                path = externalDir.getPath();
                path = path.endsWith("/") ? path : path + "/";
                sExternalSdCardPath = path;
            }
        } else {
            path = externalDir.getPath();
            path = path.endsWith("/") ? path : path + "/";
            sExternalSdCardPath = path;
        }
        // 如果还为空
//         fix 静态代码扫描 zhengyx 2016.03.15
        if (TextUtils.isEmpty(sExternalSdCardPath)) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            sExternalSdCardPath = cacheDirPath;
        }
        Log.d(TAG, "sExternalSdCardPath=" + sExternalSdCardPath);
        return sExternalSdCardPath;
    }
    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    public static String getDeviceType(Context context) {
        String phoneString = Build.MODEL;
        return phoneString;
    }

    public static String getSystemVersionRelease() {
        String systemVersion = Build.VERSION.RELEASE;
        return systemVersion;
    }

    public static String getLocalMacAddress(Context context) {
        if (context == null) {
            return null;
        }
        //1.9.2 bigfix NullPointerException 防止由于某些系统getSystemService不稳定导致activityManager为null情况
        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            return info.getMacAddress();
        }catch (RuntimeException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    public static String getIMSI(Context context) {
        if(context == null){
            return null;
        }
        if(TextUtils.isEmpty(imsi)) {
            TelephonyManager mTelephonyMgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            try { //Android 6.0异常捕获
                imsi = mTelephonyMgr.getSubscriberId();
            }catch (SecurityException e){
                Log.e(TAG,e.getMessage());
            }
        }
        return imsi;
    }



    public static String getIMEI(Context context) {
        if (context == null) {
            return null;
        }
        if(TextUtils.isEmpty(imei)) {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            try {  //Android 6.0异常捕获
                imei = tm.getDeviceId();
            }catch (SecurityException e){
                Log.e(TAG,e.getMessage());
            }
        }
        return imei;
    }
    /**
     * 获得外部sdcard存储位置
     *
     * @return
     * @author tiandp 2015-5-28
     */
    public static List<String> getExtSDCardPaths() {
        List<String> paths = new ArrayList<String>();
        String extFileStatus = Environment.getExternalStorageState();
        File extFile = Environment.getExternalStorageDirectory();
        if (extFileStatus.endsWith(Environment.MEDIA_UNMOUNTED) && extFile.exists()
                && extFile.isDirectory() && extFile.canWrite()) {
            paths.add(extFile.getAbsolutePath());
        }
        try {
            // obtain executed result of command line code of 'mount', to judge
            // whether tfCard exists by the result
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("mount");
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            int mountPathIndex = 1;
            while ((line = br.readLine()) != null) {
                // format of sdcard file system: vfat/fuse
                if ((!line.contains("fat") && !line.contains("fuse") && !line.contains("storage"))
                        || line.contains("secure") || line.contains("asec")
                        || line.contains("firmware") || line.contains("shell")
                        || line.contains("obb") || line.contains("legacy") || line.contains("data")) {
                    continue;
                }
                String[] parts = line.split(" ");
                int length = parts.length;
                if (mountPathIndex >= length) {
                    continue;
                }
                String mountPath = parts[mountPathIndex];
                if (!mountPath.contains("/") || mountPath.contains("data")
                        || mountPath.contains("Data")) {
                    continue;
                }
                File mountRoot = new File(mountPath);
                if (!mountRoot.exists() || !mountRoot.isDirectory() || !mountRoot.canWrite()) {
                    continue;
                }
                boolean equalsToPrimarySD = mountPath.equals(extFile.getAbsolutePath());
                if (equalsToPrimarySD) {
                    continue;
                }
                paths.add(mountPath);
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException:" + e.getMessage());
        }
        return paths;
    }
    public static String getExSdCardPath() {
        if("".equals(exSdCardPath)){
            ArrayList<String> paths = (ArrayList<String>) getExtSDCardPaths();
            if (paths.size() != 0) {
                exSdCardPath = paths.get(0) + "/";
            }else{
                exSdCardPath = null;
            }
        }

        return exSdCardPath;
    }
    /****************************** pushconfig *********************************************/
    /**
     * 分辨率
     *
     * @param
     * @param
     * @return
     * @exception/throws
     * @see
     */
    public static Point getScreenPixed(Context context) {
        if (context == null) return null;
        if (displayMetrics == null){
             displayMetrics = context.getResources().getDisplayMetrics();
        }
        Point point = new Point();

        point.x = displayMetrics.widthPixels;
        point.y = displayMetrics.heightPixels;
        return point;
    }

    public static String getCpuInfno() {
        String arch = "";// cpu类型
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method get = clazz.getDeclaredMethod("get", new Class[]{
                    String.class
            });
            arch = (String) get.invoke(clazz, new Object[]{
                    "ro.product.cpu.abi"
            });
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }
        return arch;
    }

    public static long getFreeMemory(Context context) {

        // 获取android当前可用内存大小
        long fm = 0;
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            fm = mi.availMem/(1024*1024);
        } catch (Exception e) {

        }

        return fm;
    }

    public static String getSmsNo(Context context) {
        //获取手机号码

        String tel  = null;
        try {
            TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            tel =  tm.getLine1Number();//获取本机号码
        } catch (Exception e) {

        }
        return tel;
    }
}
