package com.example.main_project_five0605;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import com.example.main_project_five0605.current_user.ListViewAdapter_User;
import com.example.main_project_five0605.current_user.ListViewItem_User;
import com.google.android.material.tabs.TabLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Activity_MyRestaurant_Main extends AppCompatActivity {
    private String Server_URL = ServerURL.getServerurl();
    String orderJSON;
    String beforeCall, afterCall;

    Context context;
    RecyclerView mRecyclerView;
    JSONArray peoples = null;

    LinearLayoutManager linearLayoutManager;
    static final String TAG_RESULTS = "result";
    static final String TAG_TABLE_ID = "table_ID";
    static final String TAG_USER = "user_ID";

    private ViewPager viewPager;
    private TabLayout tabLayout;

    String beforeJSON_forIsOrdering, afterJSON_forISOrdering;
    private BackPressCloseHandler backPressCloseHandler;
    Toolbar toolbar;

    Toast mToast;
    Logout_Process logout;
    RecyclerView list;
    String Qr_Scan;
    TextView title, call;
    Button resetCall;
    int idx=0;
    Vibrator vb;
    //qr scan
    private IntentIntegrator qrScan;

    ScheduledThreadPoolExecutor exec, execcall;
    Runnable r, rcall;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myrestaurant_main);
        context = Activity_MyRestaurant_Main.this;
        backPressCloseHandler = new BackPressCloseHandler(this);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        title = (TextView)findViewById(R.id.title);
        resetCall = (Button)findViewById(R.id.resetCall);
        call = (TextView)findViewById(R.id.call);
        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //SaveSharedPreference
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        tabLayout=(TabLayout)findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("주문관리"));
        tabLayout.addTab(tabLayout.newTab().setText("매출관리"));
        tabLayout.addTab(tabLayout.newTab().setText("메뉴관리"));
        tabLayout.addTab(tabLayout.newTab().setText("좌석관리"));

        viewPager=(ViewPager)findViewById(R.id.viewPager);
        list = (RecyclerView) findViewById(R.id.listView);

        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mToast = Toast.makeText(context, "null", Toast.LENGTH_SHORT);
        logout = new Logout_Process(context);
        title.setText("여기요");
        title.setTextSize(28);


        //  현재 로그인이 된 상태인지 확인.
        // 로그인이 되어있지 않다면 로그인 페이지( Login_Main.java )로 이동.
        // 정상적이지 않은 상태, 혹은 로그아웃 상태 이후 다시 Activity 가 load되었다면,

        //로그인 된 기록이 없으면 로그인 페이지로.
        if( Is_Login_Success.getLoginSuccess(context)!=1){
            startActivity(logout.Logout_Process());
        }




        if(NetworkStatus.getConnectivityStatus(context)== 3){
            goLoginActivity();
        }else{
            MyTableName mtn = new MyTableName();
            mtn.execute(Server_URL+"getMyRestaurant_name.php", SaveSharedPreference.getUserID(context) );

            getMyRestaurants task = new getMyRestaurants();
            task.execute("http://115.22.203.91/getMyRestaurants.php", SaveSharedPreference.getUserID(context));
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }
                @Override
                public void onTabUnselected(TabLayout.Tab tab) { }
                @Override
                public void onTabReselected(TabLayout.Tab tab) { }

            });




//
            // 현재 주문이 들어왔는지 확인하는 Transaction
            IsOrdering isordering = new IsOrdering();
            isordering.execute(Server_URL + "is_ordering.php",SaveSharedPreference_Restaurants.getRestaurantsID(context).trim());
            r = new Runnable() {
                public void run() {
                    IsOrderingAfter isordering2 = new IsOrderingAfter();
                    isordering2.execute(Server_URL + "is_ordering.php",SaveSharedPreference_Restaurants.getRestaurantsID(context).trim());
                }
            };
