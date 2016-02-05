package com.devoxx.data.user;

import android.content.Context;
import android.text.TextUtils;

import com.devoxx.android.activity.RegisterUserActivity_;
import com.devoxx.data.Settings_;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.sharedpreferences.Pref;

@EBean
public class UserManager {

    @RootContext
    Context context;

    @Pref
    Settings_ settings;

    public boolean isFirstTimeUser() {
        return TextUtils.isEmpty(getUserCode());
    }

    public void openUserScanBadge() {
        RegisterUserActivity_.intent(context).start();
    }

    public void saveUserCode(String code) {
        settings.edit().userId().put(code).apply();
    }

    public String getUserCode() {
        return settings.userId().get();
    }

    public void clearCode() {
        settings.edit().userId().put("").apply();
    }
}
