package com.avoscloud.beijing.push.demo.keepalive;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.AVUtils;
import com.avos.avoscloud.im.v2.AVIMMessage;

public class ChatDataAdapter extends BaseAdapter {

  List<AVIMMessage> messages;
  Context mContext;

  public ChatDataAdapter(Context context, List<AVIMMessage> messages) {
    this.messages = messages;
    this.mContext = context;
  };

  @Override
  public int getCount() {
    return messages.size();
  }

  @Override
  public AVIMMessage getItem(int position) {
    return messages.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder = null;
    final AVIMMessage m = getItem(position);

    if (convertView == null) {
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_message, null);
      holder = new ViewHolder();
      holder.message = (TextView) convertView.findViewById(R.id.avoscloud_chat_demo_message);
      holder.username = (TextView) convertView.findViewById(R.id.avoscloud_chat_demo_user_id);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    String fromId = m.getFrom();
    if (AVUtils.isBlankString(fromId)) {
      fromId = AVUser.getCurrentUser().getObjectId();
    }
    holder.username.setText(HTBApplication.lookupname(fromId));
    holder.message.setText(m.getContent());
    return convertView;
  }

  public class ViewHolder {
    TextView message;
    TextView username;
  }

}
