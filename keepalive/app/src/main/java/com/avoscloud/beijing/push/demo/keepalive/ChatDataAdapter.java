package com.avoscloud.beijing.push.demo.keepalive;

import java.util.List;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.AVUtils;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMLocationMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;

public class ChatDataAdapter extends BaseAdapter {

  List<AVIMMessage> messages;
  Context mContext;
  String clientId;

  public ChatDataAdapter(Context context, List<AVIMMessage> messages, String clientId) {
    this.messages = messages;
    this.mContext = context;
    this.clientId = clientId;
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

    long now = System.currentTimeMillis() + 1;

    if (convertView == null) {
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_message, null);
      holder = new ViewHolder();
      holder.message = (TextView) convertView.findViewById(R.id.leancloud_chat_demo_message);
      holder.username = (TextView) convertView.findViewById(R.id.leancloud_chat_demo_user_id);
      holder.messageStatus =
          (TextView) convertView.findViewById(R.id.leancloud_chat_demo_message_status);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    String fromId = m.getFrom();
    if (AVUtils.isBlankString(fromId)) {
      fromId = AVUser.getCurrentUser().getObjectId();
    }
    holder.username.setText(HTBApplication.lookupName(fromId));
    if(m instanceof  AVIMTextMessage){
        holder.message.setText(((AVIMTextMessage) m).getText());
    }else if(m instanceof AVIMLocationMessage){
        holder.message.setText(mContext.getString(R.string.msg_here));
    }

    switch (m.getMessageIOType()) {
      case AVIMMessageIOTypeIn:
        holder.messageStatus
            .setText(mContext.getResources().getString(R.string.message_received)
                + " "
                + DateUtils.getRelativeTimeSpanString(m.getTimestamp(), now,
                    DateUtils.SECOND_IN_MILLIS));
        break;
      case AVIMMessageIOTypeOut:
        switch (m.getMessageStatus()) {
          case AVIMMessageStatusSending:
            holder.messageStatus.setText(mContext.getResources()
                .getString(R.string.message_sending));
            break;
          case AVIMMessageStatusSent:
            holder.messageStatus.setText(mContext.getResources().getString(R.string.message_sent)
                + " "
                + DateUtils.getRelativeTimeSpanString(m.getTimestamp(), now,
                    DateUtils.SECOND_IN_MILLIS));
            break;
          case AVIMMessageStatusReceipt:
            holder.messageStatus.setText(mContext.getResources().getString(R.string.message_read)
                + " "
                + DateUtils.getRelativeTimeSpanString(m.getReceiptTimestamp(), now,
                    DateUtils.SECOND_IN_MILLIS));
            break;
        }
        break;
    }

    return convertView;
  }

  public class ViewHolder {
    TextView message;
    TextView username;
    TextView messageStatus;
  }

}
