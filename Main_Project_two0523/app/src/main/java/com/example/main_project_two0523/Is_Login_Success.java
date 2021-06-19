package com.example.main_project_two0523;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Is_Login_Success {
    static final String CHECK = "ISLOGIN";

    static SharedPreferences getSharedPreference(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setLoginSuccess(Context context, int check){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putInt("ISLOGIN", check);
        editor.commit();
    }


    //저장된 정보 가져오기
    public static int getLoginSuccess(Context context){
        return getSharedPreference(context).getInt("ISLOGIN", 0);
    }
    //로그아웃
    public static void clearUser(Context context){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.clear();
        editor.commit();
    }

}
