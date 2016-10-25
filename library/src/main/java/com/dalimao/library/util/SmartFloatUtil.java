
package com.dalimao.library.util;


import android.app.AppOpsManager;

import android.content.Context;

import android.os.Binder;


import android.util.Log;
import android.view.WindowManager;




import java.lang.reflect.Method;

/**
 * 浮窗入口判断相关逻辑
 * Created by liuguangli on 16/3/2.
 */
public class SmartFloatUtil {
    private static final String TAG = "FloatWinEnter";
    public static final int HAS_AUTHOR= 1;
    private static final int NOT_AUTHOR = 2;
    private static final int NOT_SUPPORT_AUTHOR = 3;
    private static final String[] INPUT_METHOD_SPECIAL_MODELS = {"SAMSUNG"};;
    private static Integer[] specialVersions;
    public static final String SPECIAL_MODELS[] = {
            "MI",
            "XIAOMI",
            "HUAWEI",
            "OPPO",
    };
    /**
     * 判断当前是否特殊机型
     * @return
     */
    public static boolean isSpecialDevice(){
        String specialDevices[] = getSpecialDevices();
        String currentDevice = DeviceInfoUtil.getBuildModel();
        if (currentDevice == null){
            return false;
        }
        currentDevice = currentDevice.toUpperCase();
        for (String d:specialDevices){
            if (currentDevice.contains(d)){
                return true;
            }
        }
        return false;
    }

    private static String[] getSpecialDevices() {
        return SPECIAL_MODELS;
    }

    /**
     * 判断是否是特殊版本（14～17）
     * @return
     */
    public static boolean isSpecialVersion() {
        int systemVersion = DeviceInfoUtil.getSystemVersion();

        return systemVersion >= 14 && systemVersion <= 18;
    }

    /**
     * 判断是否开启浮窗权限
     * @return
     */
    public static int hasAuthorFloatWin(Context context){

        if (DeviceInfoUtil.getSystemVersion() < 19){
            return NOT_SUPPORT_AUTHOR;
        }
        try {
            AppOpsManager appOps = (AppOpsManager)context.getSystemService(Context.APP_OPS_SERVICE);
            Class c = appOps.getClass();
            Class[] cArg = new Class[3];
            cArg[0] = int.class;
            cArg[1] = int.class;
            cArg[2] = String.class;
            Method lMethod = c.getDeclaredMethod("checkOp", cArg);
            if (AppOpsManager.MODE_ALLOWED == (Integer) lMethod.invoke(appOps, 24, Binder.getCallingUid(), context.getPackageName())){
                return HAS_AUTHOR;
            } else {
                return NOT_AUTHOR;
            }

        } catch(Exception e) {
           return NOT_SUPPORT_AUTHOR;
        }

    }




    /**
     * 交易猫启动游戏浮窗启动(包含探测流程)
     * @param context
     *
     */
    public static int askType(final Context context){
        Log.d(TAG, "SharedStaticField.HOST_VISIBLE = false;");
        int winType = WindowManager.LayoutParams.TYPE_TOAST;
        if (isSpecialVersion()){
            //Android 4.4 以下,google api 来看这个时候还没有对SYSTEM_PHONE类型的浮窗进行权限管理，使用 TYPE_PHONE
            winType = WindowManager.LayoutParams.TYPE_PHONE;

        } else {

            /**
             * Android 4.4 以上,有的厂商开始对SYSTEM_PHONE进行权限控制.
             * 好在Android 4.4 可以使用TOAST类型浮窗实现绕过权限,但是TOAST类型浮窗无法定位输入法,只能全屏幕显示输入法.
             * 方案:对特殊机型进行SYSTEM_PHONE权限检查,若已经授权使用SYSTEM_PHONE,若未授权使用TOAST类型浮窗
             */

            if ((isSpecialDevice() && hasAuthorFloatWin(context) == HAS_AUTHOR)){
                winType = WindowManager.LayoutParams.TYPE_PHONE;

            } else if (isSpecialDevice() && hasAuthorFloatWin(context) != HAS_AUTHOR){
                //特殊继续未授权
                winType = WindowManager.LayoutParams.TYPE_TOAST;
            } else if (isInputMethodSpecialDevice()) {
                //这种机型的Toast压根不支持输入法了,必须使用TYPE_PHONE
                Log.e(TAG, "注意浮窗对输入法支持不友好");
                winType = WindowManager.LayoutParams.TYPE_TOAST;

            }
            Log.d(TAG, "启动"+winType+"方式的浮窗");
            
        }
        return winType;
    }

    


    

    private static boolean isInputMethodSpecialDevice() {
        String specials[] = INPUT_METHOD_SPECIAL_MODELS;
        String currentDevice = DeviceInfoUtil.getBuildModel();
        if (currentDevice != null){
            currentDevice = currentDevice.toUpperCase();
        }
        for (String dev: specials){
            if (currentDevice.contains(dev)){
                return  true;
            }
        }
        return false;
    }








}
