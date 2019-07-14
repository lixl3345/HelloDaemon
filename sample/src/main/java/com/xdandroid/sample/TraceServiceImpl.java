package com.xdandroid.sample;

import android.content.*;
import android.content.pm.PackageManager;
import android.os.*;
import android.util.Log;
import android.widget.Toast;

import com.xdandroid.hellodaemon.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

import io.reactivex.*;
import io.reactivex.disposables.*;

public class TraceServiceImpl extends AbsWorkService {

    //是否 任务完成, 不再需要服务运行?
    public static boolean sShouldStopService;
    public static Disposable sDisposable;

    private DakaInfo dakaInfo=new DakaInfo("NULL",0);
    //  my add

    private boolean isRunDk=false;
    public static  String timeLabel="0812";
    private int cfCount=2;//触发次数

    private Intent intent2 = new Intent("com.xdandroid.sample.sendmsg");

    public static  int waitTime=10;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;

    List<String> msgList = new ArrayList<>();


    public static void stopService() {
        //我们现在不再需要服务运行了, 将标志位置为 true
        sShouldStopService = true;
        //取消对任务的订阅
        if (sDisposable != null) sDisposable.dispose();
        //取消 Job / Alarm / Subscription
        cancelJobAlarmSub();
    }

    /**
     * 是否 任务完成, 不再需要服务运行?
     * @return 应当停止服务, true; 应当启动服务, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean shouldStopService(Intent intent, int flags, int startId) {
        return sShouldStopService;
    }



    @Override
    public void startWork(Intent intent, int flags, int startId) {
        addMsg("开始");
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);

        sDisposable = Observable
                .interval(5, TimeUnit.SECONDS)
                //取消任务时取消定时唤醒
                .doOnDispose(() -> {
                    addMsg("停止服务。");
                    cancelJobAlarmSub();
                })
                .subscribe(count -> {
                  //  System.out.println("每 3 秒执行： " +timeLabel);
                    SimpleDateFormat d = new SimpleDateFormat("HHmm");
                    String dd = d.format(new Date());
                     if(timeLabel!=null&&!"".equals(timeLabel)){
                         if(isTime()){
                             addMsg("时间到，执行操作");
                             dk();
                         }
                     }else{
                         addMsg("时间标签有问题");
                     }
                     sendMsg(getMsg());
                });
    }

    private void addMsg(String msg) {
        if("".equals(msg)||msg==null){
            return;
        }
        if (msgList.size() > 20) {
            msgList.remove(0);
        }
        SimpleDateFormat d = new SimpleDateFormat("HH:mm:ss");
        msg=d.format(new Date())+":"+msg;
        Log.d("addMsg",msg);
        msgList.add(msg);
    }

    private String getMsg(){
        String msg="";
        for(int i=0;i<msgList.size();i++){
            String m=msgList.get(i)+"\n";
            msg=msg+m;
        }
        return msg;
    }

    /**
     * 判断是否到时间了
     * @return
     */
    private boolean isTime(){
        boolean isTime=false;
        SimpleDateFormat d = new SimpleDateFormat("HHmm");
        String dd = d.format(new Date());
        int nowdd=Integer.parseInt(dd);
        String []times=timeLabel.split(",|，");
        String msg="";
        for(String t:times){
            String[] ts=t.split("-");
            if(nowdd>=Integer.parseInt(ts[0])&&
                    nowdd<=Integer.parseInt(ts[1])  ){
                isTime=true;
                if(dakaInfo.getTimeLabel().equals(t)){//如果上一次执行的是这个时间段
                    //addMsg("这个时间段已经执行过一次了");
                    dakaInfo.setCount(dakaInfo.getCount()+1);
                    if(dakaInfo.getCount()<=cfCount){
                        msg=(t+"执行第"+dakaInfo.getCount()+"次");
                    }else{
                        isTime=false;
                        msg=(t+"执行了"+cfCount+"次后不执行");
                    }
                }else{ //如果这个时间段还未执行
                    dakaInfo.setTimeLabel(t);
                    dakaInfo.setCount(1);
                }
                break;
            }else{
                msg="没到点"+timeLabel;
            }
        }
        addMsg(msg);
        return isTime;
    }
    @Override
    public void stopWork(Intent intent, int flags, int startId) {
        stopService();
    }

    /**
     * 任务是否正在运行?
     * @return 任务正在运行, true; 任务当前不在运行, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean isWorkRunning(Intent intent, int flags, int startId) {
        //若还没有取消订阅, 就说明任务仍在运行.
        return sDisposable != null && !sDisposable.isDisposed();
    }

    @Override
    public IBinder onBind(Intent intent, Void v) {
        return null;
    }

    @Override
    public void onServiceKilled(Intent rootIntent) {
        System.out.println("停止服务。");
    }



//    //亮屏  我的手机需要设置-安全-设备管理器 里面激活该程序
//    public void screenOn() {
//        // turn on screen
//        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
//        mWakeLock.acquire();
//        mWakeLock.release();
//        //  handler.sendEmptyMessageDelayed(MESSAGE_SCREEN_OFF,1000);
//    }

//    //息屏  我的手机需要设置-安全-设备管理器 里面激活该程序
//    private void screenOff() {
//        boolean admin = policyManager.isAdminActive(adminReceiver);
//        if (admin) {
//            policyManager.lockNow();
//        } else {
//           System.out.println("没有设备管理权限");
//        }
//    }

    private void sendMsg(String msg){
        intent2.putExtra("msg", msg);
        sendBroadcast(intent2);
    }
    private void dk(){
        isRunDk=true;
        try {
            addMsg("亮屏");
            screenOn();
            Thread.sleep(1000l);
            addMsg("打开软件");
            openDD();
            Thread.sleep(waitTime*1000);
            addMsg("回到界面");
            sendMsg("setTopApp");
        }catch (Exception e){
            e.printStackTrace();
        }
        isRunDk=false;
    }
    //打开钉钉软件
    private void openDD() {

        PackageManager packageManager = getPackageManager();
        String packageName = "com.alibaba.android.rimet";//要打开应用的包名,以钉钉为例
        Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(packageName);
        if (launchIntentForPackage != null) {
            startActivity(launchIntentForPackage);
        } else {
            addMsg("手机未安装该应用");
        }
    }


    //亮屏  我的手机需要设置-安全-设备管理器 里面激活该程序
    public void screenOn() {
        // turn on screen
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
        mWakeLock.acquire();
        mWakeLock.release();
        //  handler.sendEmptyMessageDelayed(MESSAGE_SCREEN_OFF,1000);
    }
}