//        scheduleWithFixedDelay
            exec = new ScheduledThreadPoolExecutor(1);
            exec.scheduleWithFixedDelay(r , 0, 1000, TimeUnit.MILLISECONDS);



            CallingBefore call = new CallingBefore();
            call.execute(Server_URL + "iscalling.php",SaveSharedPreference_Restaurants.getRestaurantsID(context).trim());
            rcall = new Runnable() {
                public void run() {
                    CallingAfter call2 = new CallingAfter();
                    call2.execute(Server_URL + "iscalling.php",SaveSharedPreference_Restaurants.getRestaurantsID(context).trim());
                }
            };
            execcall = new ScheduledThreadPoolExecutor(1);
            execcall.scheduleWithFixedDelay(rcall , 0, 1000, TimeUnit.MILLISECONDS);




            resetCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initCall rc = new initCall();
                    rc.execute(Server_URL + "reset_calling.php",SaveSharedPreference_Restaurants.getRestaurantsID(context).trim());
                }
            });
        }
    }

    protected void showCall() {
        try {

            JSONObject jsonObj = new JSONObject(orderJSON);//전체 JSON
            peoples = jsonObj.getJSONArray(TAG_RESULTS);
            ListViewAdapter_User adapter_menu = new ListViewAdapter_User();
            idx = peoples.length();
            if(idx <= 0 ){
                call.setText("-");
            }else{
                String res="";
                for (int i = 0; i < idx; i++) {
                    JSONObject c = peoples.getJSONObject(i);
                    if(i==0){
                        res = c.getString(TAG_TABLE_ID)+"    ";
                    }else if(i==1){
                        res += c.getString(TAG_TABLE_ID)+"   ";
                    }else{
                        res += c.getString(TAG_TABLE_ID)+"  ";

                    }
                }
                call.setText(res);
            }








            adapter_menu.setOnClickListener(new ListViewAdapter_User.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
//                    SAVE_CLIENT.clearUser(context);

                    ListViewItem_User data = adapter_menu.getItem(position);
                    String temp_user_ID = data.get_user_ID();
                    SAVE_CLIENT.setUser_ID(context, temp_user_ID);
                    Intent intent_showuserorder = new Intent(context, Activity_ShowUserOrder.class );
                    startActivity(intent_showuserorder);
                    //intent_showuserorder.putExtra("user_ID",data.get_user_ID());
                    //해당 activity로 user ID를 보냄
                    //해당 activity는 해당 user의 주문정보를 알려준다.
                    // startActivityForResult(intent_showuserorder,0);
                    // 테이블을 할당할 고객을 위해 저장. //위의 인텐트는 임시로 보내주는 값


                    //주의>><< SaveSharedPreference는 함수의 리턴값을 value로 줄 수 없다.


                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }
    public void goLoginActivity(){
        Intent Login_Intent = new Intent(context, Activity_Login_Main.class);
        Login_Intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(Login_Intent);
    }


    //QR SCAN 결과 값 불러오기
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() == null){
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            }else{
                Qr_Scan = result.getContents();
                //Toast.makeText(this,  Qr_Scan, Toast.LENGTH_SHORT).show();

                QR_Pay task = new QR_Pay();
                task.execute("http://115.22.203.91/QR_pay.php", Qr_Scan);
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                backPressCloseHandler.onBackPressed();
                return true;
            case R.id.logout:
                Toast.makeText(getApplicationContext(), "Logout" ,
                        Toast.LENGTH_SHORT).show();
                startActivity(logout.Logout_Process());  // logout에 대한 Intent를 return

                return true;
            case R.id.qr_scan:
                Toast.makeText(getApplicationContext(), "QR SCAN",
                        Toast.LENGTH_SHORT).show();
                qrScan = new IntentIntegrator(Activity_MyRestaurant_Main.this);
                qrScan.setOrientationLocked(false);
                qrScan.setPrompt("Sample Text");
                qrScan.initiateScan();


                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }


    class getMyRestaurants extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            SaveSharedPreference_Restaurants.setRestaurantsID(context,result.trim());
//            mToast.setText("Restaurant ID : "+ result);
//            mToast.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String user_ID = (String) params[1];
            String serverURL = (String) params[0];//url
            String postParameters = "user_ID="+ user_ID;
            try {
                URL url = new URL(serverURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(5000);
                con.setConnectTimeout(5000);
                con.setRequestMethod("POST");
                con.connect();
                OutputStream outputStream = con.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                int responseStatusCode = con.getResponseCode();
                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = con.getInputStream();
                } else {
                    inputStream = con.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String json = null;

                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json + "\n");
                }
                bufferedReader.close();
                return sb.toString().trim();

            } catch (Exception e) {

                return new String("Error: " + e.getMessage());
            }

        }
    }


    class QR_Pay extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            orderJSON = result;

        }
        @Override
        protected String doInBackground(String... params) {
            String QR_JSON = (String) params[1];
            String serverURL = (String) params[0];//url
            String postParameters = "QR="+QR_JSON;
            try {
                URL url = new URL(serverURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(5000);
                con.setConnectTimeout(5000);
                con.setRequestMethod("POST");
                con.connect();
                OutputStream outputStream = con.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                int responseStatusCode = con.getResponseCode();
                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = con.getInputStream();
                } else {
                    inputStream = con.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String json = null;

                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json + "\n");
                }
                bufferedReader.close();
                return sb.toString().trim();

            } catch (Exception e) {

                return new String("Error: " + e.getMessage());
            }

        }
    }



    class IsOrdering extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            beforeJSON_forIsOrdering = result;
           // afterJSON_forISOrdering = result;
            //orderJSON = result;

        }


        @Override
        protected String doInBackground(String... params) {
            String res_ID3 = (String) params[1];
            String serverURL = (String) params[0];//url
            String postParameters = "res_ID=" + res_ID3;
            try {
                URL url = new URL(serverURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(5000);
                con.setConnectTimeout(5000);
                con.setRequestMethod("POST");
                con.connect();
                OutputStream outputStream = con.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                int responseStatusCode = con.getResponseCode();
                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = con.getInputStream();
                } else {
                    inputStream = con.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String json = null;

                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json.trim());
                }
                bufferedReader.close();
                return sb.toString().trim();

            } catch (Exception e) {

                return new String("Error: " + e.getMessage());
            }

        }
    }
