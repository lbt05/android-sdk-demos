package com.avoscloud.beijing.push.demo.keepalive;

import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMClientEventHandler;

/**
 * Created by lbt05 on 6/1/15.
 */
public class KeepAliveClientEventHandler extends AVIMClientEventHandler {
    @Override
    public void onConnectionPaused(AVIMClient client) {
        LogUtil.avlog.d("Client Connection Lost");
    }

    @Override
    public void onConnectionResume(AVIMClient client) {
        LogUtil.avlog.d("Client Connection Resume");
    }
}
