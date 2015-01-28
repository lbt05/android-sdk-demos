package com.avoscloud.beijing.push.demo.keepalive;

import com.avos.avoscloud.im.v2.AVIMMessage;

public interface MessageListener {

  public void onMessage(AVIMMessage msg);

}
