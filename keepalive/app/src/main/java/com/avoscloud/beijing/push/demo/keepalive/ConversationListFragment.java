package com.avoscloud.beijing.push.demo.keepalive;

import java.util.Arrays;
import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;

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
    AVIMConversationQuery query = client.getQuery();
    query.whereEqualTo("public", true);
    query.containsMembers(Arrays.asList(selfId));
    query.findInBackground(new AVIMConversationQueryCallback() {
      @Override
      public void done(List<AVIMConversation> conversations, AVException e) {
        if (e == null) {
          ConversationAdapter adapter = new ConversationAdapter(getActivity(), conversations);
          conversationList.setAdapter(adapter);
          conversationList.setOnItemClickListener(adapter);
        } else {
          e.printStackTrace();
        }
      }
    });
  }

}
