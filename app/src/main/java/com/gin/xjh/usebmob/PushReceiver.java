package com.gin.xjh.usebmob;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import cn.bmob.push.PushConstants;

/**
 * Created by Gin on 2018/2/14.
 */

public class PushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String message = "";
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            String msg = intent.getStringExtra("msg");
            //Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
            JSONTokener jsonTokener = new JSONTokener(msg);
            try {
                JSONObject object = (JSONObject) jsonTokener.nextValue();
                message = object.getString("alert");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {//判断是不是Android8.0
                //里面是错的（Android8.0还是不兼容）
                String id = "1"; // 渠道ID
                String name = "channel";//渠道名字
                int notificationId = 0x1234;
                NotificationChannel channel = new NotificationChannel(id,name,NotificationManager.IMPORTANCE_DEFAULT);
                channel.enableLights(true); //是否在桌面icon右RED上角展示小红点
                channel.setLightColor(Color.RED); //小红点颜色
                channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                manager.createNotificationChannel(channel);

                Notification.Builder builder = new Notification.Builder(context,id); //与id对应
                //icon title text必须包含，不然影响桌面图标小红点的展示
                builder.setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("BmobTest")
                        .setContentText(message)
                        .setNumber(3); //久按桌面图标时允许的此条通知的数量
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(notificationId, builder.build());
            }
            else{
                //显示Notification(Android8.0以下）
                //使用通知推送消息
                Notification.Builder builder = new Notification.Builder(context);
                //设置消息属性
                //必须设置的属性：小图标 标题 内容
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setContentTitle("BmobTest");
                builder.setContentText(message);

                //创建一个通知对象
                Notification notification = builder.build();
                //使用通知管理器发送一条通知
                manager.notify(1,notification);
            }
        }
    }
}
