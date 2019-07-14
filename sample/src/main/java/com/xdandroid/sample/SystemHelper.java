package com.xdandroid.sample;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * 系统帮助类
 */
public class SystemHelper {

    public final static   String MSG_PKG="com.xdandroid.sample.sendmsg";

//    /**
//     * 判断本地是否已经安装好了指定的应用程序包
//     *
//     * @param packageNameTarget ：待判断的 App 包名，如 微博 com.sina.weibo
//     * @return 已安装时返回 true,不存在时返回 false
//     */
//    public static boolean appIsExist(Context context, String packageNameTarget) {
//        if (!"".equals(packageNameTarget.trim())) {
//            PackageManager packageManager = context.getPackageManager();
//            List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES);
//            for (PackageInfo packageInfo : packageInfoList) {
//                String packageNameSource = packageInfo.packageName;
//                if (packageNameSource.equals(packageNameTarget)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
    /**
     * 将本应用置顶到最前端
     * 当本应用位于后台时，则将它切换到最前端
     *
     * @param context
     */
    public static void setTopApp(Context context) {
        if (!isRunningForeground(context)) {
            /**获取ActivityManager*/
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

            /**获得当前运行的task(任务)*/
            List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(100);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                /**找到本应用的 task，并将它切换到前台*/
                if (taskInfo.topActivity.getPackageName().equals(context.getPackageName())) {
                    activityManager.moveTaskToFront(taskInfo.id, 0);
                    break;
                }
            }
        }
    }
//
//    /**
//     * 判断本应用是否已经位于最前端
//     *
//     * @param context
//     * @return 本应用已经位于最前端时，返回 true；否则返回 false
//     */
    public static boolean isRunningForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
        /**枚举进程*/
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfoList) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName.equals(context.getApplicationInfo().processName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 隐藏软键盘(只适用于Activity，不适用于Fragment)
     */
    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 隐藏软键盘(可用于Activity，Fragment)
     */
    public static void hideSoftKeyboard(Context context, View ... viewList) {
        if (viewList == null) return;

        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);

        for (View v : viewList) {
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void getFocus(View v){
        v.setFocusable(true);
        v.requestFocus();
        v.setFocusableInTouchMode(true);
        v.requestFocusFromTouch();
    }
}
