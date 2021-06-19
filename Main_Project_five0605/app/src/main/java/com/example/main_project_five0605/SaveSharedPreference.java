package com.example.main_project_five0605;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {
    static final String PREF_USER_ID = "user_ID";
    static final String PREF_USER_PW = "user_PW";

    static SharedPreferences getSharedPreference(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setUserID(Context context, String user_ID){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(PREF_USER_ID, user_ID);
        editor.commit();
    }
    public static void setUserPW(Context context, String user_PW){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(PREF_USER_PW, user_PW);
        editor.commit();
    }


    //저장된 정보 가져오기
    public static String getUserID(Context context){
        return getSharedPreference(context).getString(PREF_USER_ID, "");
    }
    public static String getUserPW(Context context){
        return getSharedPreference(context).getString(PREF_USER_PW, "");
    }

    //로그아웃
    public static void clearUser(Context context){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.clear();
        editor.commit();
    }

}
