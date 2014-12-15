package com.avoscloud.beijing.push.demo.keepalive;

import java.util.LinkedList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.*;
import com.avoscloud.beijing.push.demo.keepalive.data.ChatDemoMessage;
import com.avoscloud.beijing.push.demo.keepalive.data.ChatDemoMessage.MessageType;

import android.app.Activity;
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

public class GroupChatActivity extends Activity implements OnClickListener, MessageListener {
  public static final String DATA_EXTRA_SINGLE_DIALOG_TARGET = "single_target_peerId";

  String groupId;
  private ImageButton sendBtn;
  private EditText composeZone;
  String currentName;

  ListView chatList;
  ChatDataAdapter adapter;
  List<ChatDemoMessage> messages = new LinkedList<ChatDemoMessage>();
  Group group;
  String selfId;

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.heartbeat);
    groupId = this.getIntent().getStringExtra(DATA_EXTRA_SINGLE_DIALOG_TARGET);
    this.setTitle(HTBApplication.lookupname(groupId));

    chatList = (ListView) this.findViewById(R.id.avoscloud_chat_list);
    adapter = new ChatDataAdapter(this, messages);
    chatList.setAdapter(adapter);
    sendBtn = (ImageButton) this.findViewById(R.id.sendBtn);
    composeZone = (EditText) this.findViewById(R.id.chatText);
    selfId = AVUser.getCurrentUser().getObjectId();
    currentName = HTBApplication.lookupname(selfId);
    group = SessionManager.getInstance(selfId).getGroup(groupId);

    // 您可以在这里读取本地的聊天记录，并且加载进来。
    // 　我们会在未来加入这些代码
    sendBtn.setOnClickListener(this);
    if (!AVUtils.isBlankString(getIntent().getExtras()
        .getString(Session.AV_SESSION_INTENT_DATA_KEY))) {
      String msg = getIntent().getExtras().getString(Session.AV_SESSION_INTENT_DATA_KEY);
      ChatDemoMessage message = JSON.parseObject(msg, ChatDemoMessage.class);
      messages.add(message);
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
    ChatDemoMessage message = new ChatDemoMessage();
    message.setMessageContent(text);
    message.setMessageType(MessageType.Text);
    message.setMessageFrom(currentName);
    group.sendMessage(message.makeMessage());
    messages.add(message);
    adapter.notifyDataSetChanged();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case R.id.action_kick:
        AVObject groupObject =
            AVObject.createWithoutData(ChatGroupListFragment.GROUP_TABLE_NAME, this.groupId);
        groupObject.fetchInBackground(new GetCallback<AVObject>() {

          @Override
          public void done(AVObject object, AVException e) {
            if (e == null) {
              List<String> joinedMember = object.getList("m");
              joinedMember.remove(selfId);
              System.out.println(joinedMember);
              group.kickMember(joinedMember);
            } else {
              Toast.makeText(GroupChatActivity.this, "查询异常", Toast.LENGTH_SHORT).show();
            }
          }
        });
        return true;
      case R.id.action_invite:
        AVQuery<AVUser> q = AVUser.getQuery();
        q.orderByDescending("updatedAt");
        q.setLimit(2);
        q.findInBackground(new FindCallback<AVUser>() {

          @Override
          public void done(List<AVUser> parseObjects, AVException parseException) {
            if (parseException == null) {
              List<String> inviteList = new LinkedList<String>();
              for (AVObject o : parseObjects) {
                if (!selfId.equals(o.getString("objectId"))) {
                  inviteList.add(o.getString("objectId"));
                }
              }
              System.out.println(inviteList);
              group.inviteMember(inviteList);
            } else {
              Toast.makeText(GroupChatActivity.this, "查询异常", Toast.LENGTH_SHORT).show();
            }
          }
        });
        return true;
      case R.id.action_quit:
        group.quit();
        this.onBackPressed();
        return true;
      case R.id.action_get_members:
        group.getMembersInBackground(new GroupMemberQueryCallback() {

          @Override
          public void done(List<String> parseObjects, AVException parseException) {
            if (parseException == null) {
              Toast.makeText(getApplicationContext(), JSON.toJSONString(parseObjects),
                  Toast.LENGTH_SHORT).show();
            }
          }
        });
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    getMenuInflater().inflate(R.menu.groupchat, menu);
    return true;
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
  }

  @Override
  public void onMessage(String msg) {
    ChatDemoMessage message = JSON.parseObject(msg, ChatDemoMessage.class);
    messages.add(message);
    adapter.notifyDataSetChanged();

  }

  @Override
  public void onResume() {
    super.onResume();
    ChatDemoGroupMessageReceiver.registerGroupListener(groupId, this);
  }

  @Override
  public void onPause() {
    super.onPause();
    ChatDemoGroupMessageReceiver.unregisterGroupListener(groupId);
  }
}
