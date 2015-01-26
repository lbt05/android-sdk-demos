package com.avoscloud.beijing.push.demo.keepalive;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.*;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationCallback;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.MessageHandler;

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
    OnClickListener,
    MessageHandler {
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

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.heartbeat);
    targetPeerId = this.getIntent().getStringExtra(DATA_EXTRA_SINGLE_DIALOG_TARGET);

    // 您可以在这里读取本地的聊天记录，并且加载进来。
    // 　我们会在未来加入这些代码

    // 上面这些都是以前的，后面我要加的是新的conversation的
    AVIMClient client = AVIMClient.getInstance(selfId);
    currentConversation = client.getConversation(targetPeerId);
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
    adapter = new ChatDataAdapter(this, messages);
    chatList.setAdapter(adapter);
    sendBtn = (ImageButton) this.findViewById(R.id.sendBtn);
    composeZone = (EditText) this.findViewById(R.id.chatText);
    selfId = AVUser.getCurrentUser().getObjectId();
    currentName = HTBApplication.lookupname(selfId);

    sendBtn.setOnClickListener(this);
    if (getIntent().getExtras().getParcelable(Session.AV_SESSION_INTENT_DATA_KEY) != null) {
      AVIMMessage msg = getIntent().getExtras().getParcelable(Session.AV_SESSION_INTENT_DATA_KEY);
      messages.add(msg);
      adapter.notifyDataSetChanged();
    }

  }

  @Override
  public void onClick(View v) {
    String text = composeZone.getText().toString();

    if (TextUtils.isEmpty(text)) {
      return;
    }
    composeZone.getEditableText().clear();

    final AVIMMessage m = new AVIMMessage();
    m.setContent(text);
    currentConversation.sendMessage(m, AVIMConversation.NONTRANSIENT_MESSAGE_FLAG,
        new AVIMConversationCallback() {

          @Override
          public void done(AVException e) {
            if (e != null) {
              e.printStackTrace();
            } else {
              System.out.println("messageSent");
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
    AVIMMessageManager.registerMessageHandler(AVIMMessage.class, this);
  }

  @Override
  public void onPause() {
    super.onPause();
    AVIMMessageManager.registerMessageHandler(AVIMMessage.class, this);
  }

  @Override
  public void onMessage(AVIMMessage msg) {
    messages.add(msg);
    adapter.notifyDataSetChanged();
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
            Map<String, Object> info = currentConversation.getInfo();
            info.put("name", i);
            currentConversation.setInfo(info);
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
        Toast.makeText(this, currentConversation.getMembers().toString(), Toast.LENGTH_SHORT)
            .show();
        return true;
      case R.id.action_update_name:
        this.updateConversationName();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
