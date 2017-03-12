package com.silpe.vire.slip.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.silpe.vire.slip.dtos.User;

import java.util.ArrayList;
import java.util.List;

//TODO Link this class with SharedPreferences to gracefully handle and cache information

/**
 * Singleton class that contains the current session models.
 */
public class SessionModel {

    private static final String USER_SESSION_CACHE = "userSession";
    private static final String USER_SESSION_KEY = "user";

    private static SessionModel model = null;

    public static SessionModel get() {
        if (model == null) {
            model = new SessionModel();
        }
        return model;
    }

    private static <T> void notifyListeners(List<SessionModelListener<T>> listeners, T t) {
        for (SessionModelListener<T> listener : listeners) {
            listener.valueUpdated(t);
        }
    }

    private SessionModel() {
        userListeners = new ArrayList<>();
    }


    private User user;
    private List<SessionModelListener<User>> userListeners;

    public User getUser(Context context) {
        if (user == null) {
            readUser(context);
        }
        return user;
    }

    public void setUser(User user, Context context) {
        this.user = user;
        notifyListeners(userListeners, user);
        cacheUser(context);
    }

    public void addListener(SessionModelListener<User> userListener) {
        userListeners.add(userListener);
    }

    public void removeListener(SessionModelListener<User> userListener) {
        userListeners.remove(userListener);
    }

    private void cacheUser(@NonNull Context context) {
        SharedPreferences userSessionCache = context.getSharedPreferences(USER_SESSION_CACHE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userSessionCache.edit();
        editor.putString(USER_SESSION_KEY, user == null ? null : user.encode());
        editor.apply();
    }

    private void readUser(@NonNull Context context) {
        SharedPreferences userSessionCache = context.getSharedPreferences(USER_SESSION_CACHE, Context.MODE_PRIVATE);
        String serial = userSessionCache.getString(USER_SESSION_KEY, null);
        user = serial == null ? null : new User().decode(serial);
    }

}
