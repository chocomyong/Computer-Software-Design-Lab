package com.example.main_project_two0523;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Activity_Login_Main extends AppCompatActivity {
    private String Server_URL = ServerURL.getServerurl();
    Context context;
    private BackPressCloseHandler backPressCloseHandler;
    Button btn_login, btn_signup, kakaoButton;
    EditText eID, ePW;
    TextView eresult;
    Toolbar toolbar;
    Login_ProgressDialog progressDig;
    Toast mToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        context = Activity_Login_Main.this;
        backPressCloseHandler = new BackPressCloseHandler(this);
        progressDig = new Login_ProgressDialog(context);
        mToast = Toast.makeText(context, "null", Toast.LENGTH_SHORT);

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_signup = (Button) findViewById(R.id.btn_signup);
        kakaoButton = (Button) findViewById(R.id.kakaoButton);
        eID = (EditText) findViewById(R.id.eID);
        ePW = (EditText) findViewById(R.id.ePW);
        eresult = (TextView) findViewById(R.id.result);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);


        if(NetworkStatus.getConnectivityStatus(context)== 3) {
            mToast.setText("인터넷에 연결되지 않음.");
            mToast.show();
        }else {
            KakaoSdk.init(this, "3ccc7aba0007a5cfde72bd6d3bb66e7d");






            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoginProcess();
                }
            });
            btn_signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    go_SignUp_Activity();
                }
            });

            kakaoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(context)) {
                        UserApiClient.getInstance().loginWithKakaoTalk(context, kakaoCallback);
                    } else {
                        UserApiClient.getInstance().loginWithKakaoAccount(context, kakaoCallback);
                    }

                }
            });


        }


//        getAppKeyHash();
//        KakaoSdk.init(this, "0693832ff15e7156c2cec18c5d6ca5c1");
//        kakaoButton.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int action = event.getAction();
//                if(action==MotionEvent.ACTION_DOWN){
//                    //myToast = Toast.makeText(act, Long.toString(str), Toast.LENGTH_SHORT);
//                    if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(context)) {
//                        UserApiClient.getInstance().loginWithKakaoTalk(context, kakaoCallback);
//                    } else {
//                        UserApiClient.getInstance().loginWithKakaoAccount(context, kakaoCallback);
//                    }
//                    kakaoButton.setBackground(getResources().getDrawable(R.drawable.kakao_login_blue_final_dark));
//                }else if(action== MotionEvent.ACTION_UP){
//
//                    kakaoButton.setBackground(getResources().getDrawable(R.drawable.kakao_login_blue_final));
//
//                }
//                return true;
//            }
//
//        });

       // getAppKeyHash(); 해시키 등록시 사용.
    }


//    private void getAppKeyHash(){
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md;
//                md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String something = new String(Base64.encode(md.digest(), 0));
//                Log.e("Hash key", something);
//            }
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            Log.e("name not found", e.toString());
//        }
//    }
    public void LoginProcess() {

        LoginData task = new LoginData();
        SaveSharedPreference.setUserID(context,eID.getText().toString());
        SaveSharedPreference.setUserPW(context,ePW.getText().toString());
        task.execute(Server_URL+"login.php", SaveSharedPreference.getUserID(context),SaveSharedPreference.getUserPW(context));

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        // 메뉴버튼이 처음 눌러졌을 때 실행되는 콜백메서드
//        // 메뉴버튼을 눌렀을 때 보여줄 menu 에 대해서 정의
//        getMenuInflater().inflate(R.menu.menu, menu);
//        Log.d("test", "onCreateOptionsMenu - 최초 메뉴키를 눌렀을 때 호출됨");
//        return true;
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu1:
                Toast.makeText(getApplicationContext(), "사용법",
                        Toast.LENGTH_SHORT).show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu2, menu);





        return true;
    }

    public void go_Restaurant_Activity(){
        Intent restaurant_intent = new Intent(context, Activity_Restaurant_Main.class);
        //restaurant_intent.addFlags(  FLAG_ACTIVITY_CLEAR_TOP );
        restaurant_intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(restaurant_intent);
    }
    public void go_SignUp_Activity(){
        Intent signup_intent = new Intent(context, Activity_SignUp_Main.class);
        //restaurant_intent.addFlags(  FLAG_ACTIVITY_CLEAR_TOP );
       // signup_intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(signup_intent);
    }
////////////////////////
    Function2<OAuthToken, Throwable, Unit> kakaoCallback = new Function2<OAuthToken, Throwable, Unit>() {
        @Override
        public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
            if(oAuthToken != null){
                Log.d("Logged", "logged in: ");
            }
            if(throwable != null){
                Log.d("error", "Message: "+throwable.getLocalizedMessage());
            }
            getKaKaoProfile();
            return null;
        }
    };
    private void getKaKaoProfile() {
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                if (user != null) {
                    Log.d("Logged in", "Kakao id =" + user.getId());
                    //str = user.getId(); id를 long으로 보여줌.
                    //str = user.toString();//유저 정보 좌라락.
//                    mToast.setText(   user.getKakaoAccount().component4().getNickname().toString()+", "+ user.getKakaoAccount().component8().toString() );
//                    mToast.show();
//                    SaveSharedPreference_For_Kakao.setUserID(context,user.getKakaoAccount().component8().toString());
//                    SaveSharedPreference_For_Kakao.setUserPW(context,user.getKakaoAccount().component4().getNickname().toString());
                    SaveSharedPreference.setUserID(context,user.getKakaoAccount().component4().getNickname().toString());
                    SaveSharedPreference.setUserPW(context,user.getKakaoAccount().component8().toString());
                    LoginData task = new LoginData();
                    task.execute(Server_URL+"signup_kakao.php", SaveSharedPreference.getUserID(context), SaveSharedPreference.getUserPW(context));



                    //계정정보를 불러 왔을 경우
                } else {
                    Log.d("Logged in", "Kakao id = nono");
                    mToast.setText("로그인 실패 : KAKAO");
                    mToast.show();
                    //계정정보가 없을경우
                }
                if (throwable != null) {
                    Log.d("Error ", "invoke: " + throwable.getLocalizedMessage());
                }
                return null;
            }
        });
    }



    /////////////////////////////////////

    class LoginData extends AsyncTask<String, Void, String> {
        //ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDig.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDig.show();
//
            super.onPreExecute();
        }

//        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//context.startActivity(i);
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDig.dismiss(); //progress Dialog 종료

//            eresult.setText(result);
            Is_Login_Success.setLoginSuccess(context,result.equals("1")==true?1:0);

            if(!result.equals("1")){
                SaveSharedPreference.clearUser(context);
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            }else{
                go_Restaurant_Activity();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String user_ID = (String)params[1];
            String user_PW = (String)params[2];

            String serverURL = (String)params[0];
            String postParameters = "user_ID=" + user_ID + "&user_PW=" + user_PW;

            //  result.setText(postParameters);

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                int responseStatusCode = httpURLConnection.getResponseCode();
                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();



                return sb.toString();
            } catch (Exception e) {
                return new String("Error: " + e.getMessage());
            }

        }
    }
}



