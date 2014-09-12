package com.avos.demo;

import android.text.TextUtils;
import android.util.Log;
import com.avos.avoscloud.*;
import com.avos.avoscloud.search.AVSearchQuery;

import java.util.Collections;
import java.util.List;

/**
 * Created by lzw on 14-9-12.
 */
public class AVService {
  public static void fetchTodoById(String objectId,GetCallback<AVObject> getCallback) {
    Todo todo = new Todo();
    todo.setObjectId(objectId);
    // 通过Fetch获取content内容
    todo.fetchInBackground(getCallback);
  }

  public static void createOrUpdateTodo(String objectId, String content, SaveCallback saveCallback) {
    final Todo todo = new Todo();
    if (!TextUtils.isEmpty(objectId)) {
      // 如果存在objectId，保存会变成更新操作。
      todo.setObjectId(objectId);
    }
    todo.setContent(content);
    // 异步保存
    todo.saveInBackground(saveCallback);
  }

  public static List<Todo> findTodos() {
    // 查询当前Todo列表
    AVQuery<Todo> query = AVQuery.getQuery(Todo.class);
    // 按照更新时间降序排序
    query.orderByDescending("updatedAt");
    // 最大返回1000条
    query.limit(1000);
    try {
      return query.find();
    } catch (AVException exception) {
      Log.e("tag", "Query todos failed.", exception);
      return Collections.emptyList();
    }
  }

  public static void searchQuery(String inputSearch) {
    AVSearchQuery searchQuery = new AVSearchQuery(inputSearch);
    searchQuery.search();
  }
}
