# AVOS Cloud Android SDK Demos

本目录存放 Android 的示例项目，包括：

* [AVOSCloud-Todo](./AVOSCloud-Todo)：一个演示数据增删改查、子类化、统计和应用内搜索的简单 demo。
* [AVOSCloud-Push](./AVOSCloud-Push)：一个演示如何使用消息推送的例子。
* [AnyTime](./AnyTime)：从开发者「猫咪神」征集到一个应用，主要演示用户系统（注册、登陆等）、数据存储以及消息推送功能。
* [AVOSCloud-Demo](./AVOSCloud-Demo)：一个综合型的例子，演示了大多数功能，但是 UI 比较简陋，需要您通过代码学习。
* [keepalive](./keepalive)：是实时聊天系统的例子，包括了用户间聊天和群聊功能等。


[AVOS Cloud 站点下载地址](https://download.avoscloud.com/demo/)

## 推送 Demo 

你可以从推送 Demo 中学到：

* 如何自定义接受推送信息的 `Receiver`
* 如何订阅频道
* 如何根据 `installationId` 或 `channel` 推送，随意定义你的推送对象
* 如何推送 `json` 数据以及获取数据

![img](https://github.com/lzwjava/plan/blob/master/push.png)

## Todo Demo

可以从 Todo Demo 中学到：

* 如何使用 `AVObject` 保存数据到服务器上
* 如何扩展 `AVObject` 类来更为方便地使用
* 如何使用 `AVQuery` 查询服务器数据
* 如何为应用加入统计功能，统计相应的事件，从而分析用户喜好
* 如何给应用加入全局搜索功能，便捷地找到所需要的信息

Todo页，

![img](https://raw.githubusercontent.com/lzwjava/plan/master/android-todo-360.png)

应用内搜索页，

![img](https://raw.githubusercontent.com/lzwjava/plan/master/todo360.png)


## Anytime
将学会

* 配置消息推送
* AVQuery
* 邮箱验证重设密码
* 用户登录注册
* 调用云函数
* 创建AVObject

应用截图，

![img](https://raw.githubusercontent.com/lzwjava/plan/master/anytime360.png)

相关代码，
```java
public class AVService {
  public static void countDoing(String doingObjectId, CountCallback countCallback) {
    AVQuery<AVObject> query = new AVQuery<AVObject>("DoingList");
    query.whereEqualTo("doingListChildObjectId", doingObjectId);
    Calendar c = Calendar.getInstance();
    c.add(Calendar.MINUTE, -10);
    // query.whereNotEqualTo("userObjectId", userId);
    query.whereGreaterThan("createdAt", c.getTime());
    query.countInBackground(countCallback);
  }

  //Use callFunctionMethod
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static void getAchievement(String userObjectId) {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("userObjectId", userObjectId);
    AVCloud.callFunctionInBackground("hello", parameters,
        new FunctionCallback() {
          @Override
          public void done(Object object, AVException e) {
            if (e == null) {
              Log.e("at", object.toString());// processResponse(object);
            } else {
              // handleError();
            }
          }
        });
  }

  public static void createDoing(String userId, String doingObjectId) {
    AVObject doing = new AVObject("DoingList");
    doing.put("userObjectId", userId);
    doing.put("doingListChildObjectId", doingObjectId);
    doing.saveInBackground();
  }

  public static void requestPasswordReset(String email, RequestPasswordResetCallback callback) {
    AVUser.requestPasswordResetInBackground(email, callback);
  }

  public static void findDoingListGroup(FindCallback<AVObject> findCallback) {
    AVQuery<AVObject> query = new AVQuery<AVObject>("DoingListGroup");
    query.orderByAscending("Index");
    query.findInBackground(findCallback);
  }

  public static void findChildrenList(String groupObjectId, FindCallback<AVObject> findCallback) {
    AVQuery<AVObject> query = new AVQuery<AVObject>("DoingListChild");
    query.orderByAscending("Index");
    query.whereEqualTo("parentObjectId", groupObjectId);
    query.findInBackground(findCallback);
  }

  public static void initPushService(Context ctx) {
    PushService.setDefaultPushCallback(ctx, LoginActivity.class);
    PushService.subscribe(ctx, "public", LoginActivity.class);
    AVInstallation.getCurrentInstallation().saveInBackground();
  }

  public static void signUp(String username, String password, String email, SignUpCallback signUpCallback) {
    AVUser user = new AVUser();
    user.setUsername(username);
    user.setPassword(password);
    user.setEmail(email);
    user.signUpInBackground(signUpCallback);
  }

  public static void logout() {
    AVUser.logOut();
  }

  public static void createAdvice(String userId, String advice, SaveCallback saveCallback) {
    AVObject doing = new AVObject("SuggestionByUser");
    doing.put("UserObjectId", userId);
    doing.put("UserSuggestion", advice);
    doing.saveInBackground(saveCallback);
  }
}

```
