package com.example.main_project_two0523;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference_Rating {
    static final String RATING = "RATING";

    static SharedPreferences getSharedPreference(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setRating(Context context, float menu_ID){
        SharedPreferences.Editor editor1 = getSharedPreference(context).edit();
        editor1.putFloat(RATING, menu_ID);
        editor1.commit();
    }

    //저장된 정보 가져오기
    public static float getRating(Context context){
        return getSharedPreference(context).getFloat(RATING, 5);
    }


    //로그아웃
    public static void clearRating(Context context){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.clear();
        editor.commit();
    }

}
