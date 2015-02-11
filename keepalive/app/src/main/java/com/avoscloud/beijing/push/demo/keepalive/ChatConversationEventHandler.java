package com.avoscloud.beijing.push.demo.keepalive;

import android.widget.Toast;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationEventHandler;

import java.util.List;

/**
 * Created by lbt05 on 1/29/15.
 */
public class ChatConversationEventHandler extends AVIMConversationEventHandler {

  @Override
  public void onMemberLeft(AVIMClient client, AVIMConversation conversation, List<String> members,
      String kickedBy) {
    Toast.makeText(AVOSCloud.applicationContext,
        members + " kicked from:" + conversation.getConversationId() + " by "
            + kickedBy, Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onMemberJoined(AVIMClient client, AVIMConversation conversation,
      List<String> members, String invitedBy) {
    Toast.makeText(AVOSCloud.applicationContext,
        members + " invited to:" + conversation.getConversationId() + " by "
            + invitedBy, Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onKicked(AVIMClient client, AVIMConversation conversation, String kickedBy) {
    Toast.makeText(AVOSCloud.applicationContext,
        "Kicked from:" + conversation.getConversationId() + " by " + kickedBy, Toast.LENGTH_SHORT)
        .show();
  }

  @Override
  public void onInvited(AVIMClient client, AVIMConversation conversation, String invitedBy) {
    Toast.makeText(AVOSCloud.applicationContext,
        "Kicked from:" + conversation.getConversationId() + " by " + invitedBy, Toast.LENGTH_SHORT)
        .show();
  }
}
