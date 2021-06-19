package com.example.main_project_two0523;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference_Restaurants {
    static final String RES_ID = "res_ID";
    static final String RES_NAME = "res_name";

    static SharedPreferences getSharedPreference(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setRestaurantsID(Context context, String res_ID){
        SharedPreferences.Editor editor1 = getSharedPreference(context).edit();
        editor1.putString(RES_ID, res_ID);
        editor1.commit();
    }
    public static void setRestaurantsName(Context context, String res_name){
        SharedPreferences.Editor editor1 = getSharedPreference(context).edit();
        editor1.putString(RES_NAME, res_name);
        editor1.commit();
    }




    //저장된 정보 가져오기
    public static String getRestaurantsID(Context context){
        return getSharedPreference(context).getString(RES_ID, "");
    }
    public static String getRestaurantsName(Context context){
        return getSharedPreference(context).getString(RES_NAME, "");
    }

    //로그아웃
    public static void clearMenu(Context context){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.clear();
        editor.commit();
    }

}
