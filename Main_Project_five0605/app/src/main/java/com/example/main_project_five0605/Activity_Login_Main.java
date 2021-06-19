package com.example.main_project_five0605;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Activity_Login_Main extends AppCompatActivity {
    private String Server_URL = ServerURL.getServerurl();
    Context context;
    private BackPressCloseHandler backPressCloseHandler;
    Button btn_login, btn_signup;
    EditText eID, ePW;
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

        eID = (EditText) findViewById(R.id.eID);
        ePW = (EditText) findViewById(R.id.ePW);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        if(NetworkStatus.getConnectivityStatus(context)== 3){
            mToast.setText("인터넷에 연결되지 않음.");
            mToast.show();

        }else{

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
        }

    }
    public void LoginProcess() {
        LoginData task = new LoginData();
        SaveSharedPreference.setUserID(context,eID.getText().toString().trim());
        SaveSharedPreference.setUserPW(context,ePW.getText().toString().trim());
        task.execute(Server_URL+"login_staff.php", SaveSharedPreference.getUserID(context),SaveSharedPreference.getUserPW(context));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

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
        Intent restaurant_intent = new Intent(context, Activity_MyRestaurant_Main.class);
        restaurant_intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(restaurant_intent);
    }
    public void go_SignUp_Activity(){
        Intent signup_intent = new Intent(context, Activity_SignUp_Main.class);
        startActivity(signup_intent);
    }




    class LoginData extends AsyncTask<String, Void, String> {
        //ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDig.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDig.show();
//
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDig.dismiss(); //progress Dialog 종료
            Is_Login_Success.setLoginSuccess(context,result.equals("1")==true?1:0);

            if(!result.equals("1")){// 로그인 실패시
                SaveSharedPreference.clearUser(context);
               // SaveSharedPreferenceCheckAutoLogin.clearUser(context);
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            }else{
                SaveSharedPreference.setUserID(context, eID.getText().toString());
                SaveSharedPreference.setUserPW(context,ePW.getText().toString());
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

                httpURLConnection.setReadTimeout(500);
                httpURLConnection.setConnectTimeout(500);
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



