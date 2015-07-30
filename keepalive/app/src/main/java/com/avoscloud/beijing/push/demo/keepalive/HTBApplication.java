package com.avoscloud.beijing.push.demo.keepalive;

import java.util.HashMap;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.Session;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;

/**
 * Created by nsun on 4/28/14.
 */
public class HTBApplication extends Application {

  private static HashMap<String, String> userNameCache = new HashMap<String, String>();

  @Override
  public void onCreate() {
    super.onCreate();

    // 必需：初始化你的appid和appkey，保存installationid
    AVOSCloud.initialize(this, "sax3bkbtwimgnpg6e44cm1uquurfgt8dwycjstw2yv09gw2b",
        "a5pidc56v6b605dlbzi3t4x5sz8e163k2s7ldy8bdn4qghj0");
    AVInstallation.getCurrentInstallation().saveInBackground();
    PushService.setDefaultPushCallback(this, MainActivity.class);
    AVOSCloud.setDebugLogEnabled(true);

    // 设置签名
    AVIMClient.setSignatureFactory(new KeepAliveSignatureFactory());

    AVIMClient.setClientEventHandler(new KeepAliveClientEventHandler());

    // 设置默认的消息处理单元
    AVIMMessageManager.registerDefaultMessageHandler(new AVIMMessageHandler() {
      @Override
      public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
        NotificationManager nm =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String ctnt = null;
        if (message instanceof AVIMTextMessage) {
          ctnt =
              HTBApplication.lookupName(message.getFrom()) + "："
                  + ((AVIMTextMessage) message).getText();
        } else if (message instanceof AVIMLocationMessage) {
          ctnt = HTBApplication.lookupName(message.getFrom()) + " sent you a location message";
        } else {
          ctnt = HTBApplication.lookupName(message.getFrom()) + message.getContent();
        }

        System.out.println(client.getClientId() + " get message");
        Intent resultIntent =
            new Intent(getApplicationContext(), PrivateConversationActivity.class);
        resultIntent.putExtra(PrivateConversationActivity.DATA_EXTRA_SINGLE_DIALOG_TARGET,
            message.getConversationId());
        resultIntent.putExtra(Session.AV_SESSION_INTENT_DATA_KEY, message);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

        PendingIntent pi =
            PendingIntent.getActivity(getApplicationContext(), -1, resultIntent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification notification =
            new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.notif_title))
                .setContentText(ctnt)
                .setContentIntent(pi)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(
                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setAutoCancel(true).build();
        nm.notify(233, notification);
        LogUtil.avlog.d("notification sent");
      }

      public void onMessageReceipt(AVIMMessage message, AVIMConversation conversation) {

      }
    });

    AVIMMessageManager.setConversationEventHandler(new ChatConversationEventHandler());
  }

  public static String lookupName(String peerId) {
    return userNameCache.get(peerId);
  }

  public static void registerLocalNameCache(String peerId, String name) {
    userNameCache.put(peerId, name);
  }

  @Override
  public void onTerminate() {
    LogUtil.avlog.d("xiao mi want to kill me, help me !");
    super.onTerminate();
  }


}
