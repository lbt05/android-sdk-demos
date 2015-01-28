package com.avoscloud.beijing.push.demo.keepalive;

import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQueryCallback;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ConversationListFragment extends Fragment {
  String selfId;
  ListView conversationList;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.onlinelist, null);
    rootView.findViewById(R.id.add_new).setVisibility(View.GONE);
    conversationList = (ListView) rootView.findViewById(R.id.onlineList);
    selfId = AVUser.getCurrentUser().getObjectId();
    return rootView;
  }

  @Override
  public void onResume() {
    super.onResume();
    queryConversations();
  }

  public void queryConversations() {
    LogUtil.avlog.d("try to fetch recent conversations");
    AVIMClient client = AVIMClient.getInstance(selfId);
    client.getRecentConversations(0, 10, new AVIMConversationQueryCallback() {

      @Override
      public void done(List<AVIMConversation> conversations, AVException error) {
        if (error == null) {
          ConversationAdapter apdater = new ConversationAdapter(getActivity(), conversations);
          conversationList.setAdapter(apdater);
          conversationList.setOnItemClickListener(apdater);
        } else {
          error.printStackTrace();
        }
      }
    });
  }

}
