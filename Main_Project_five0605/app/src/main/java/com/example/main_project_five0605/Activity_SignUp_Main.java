package com.example.main_project_five0605;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Activity_SignUp_Main extends AppCompatActivity {
    private String Server_URL = ServerURL.getServerurl();


    JSONArray peoples = null;


    static Context context;
    private BackPressCloseHandler backPressCloseHandler;
    Button btn_signup;
    EditText eID, ePW, email;
    Toolbar toolbar;
    Login_ProgressDialog progressDig;


    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_main);
        context = Activity_SignUp_Main.this;

        backPressCloseHandler = new BackPressCloseHandler(this);
        progressDig = new Login_ProgressDialog(context);
        mToast = Toast.makeText(context, "null",Toast.LENGTH_SHORT);


        eID = (EditText) findViewById(R.id.eID);
        ePW = (EditText) findViewById(R.id.ePW);
        email = (EditText)findViewById(R.id.email);

        btn_signup = (Button) findViewById(R.id.btn_signup);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);



        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDig.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDig.show();
                SignUpProcess();
            }
        });

    }

    public void SignUpProcess() {
        SignUpData task = new SignUpData();
        task.execute(Server_URL+"signup_staff.php", eID.getText().toString().trim(), ePW.getText().toString().trim(), email.getText().toString().trim());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //backPressCloseHandler.onBackPressed();
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
        Intent restaurant_intent = new Intent(context, Activity_MyRestaurant_Main.class);
        restaurant_intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(restaurant_intent);
    }


    class SignUpData extends AsyncTask<String, Void, String> {
        //ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            //ProgressDialog asyncDialog = new ProgressDialog(Login_ProgressDialogActivity.this);

//            progressDig.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            progressDig.show();
            super.onPreExecute();
        }

        //        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//context.startActivity(i);
        @Override
        protected void onPostExecute(String result) {
            progressDig.dismiss(); //progress Dialog 종료
            super.onPostExecute(result);
//            eresult.setText(result);
            Is_Login_Success.setLoginSuccess(context,result.equals("1")==true?1:0);
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            //Login Success
            if(result.equals("1")){
                // setting login token
                SaveSharedPreference.setUserID(context, eID.getText().toString().trim());
                SaveSharedPreference.setUserPW(context, ePW.getText().toString().trim());
                //Toast.makeText(getApplicationContext(), "Login:LoginSuccess : " + Is_Login_Success.getLoginSuccess(context), Toast.LENGTH_SHORT).show();
                // Move Intent
                go_Restaurant_Activity();
            }else{
//                eID.setText("");
//                ePW.setText("");
//                email.setText("");
                SaveSharedPreference.clearUser(context);
                Toast.makeText(getApplicationContext(), result,  Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String user_ID = (String)params[1];
            String user_PW = (String)params[2];
            String my_res_ID = (String)params[3];

            String serverURL = (String)params[0];
            String postParameters = "user_ID=" + user_ID + "&user_PW=" + user_PW + "&my_res_ID="+my_res_ID;
          //  mToast.setText(postParameters);
           // mToast.show();
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