//    void vibrate(){
//        Handler mHandler = new Handler();
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                vb.vibrate(500);
//            }
//        },1000);
//
//    }
    class IsOrderingAfter extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //afterJSON_forISOrdering = beforeJSON_forIsOrdering;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //orderJSON = result;
            afterJSON_forISOrdering = result;

            // 로그아웃시 afterJSON_forIsOrdering의 초기화가 이루어짐.
            if( !beforeJSON_forIsOrdering.equals(afterJSON_forISOrdering)   && (!result.equals("0"))  && result.matches("[+-]?\\d*(\\.\\d+)?")  ) {
                // 주문이 갱신!!

//                mToast.setText( beforeJSON_forIsOrdering+"\n"+afterJSON_forISOrdering);
//                mToast.show();
                //vibrate();
                vb.vibrate(500);
                beforeJSON_forIsOrdering = afterJSON_forISOrdering;

                //vibrate();

            }
        }


        @Override
        protected String doInBackground(String... params) {
            String res_ID3 = (String) params[1];
            String serverURL = (String) params[0];//url
            String postParameters = "res_ID=" + res_ID3;
            try {
                URL url = new URL(serverURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(5000);
                con.setConnectTimeout(5000);
                con.setRequestMethod("POST");
                con.connect();
                OutputStream outputStream = con.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                int responseStatusCode = con.getResponseCode();
                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = con.getInputStream();
                } else {
                    inputStream = con.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String json = null;

                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json.trim());
                }
                bufferedReader.close();
                return sb.toString().trim();

            } catch (Exception e) {

                return new String("Error: " + e.getMessage());
            }

        }
    }

    class MyTableName extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            title.setText(result);
            mToast.setText(result);
            mToast.show();

        }


        @Override
        protected String doInBackground(String... params) {
            String user_ID = (String) params[1];

            String serverURL = (String) params[0];//url
            String postParameters = "user_ID=" + user_ID ;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(5000);
                con.setConnectTimeout(5000);
                con.setRequestMethod("POST");
                con.connect();
                OutputStream outputStream = con.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                int responseStatusCode = con.getResponseCode();
                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = con.getInputStream();
                } else {
                    inputStream = con.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String json = null;

                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json.trim());
                }
                bufferedReader.close();
                return sb.toString().trim();

            } catch (Exception e) {

                return new String("Error: " + e.getMessage());
            }

        }
    }



    void vibrate(){
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                vb.vibrate(500);
            }
        },1000);

    }

    class CallingBefore extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            orderJSON = result;
            beforeCall = result;
            showCall();

        }


        @Override
        protected String doInBackground(String... params) {
            String res_ID3 = (String) params[1];
            String serverURL = (String) params[0];//url
            String postParameters = "res_ID=" + res_ID3;
            try {
                URL url = new URL(serverURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(5000);
                con.setConnectTimeout(5000);
                con.setRequestMethod("POST");
                con.connect();
                OutputStream outputStream = con.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                int responseStatusCode = con.getResponseCode();
                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = con.getInputStream();
                } else {
                    inputStream = con.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String json = null;

                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json.trim());
                }
                bufferedReader.close();
                return sb.toString().trim();

            } catch (Exception e) {

                return new String("Error: " + e.getMessage());
            }

        }
    }
    class CallingAfter extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //afterJSON_forISOrdering = beforeJSON_forIsOrdering;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            orderJSON = result;
            afterCall = result;
            // 로그아웃시 afterJSON_forIsOrdering의 초기화가 이루어짐.
            if( !beforeCall.equals(afterCall)     ) {
                // 주문이 갱신!!

//                mToast.setText( beforeJSON_forIsOrdering+"\n"+afterJSON_forISOrdering);
//                mToast.show();
                //vibrate();
                vb.vibrate(200);


                beforeCall = afterCall;
                showCall();
                //vibrate();

            }
        }


        @Override
        protected String doInBackground(String... params) {
            String res_ID3 = (String) params[1];
            String serverURL = (String) params[0];//url
            String postParameters = "res_ID=" + res_ID3;
            try {
                URL url = new URL(serverURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(5000);
                con.setConnectTimeout(5000);
                con.setRequestMethod("POST");
                con.connect();
                OutputStream outputStream = con.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                int responseStatusCode = con.getResponseCode();
                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = con.getInputStream();
                } else {
                    inputStream = con.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String json = null;

                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json.trim());
                }
                bufferedReader.close();
                return sb.toString().trim();

            } catch (Exception e) {

                return new String("Error: " + e.getMessage());
            }

        }
    }



    class initCall extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //afterJSON_forISOrdering = beforeJSON_forIsOrdering;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mToast.setText(result);
            mToast.show();
        }


        @Override
        protected String doInBackground(String... params) {
            String res_ID = (String) params[1];
            String serverURL = (String) params[0];//url
            String postParameters = "res_ID=" + res_ID;
            try {
                URL url = new URL(serverURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(5000);
                con.setConnectTimeout(5000);
                con.setRequestMethod("POST");
                con.connect();
                OutputStream outputStream = con.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                int responseStatusCode = con.getResponseCode();
                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = con.getInputStream();
                } else {
                    inputStream = con.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String json = null;

                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json.trim());
                }
                bufferedReader.close();
                return sb.toString().trim();

            } catch (Exception e) {

                return new String("Error: " + e.getMessage());
            }

        }
    }

}



