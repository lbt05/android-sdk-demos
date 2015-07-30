package com.avoscloud.beijing.push.demo.keepalive;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.avos.avoscloud.*;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;

import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationMemberCountCallback;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class PrivateConversationActivity extends Activity
    implements
    OnClickListener {
  public static final String DATA_EXTRA_SINGLE_DIALOG_TARGET = "single_target_peerId";

  String targetPeerId;
  private ImageButton sendBtn;
  private EditText composeZone;
  String currentName;
  String selfId;
  ListView chatList;
  ChatDataAdapter adapter;
  List<AVIMMessage> messages = new LinkedList<AVIMMessage>();
  AVIMConversation currentConversation;
  AVIMMessageHandler messageHandler;
  AVIMClient currentClient;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.heartbeat);
    targetPeerId = this.getIntent().getStringExtra(DATA_EXTRA_SINGLE_DIALOG_TARGET);
    selfId = AVUser.getCurrentUser().getObjectId();
    // 您可以在这里读取本地的聊天记录，并且加载进来。
    // 　我们会在未来加入这些代码

    // 上面这些都是以前的，后面我要加的是新的conversation的
    currentClient = AVIMClient.getInstance(selfId);
    currentConversation = currentClient.getConversation(targetPeerId);
    currentConversation.fetchInfoInBackground(new AVIMConversationCallback() {

      @Override
      public void done(AVException e) {
        if (e != null) {
          e.printStackTrace();
        } else {
          System.out.println("get Info successfully");
        }
      }
    });

    chatList = (ListView) this.findViewById(R.id.avoscloud_chat_list);
    adapter = new ChatDataAdapter(this, messages, selfId);
    chatList.setAdapter(adapter);
    sendBtn = (ImageButton) this.findViewById(R.id.sendBtn);
    composeZone = (EditText) this.findViewById(R.id.chatText);
    selfId = AVUser.getCurrentUser().getObjectId();
    currentName = HTBApplication.lookupName(selfId);

    sendBtn.setOnClickListener(this);
    // currentConversation.queryMessages(new AVIMMessagesQueryCallback() {
    // @Override
    // public void done(List<AVIMMessage> list, AVException e) {
    // messages.addAll(list);
    // adapter.notifyDataSetChanged();
    // }
    // });
    messageHandler = new AVIMMessageHandler() {
      @Override
      public void onMessage(AVIMMessage msg, AVIMConversation conversation, AVIMClient client) {
        if (client.equals(currentClient)
            && conversation.getConversationId().equals(currentConversation.getConversationId())) {
          messages.add(msg);
          adapter.notifyDataSetChanged();
          LogUtil.avlog.d("MSG received");
        } else {
          LogUtil.avlog.d("MSG from another client");
        }
      }

      @Override
      public void onMessageReceipt(AVIMMessage m, AVIMConversation conversation, AVIMClient client) {
        LogUtil.avlog.d("MSG  delivered");
      }
    };
  }

  @Override
  public void onClick(View v) {
    String text = composeZone.getText().toString();

    if (TextUtils.isEmpty(text)) {
      return;
    }
    composeZone.getEditableText().clear();

    final AVIMTextMessage m = new AVIMTextMessage();
    m.setText(text);

    currentConversation.sendMessage(m, AVIMConversation.RECEIPT_MESSAGE_FLAG,
        new AVIMConversationCallback() {

          @Override
          public void done(AVException e) {
            if (e != null) {
              e.printStackTrace();
            } else {
              System.out.println("messageSent");
              adapter.notifyDataSetChanged();
            }
          }
        });
    messages.add(m);
    adapter.notifyDataSetChanged();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
  }

  @Override
  public void onResume() {
    super.onResume();
    AVIMMessageManager.registerMessageHandler(AVIMTextMessage.class, messageHandler);
  }

  @Override
  public void onPause() {
    super.onPause();
    AVIMMessageManager.unregisterMessageHandler(AVIMTextMessage.class, messageHandler);
  }

  private void updateConversationName() {
    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
    final EditText input = new EditText(this);
    alertBuilder.setView(input);
    alertBuilder.setTitle(R.string.conversation_update_name)
        .setPositiveButton("OK", new AlertDialog.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int which) {
            String i = input.getText().toString();
            currentConversation.setName(i);
            currentConversation.updateInfoInBackground(new AVIMConversationCallback() {

              @Override
              public void done(AVException e) {

              }
            });
          }
        }).setNegativeButton("Cancel", new AlertDialog.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int which) {

          }
        });
    alertBuilder.show();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    getMenuInflater().inflate(R.menu.conversation, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_kick:
        currentConversation.kickMembers(Arrays.asList("123"), new AVIMConversationCallback() {

          @Override
          public void done(AVException e) {
            System.out.println("kicked 123");
          }
        });
        return true;
      case R.id.action_invite:
        currentConversation.addMembers(Arrays.asList("123"), new AVIMConversationCallback() {

          @Override
          public void done(AVException e) {
            System.out.println("added 123");
          }
        });

        return true;
      case R.id.action_quit:
        currentConversation.quit(new AVIMConversationCallback() {
          @Override
          public void done(AVException e) {
            onBackPressed();
          }
        });
        return true;
      case R.id.action_get_members:
        Toast.makeText(PrivateConversationActivity.this,
            currentConversation.getMembers().toString(), Toast.LENGTH_SHORT)
            .show();
        return true;
      case R.id.action_update_name:
        this.updateConversationName();
        return true;
      case R.id.action_query_message_history:
        currentConversation.queryMessages(10,
            new AVIMMessagesQueryCallback() {
              @Override
              public void done(List<AVIMMessage> avimMessages, AVException e) {
                if (e != null) {
                  e.printStackTrace();
                } else {
                  messages.addAll(avimMessages);
                  adapter.notifyDataSetChanged();
                }
              }
            });
        break;
      case R.id.action_query_message_history_2:
        AVIMMessage message = messages.get(0);

        currentConversation.queryMessages(message.getMessageId(), message.getTimestamp(), 10,
            new AVIMMessagesQueryCallback() {

              @Override
              public void done(List<AVIMMessage> avimMessages, AVException e) {
                if (e != null) {
                  e.printStackTrace();
                } else {
                  messages.addAll(0,avimMessages);
                  adapter.notifyDataSetChanged();
                }
              }
            });
        break;

      case R.id.action_mockup_location:
        AVIMLocationMessage locationMessage = new AVIMLocationMessage();
        locationMessage.setLocation(new AVGeoPoint(138.4, 34.8));
        currentConversation.sendMessage(locationMessage, new AVIMConversationCallback() {
          @Override
          public void done(AVException e) {
            if (e == null) {
              Toast.makeText(PrivateConversationActivity.this,
                  getResources().getString(R.string.msg_here), Toast.LENGTH_SHORT).show();
            }
          }
        });
        break;
      case R.id.action_query_member_count:
        currentConversation.getMemberCount(new AVIMConversationMemberCountCallback() {
          @Override
          public void done(Integer count, AVException e) {
            if (e == null) {
              Toast.makeText(PrivateConversationActivity.this, "群内有" + count + "人",
                  Toast.LENGTH_SHORT).show();
            }
          }
        });
        break;
      case R.id.action_add_attribute:
        currentConversation.setAttribute("shit", 1);
        currentConversation.updateInfoInBackground(new AVIMConversationCallback() {
          @Override
          public void done(AVException e) {

          }
        });
    }
    return super.onOptionsItemSelected(item);
  }
}
