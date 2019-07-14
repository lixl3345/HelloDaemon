package com.xdandroid.sample;

import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.*;
import android.view.*;
import android.widget.TextView;

import com.xdandroid.hellodaemon.*;

public class MainActivity extends Activity {


    private MsgReceiver msgReceiver;
    private Intent mIntent;

    TextView timeLabel;
    TextView waitTime;
    TextView msgTextView;
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        timeLabel=findViewById(R.id.timeLabel);

        waitTime=findViewById(R.id.waitTime);
        msgTextView=findViewById(R.id.msg);
        msgReceiver = new MsgReceiver();
        TraceServiceImpl.timeLabel=timeLabel.getText().toString();
        TraceServiceImpl.waitTime=Integer.parseInt(waitTime.getText().toString());


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.xdandroid.sample.sendmsg");
        registerReceiver(msgReceiver, intentFilter);



    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                TraceServiceImpl.sShouldStopService = false;
                TraceServiceImpl.timeLabel=timeLabel.getText().toString();
                TraceServiceImpl.waitTime=Integer.parseInt(waitTime.getText().toString());
                DaemonEnv.startServiceMayBind(TraceServiceImpl.class);

                break;
            case R.id.btn_white:
                IntentWrapper.whiteListMatters(this, "轨迹跟踪服务的持续运行");
                break;
            case R.id.btn_stop:
                TraceServiceImpl.stopService();
                break;
        }
    }

    //防止华为机型未加入白名单时按返回键回到桌面再锁屏后几秒钟进程被杀
    public void onBackPressed() {
        IntentWrapper.onBackPressed(this);
    }

    /**
     * 广播接收器
     * @author len
     *
     */
    public class MsgReceiver extends BroadcastReceiver  {

        @Override
        public void onReceive(Context context, Intent intent) {
            //拿到进度，更新UI
            String msg = intent.getStringExtra("msg");
            System.out.println("activity收到消息：" + msg);
            if("setTopApp".equals(msg)){
                SystemHelper.setTopApp(MainActivity.this);
            }else{
                msgTextView.setText(msg);
            }
        }

    }


}
