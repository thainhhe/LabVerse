package com.example.labverse.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {
    private static final String PREF_NAME = "LabVerseAuth";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_ROLE = "user_role";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public AuthManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveAuthToken(String token) {
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.apply();
    }

    public String getAuthToken() {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null);
    }

    public void saveUserData(String email, String name, String role) {
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_ROLE, role);
        editor.apply();
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "");
    }

    public String getUserRole() {
        return sharedPreferences.getString(KEY_USER_ROLE, "");
    }

    public boolean isLoggedIn() {
        return getAuthToken() != null;
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
