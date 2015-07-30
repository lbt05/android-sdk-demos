package com.avoscloud.beijing.push.demo.keepalive;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.AVUtils;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements View.OnClickListener {

  private EditText nameInput;
  private EditText passwordInput;
  private Button joinButton;
  int retryTimes = 0;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.main);


    joinButton = (Button) findViewById(R.id.button);
    joinButton.setOnClickListener(this);
    nameInput = (EditText) findViewById(R.id.username);
    passwordInput = (EditText) findViewById(R.id.password);
    retryTimes = 0;

    String predefinedName =
        PreferenceManager.getDefaultSharedPreferences(this).getString("username", null);
    if (predefinedName != null) {
      nameInput.setText(predefinedName);
    }
    AVUser currentUser = AVUser.getCurrentUser();
    if (currentUser != null && currentUser.isAuthenticated()) {
      onLoginSuccess();
    }

  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }

  @Override
  public void onClick(View v) {
    final String name = nameInput.getText().toString();
    if (AVUtils.isBlankString(name)) {
      nameInput.setError(getResources().getText(R.string.username_empty_error));
      nameInput.requestFocus();
      return;
    }
    final String password = passwordInput.getText().toString();
    if (AVUtils.isBlankString(name)) {
      passwordInput.setError(getResources().getText(R.string.password_error));
      passwordInput.requestFocus();
      return;
    }

    SharedPreferences spr = PreferenceManager.getDefaultSharedPreferences(this);
    spr.edit().putString("username", name).commit();
    AVUser.logInInBackground(name, password, new LogInCallback<AVUser>() {

      @Override
      public void done(AVUser user, AVException e) {
        if (e == null) {
          onLoginSuccess();
        } else {
          if (e.getCode() == AVException.USER_DOESNOT_EXIST) {
            AVUser newUser = new AVUser();
            newUser.setUsername(name);
            newUser.setPassword(password);
            newUser.put("v2", true);
            newUser.saveInBackground(new SaveCallback() {

              @Override
              public void done(AVException e) {
                if (e == null) {
                  onLoginSuccess();
                } else {
                  passwordInput.setError(e.getMessage());
                  passwordInput.requestFocus();
                }
              }
            });
          } else {
            retryTimes++;
            passwordInput.setError(e.getMessage());
            passwordInput.requestFocus();
            if (retryTimes >= 3) {
              passwordInput.setInputType(InputType.TYPE_CLASS_TEXT);
            }
          }
        }
      }
    });

  }

  private void onLoginSuccess() {
    final long startTime = System.currentTimeMillis();
    retryTimes = 0;
    final String selfId = AVUser.getCurrentUser().getObjectId();
    HTBApplication.registerLocalNameCache(AVUser.getCurrentUser().getObjectId(), AVUser
        .getCurrentUser().getUsername());
    AVIMClient client = AVIMClient.getInstance(selfId);
      LogUtil.avlog.d("try to  client open");
    client.open(new AVIMClientCallback() {
      @Override
      public void done(AVIMClient client, AVIMException e) {
          LogUtil.avlog.d("callback from client open");
        if (e == null) {
          LogUtil.avlog.d("time to spend for open session:"+(System.currentTimeMillis() -startTime));
          Intent intent = new Intent(MainActivity.this, ChatTargetActivity.class);
          startActivity(intent);
        } else {
          e.printStackTrace();
        }
      }
    });
  }
}
