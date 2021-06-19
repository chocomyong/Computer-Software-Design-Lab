package com.example.main_project_five0605;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SAVE_CLIENT {
    static final String USER_ID_FOR= "user_ID_FOR";
    static final String USER_TABLE = "user_Table";

    static SharedPreferences getSharedPreference(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setUser_ID(Context context, String user_ID){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(USER_ID_FOR, user_ID);
        editor.commit();
    }
    public static void setUser_Table(Context context, String user_Table){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(USER_TABLE, user_Table);
        editor.commit();
    }


    //저장된 정보 가져오기
    public static String getUser_ID(Context context){
        return getSharedPreference(context).getString(USER_ID_FOR, "");
    }
    public static String getuser_Table(Context context){
        return getSharedPreference(context).getString(USER_TABLE, "-");
    }

    //로그아웃
    public static void clearUser(Context context){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.clear();
        editor.commit();
    }

}
