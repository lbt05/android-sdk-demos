package com.avoscloud.beijing.push.demo.keepalive;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;

public class ChatTargetActivity extends FragmentActivity {
  ViewPager mPager;
  PagerAdapter adapter;
  PagerTabStrip pagerTabStrip;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.chat_collection);
    mPager = (ViewPager) findViewById(R.id.pager);
    mPager.setAdapter(new ChatTargetFragmentAdapter(getSupportFragmentManager()));
    mPager.setPageMargin(16);

    pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_tab_strip);
    pagerTabStrip.setTabIndicatorColorResource(R.color.avoscloud_tab_indicator_color);
    pagerTabStrip.setTextColor(getResources().getColor(R.color.avoscloud_tab_text_color));
  }

  @Override
  public void onBackPressed() {

    AVIMClient.getInstance(AVUser.getCurrentUser().getObjectId()).close(new AVIMClientCallback() {

      @Override
      public void done(AVIMClient client, AVException e) {
        if (e != null) {

        }
        back();
      }
    });
  }

  private void back() {
    AVUser.logOut();
    super.onBackPressed();
  }

  public class ChatTargetFragmentAdapter extends FragmentStatePagerAdapter {

    public ChatTargetFragmentAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      Fragment frag = null;
      switch (position) {
        case 0:
          // TODO;
          frag = new ConversationListFragment();
          break;
        case 1:
          frag = new UserListFragment();

          break;
      }
      return frag;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      String title = null;
      switch (position) {
        case 0:
          title = getResources().getString(R.string.conversations);
          break;
        case 1:
          title = getResources().getString(R.string.online_users);
          break;
      }
      return title;
    }

    @Override
    public int getCount() {
      return 2;
    }
  }
}
