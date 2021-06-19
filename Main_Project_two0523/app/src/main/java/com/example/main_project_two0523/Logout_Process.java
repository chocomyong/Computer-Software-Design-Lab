package com.example.main_project_two0523;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Logout_Process extends AppCompatActivity {

    public  Context context ;
    public Logout_Process(Context context){
        this.context = context;

    }
    // Logout Process : Logout 에대한 처리.
    public  Intent Logout_Process(){
        Intent Login_Intent = null;
        // 모든 정보, (User ID,PW, AutoLoginCheck, LoginSuccess) 초기화
        SaveSharedPreference.clearUser(context);// user 정보 초기화
        SaveSharedPreference_For_Kakao.clearUser(context);
        Is_Login_Success.clearUser(context);

        // 재 로그인을 위한 Login_Main Activity 로 이동
        Login_Intent = new Intent(context, Activity_Login_Main.class); // 이동하는 context target은 항상 동일하게 Login Activity 이다.
        Login_Intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_CLEAR_TOP);
        return Login_Intent;

    }

}
