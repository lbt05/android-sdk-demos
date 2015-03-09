package com.avoscloud.beijing.push.demo.keepalive;

import java.util.List;
import java.util.Random;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ConversationAdapter extends BaseAdapter implements OnItemClickListener {
  Context mContext;
  List<AVIMConversation> data;
  Random random;

  public ConversationAdapter(Context context, List<AVIMConversation> conversations) {
    this.mContext = context;
    data = conversations;
    random = new Random();
  }

  @Override
  public int getCount() {
    return data.size();
  }

  @Override
  public AVIMConversation getItem(int position) {
    return data.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    ViewHolder holder = null;
    if (convertView == null) {
      convertView = LayoutInflater.from(mContext).inflate(R.layout.item_chat_target, null);
      holder = new ViewHolder();
      holder.username = (TextView) convertView.findViewById(R.id.onlinetarget);
      holder.avatar = (ImageView) convertView.findViewById(R.id.online_icon);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    int avatarColor =
        Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));

    holder.avatar.setBackgroundColor(avatarColor);
    holder.username.setText((String) this.getItem(position).getAttributes().get("name"));
    return convertView;
  }

  public class ViewHolder {
    TextView username;
    ImageView avatar;
  }

  @Override
  public void onItemClick(AdapterView<?> adapterView, View v, final int position, long itemId) {
    Intent i = new Intent(mContext, PrivateConversationActivity.class);
    i.putExtra(PrivateConversationActivity.DATA_EXTRA_SINGLE_DIALOG_TARGET, getItem(position)
        .getConversationId());
    mContext.startActivity(i);
    ((Activity) mContext).overridePendingTransition(android.R.anim.slide_in_left,
        android.R.anim.slide_out_right);
  }

}
