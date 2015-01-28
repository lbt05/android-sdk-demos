package com.avoscloud.beijing.push.demo.keepalive;

import java.util.HashMap;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.Session;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMReceiver;
import com.avos.avospush.notification.NotificationCompat;

public class ChatConversationMessageReceiver extends AVIMReceiver {

  @Override
  public void onMessageReceipted(Context context, AVIMClient client, AVIMMessage message) {
    System.out.println("onMessageReceipted");
  }


  @Override
  public void onMemberLeft(Context context, AVIMClient client, AVIMConversation conversation,
      List<String> members, String kickedBy) {
    System.out.println("members left from conversation");
  }

  @Override
  public void onMemberJoined(Context context, AVIMClient client, AVIMConversation conversation,
      List<String> members, String invitedBy) {
    System.out.println("members join in conversation");
  }

  @Override
  public void onConnectionPaused(Context context, AVIMClient client) {
    System.out.println("connnection lost");
  }

  @Override
  public void onConnectionResume(Context context, AVIMClient client) {
    System.out.println("connection resume");
  }

  @Override
  public void onKicked(Context context, AVIMClient client, AVIMConversation conversation,
      String kickedBy) {
    System.out.println("kicked from conversation");

  }

  @Override
  public void onInvited(Context context, AVIMClient client, AVIMConversation conversation,
      String operatior) {
    System.out.println("invited to conversation");

  }

  static HashMap<String, MessageListener> sessionMessageDispatchers =
      new HashMap<String, MessageListener>();

  public static void registerSessionListener(String peerId, MessageListener listener) {
    sessionMessageDispatchers.put(peerId, listener);
  }

  public static void unregisterSessionListener(String peerId) {
    sessionMessageDispatchers.remove(peerId);
  }
}
