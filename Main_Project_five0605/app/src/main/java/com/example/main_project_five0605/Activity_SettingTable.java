package com.example.main_project_five0605;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.main_project_five0605.settingtable.ListViewAdapter_Table;
import com.example.main_project_five0605.settingtable.ListViewItem_Table;
import com.example.main_project_five0605.showuserorder.ListViewAdapter_ShowUserOrder;
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

public class Activity_SettingTable extends AppCompatActivity {
    private String Server_URL = ServerURL.getServerurl();
    private Toast mToast;
    Toolbar toolbar;
    TextView title;
    RecyclerView list;
    Context context;
    Logout_Process logout;
    MoneyFormat mF;
    String Qr_Scan;
    private IntentIntegrator qrScan;

    String orderJSON="";
    private static final String TAG_RESULTS = "result";

    private BackPressCloseHandler backPressCloseHandler;

    String beforeJSON, afterJSON;
    JSONArray peoples = null;

    Button btn_table;

    String user_ID="";
    String user_ID2="";
    LinearLayoutManager linearLayoutManager;
    ScheduledThreadPoolExecutor exec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_table_main);


        context = Activity_SettingTable.this;
        linearLayoutManager = new LinearLayoutManager(this);
        title = (TextView) findViewById(R.id.title);
        list = (RecyclerView) findViewById(R.id.listView);
        mToast = Toast.makeText( context, "null",Toast.LENGTH_SHORT);
        btn_table = (Button)findViewById(R.id.btn_table);
        backPressCloseHandler = new BackPressCloseHandler(this);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        logout = new Logout_Process(context);

//        Intent user_intent = getIntent();
//        user_ID = user_intent.getStringExtra("user_ID");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// back 버튼
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        title.setText(SAVE_CLIENT.getUser_ID(context)+"님 고객 테이블 설정");
        title.setTextSize(15);




        ShowSettingTable task = new ShowSettingTable();
        task.execute(Server_URL+ "show_table.php",  SaveSharedPreference_Restaurants.getRestaurantsID(context));

//        Runnable r = new Runnable() {
//            public void run() {
//                ShowSettingTableAfter task2 = new ShowSettingTableAfter();
//                task2.execute(Server_URL+ "show_table.php",  SaveSharedPreference_Restaurants.getRestaurantsID(context));
//            }
//        };
//        exec = new ScheduledThreadPoolExecutor(1);
//        exec.scheduleWithFixedDelay(r , 0, 500, TimeUnit.MILLISECONDS);

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        backPressCloseHandler.onBackPressed();
    }

//    @Override
//    protected void onDestroy() {
//        if(!exec.isTerminated()){
//            exec.shutdown();
//        }
//        super.onDestroy();
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.logout:
                Toast.makeText(getApplicationContext(), "Logout" ,
                        Toast.LENGTH_SHORT).show();
                startActivity(logout.Logout_Process());  // logout에 대한 Intent를 return

                return true;
            case R.id.qr_scan:
//                Toast.makeText(getApplicationContext(), "QR SCAN",
//                        Toast.LENGTH_SHORT).show();
                qrScan = new IntentIntegrator(Activity_SettingTable.this);
                qrScan.setOrientationLocked(false);
                qrScan.setPrompt("스마트폰을 여기로!!!");
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
    protected void show_table() {
        try {
            int idx=0;
            JSONObject jsonObj = new JSONObject(orderJSON);//전체 JSON
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            ListViewAdapter_Table adapter_menu = new ListViewAdapter_Table();
            list.setLayoutManager(linearLayoutManager);
            linearLayoutManager.setItemPrefetchEnabled(true);


            ListViewItem_Table li;
            idx = peoples.length();
//            if(idx==0){// 해당 고객의 주문이 없음.
//                Intent inMain = new Intent(context, Activity_MyRestaurant_Main.class);
//                inMain.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(inMain);
//
//            }

            String a="";
            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);
                li = new ListViewItem_Table();
                li.set_ID( c.getString("table_ID") );
                li.set_occ(c.getString("occ"));
              //  a+=c.getString("occ");

                adapter_menu.addItem(li);

            }


            adapter_menu.setHasStableIds(true);
            list.setItemViewCacheSize(adapter_menu.getItemCount()/2);
            list.setAdapter(adapter_menu);


            adapter_menu.setOnClickListener(new ListViewAdapter_Table.OnItemClickListener() {
                ListViewItem_Table data;
                @Override
                public void onSettingTable(View v, int position) {
                    data = adapter_menu.getItem(position);


//                    mToast.setText(Server_URL+ "setting_table_num.php"+", "+ SaveSharedPreference_Restaurants.getRestaurantsID(context)+", "+
//                            data.get_ID()+", "+ data.get_occ() +", "+ SAVE_CLIENT.getUser_ID(context));
                    SettingTableNum task = new SettingTableNum();
                    task.execute(Server_URL+ "setting_table_num.php", SaveSharedPreference_Restaurants.getRestaurantsID(context),
                            data.get_ID(),data.get_occ().equals("0")?"1":"0" , SAVE_CLIENT.getUser_ID(context)
                            );



                }


            });



        } catch (JSONException e) {
            e.printStackTrace();
        }
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


    class QR_Pay extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            orderJSON = result;
            Intent goMyMain = new Intent(context, Activity_MyRestaurant_Main.class); // 이동하는 context target은 항상 동일하게 Login Activity 이다.
            goMyMain.addFlags(  FLAG_ACTIVITY_CLEAR_TOP  );
            startActivity(goMyMain);

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
    class ShowSettingTable extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            orderJSON = result;
            beforeJSON = result;
            show_table();
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


    class SettingTableNum extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            
           if(result.equals("-1")){
               //테이블 선점 성공
                mToast.setText("테이블 할당실패, 태호에게 문의"+" "+result);
                mToast.show();
           }else{
               //테이블 할당 성공
                SAVE_CLIENT.setUser_Table(context, result);
                Intent goback = new Intent(context, Activity_ShowUserOrder.class); // 이동하는 context target은 항상 동일하게 Login Activity 이다.
                goback.addFlags(  FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(goback);
           }
        }


        @Override
        protected String doInBackground(String... params) {

            String res_ID = (String)params[1];
            String table_ID = (String)params[2];
            String occ = (String)params[3];
            String user_ID  =(String)params[4];
            String serverURL = (String) params[0];//url
            String postParameters = "res_ID="+res_ID+"&table_ID="+table_ID+"&occ="+occ+"&user_ID="+user_ID;
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