package com.example.main_project_two0523;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main_project_two0523.restaurants.ListViewAdapter_Restaurants;
import com.example.main_project_two0523.restaurants.ListViewItem_Restaurants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Activity_Restaurant_Main extends AppCompatActivity {
    private String Server_URL = ServerURL.getServerurl();

    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "res_ID";
    private static final String TAG_NAME = "res_name";
    private static final String TAG_RATING = "res_rating";


    String orderJSON;
    TextView text ,tTost;
    Context context;
    JSONArray peoples = null;
    ArrayList<HashMap<String, String>> personList;
    RecyclerView list;
    String res_id;
    String res_name;
    String res_rating;
    String price;
    String beforeJSON="", afterJSON="";

    Toolbar toolbar;
    private BackPressCloseHandler backPressCloseHandler;

    LinearLayoutManager linearLayoutManager;
    String res_ID;
    Toast mToast;
    Logout_Process logout;
    Timer timer;
    TimerTask TT;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_main);
        context = Activity_Restaurant_Main.this;
        mToast = Toast.makeText(context, "null",Toast.LENGTH_SHORT);
        logout = new Logout_Process(context);
        backPressCloseHandler = new BackPressCloseHandler(this);
        text = (TextView) findViewById(R.id.text);
        tTost = (TextView)findViewById(R.id.tTost);
        list = (RecyclerView) findViewById(R.id.listView);
        toolbar = (Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        linearLayoutManager = new LinearLayoutManager(this);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        list.addItemDecoration(new DividerItemDecoration(Activity_Restaurant_Main.this, DividerItemDecoration.VERTICAL));

        //  현재 로그인이 된 상태인지 확인.
        // 로그인이 되어있지 않다면 로그인 페이지( Login_Main.java )로 이동.
        // 정상적이지 않은 상태, 혹은 로그아웃 상태 이후 다시 Activity 가 load되었다면,

        //로그인 된 기록이 없으면 로그인 페이지로.
        if( Is_Login_Success.getLoginSuccess(context)!=1){
            startActivity(logout.Logout_Process());
        }

        // 인터넷이 연결되어야 이 앱을 사용 가능하다.
        if(NetworkStatus.getConnectivityStatus(context)== 3){
            goLoginActivity();
        }else{
            getData("http://115.22.203.91/select_restaurants.php");

        }




    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        timer.schedule(TT, 0, 500); //Timer 실행
    }
    // onStop은 Activity 가 삭제된 상태이다.
    @Override
    protected void onPause() { //작동함 // Main Activity가 종료하지 않고 정지상태
        super.onPause();
//        timer.cancel();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }
    protected void show_Restaurant_List() {
        try {
            JSONObject jsonObj = new JSONObject(orderJSON);//전체 JSON
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            ListViewAdapter_Restaurants adapter_res = new ListViewAdapter_Restaurants();
            list.setLayoutManager(linearLayoutManager);
            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);
                res_id = c.getString(TAG_ID);
                res_name = c.getString(TAG_NAME);
                res_rating = c.getString(TAG_RATING);


                ListViewItem_Restaurants li = new ListViewItem_Restaurants();
                li.set_IMG(Server_URL + "img/restaurants/m" + res_id + ".png");
                li.set_res_ID(res_id);
                li.set_res_name(res_name);
                li.set_res_rating( Math.round(  (Float.parseFloat(res_rating)*10))/10.0f);
                adapter_res.addItem(li);

                //int imageSource = this.getResources().getIdentifier("@drawable/m"+id, "drawable",this.getPackageName());
               // adapter_res.addItem(ContextCompat.getDrawable(this, imageSource ), id,name);
                // Server에서 이미지 불러오기.
            }



            adapter_res.setHasStableIds(true);
            list.setItemViewCacheSize(peoples.length());
            list.setAdapter(adapter_res);

             adapter_res.notifyDataSetChanged();

            adapter_res.setOnClickListener(new ListViewAdapter_Restaurants.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    ListViewItem_Restaurants data = adapter_res.getItem(position);
                    //  v.setBackgroundColor(Color.parseColor("#eeeeee"));   //15658734

                    //Toast.makeText(Restaurant_Main.this, data.get_res_ID() + ", " + data.get_res_name(), Toast.LENGTH_SHORT).show();
                    SaveSharedPreference_Restaurants.setRestaurantsID(context, data.get_res_ID().toString().trim()); // restaurants ID
                    SaveSharedPreference_Restaurants.setRestaurantsName(context, data.get_res_name().toString().trim()); // restaurants name

//                    Intent restaurant_intent = new Intent(getApplicationContext(), Activity_Menu_Main.class);
//                    restaurant_intent.putExtra("res_ID",data.get_res_ID());
//                    restaurant_intent.putExtra("res_name",data.get_res_name());
//                    startActivityForResult(restaurant_intent,0);


                    Intent go_Menu_intent = new Intent(context, Activity_Menu_Main.class);
                    startActivity(go_Menu_intent);
                }

            });



        } catch (JSONException e) {
            e.printStackTrace();
        }
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
//

//    // Logout Process : Logout에대한 처리.
//    public void Logout_Process(){
//        Is_Login_Success.clearUser(context);
//        // Logout process 진행 중 AutoLogin을 check하지 않은 경우 Login Token 삭제
//        if( (SaveSharedPreferenceCheckAutoLogin.getCheck(context)) != 1){
//            SaveSharedPreference.clearUser(context);
//        }
//        // 재 로그인을 위한 Login_Main Activity 로 이동
//        goLoginActivity();
//    }
//    //모든 Activity를 날리고 Login_Main Activity 로 이동.
    public void goLoginActivity(){
        Intent Login_Intent = new Intent(context, Activity_Login_Main.class);
        startActivity(Login_Intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                backPressCloseHandler.onBackPressed();
                return true;
            case R.id.menu_login:
                Toast.makeText(getApplicationContext(), "Login : User"+ Is_Login_Success.getLoginSuccess(Activity_Restaurant_Main.this) ,
                        Toast.LENGTH_SHORT).show();

                return true;
            case R.id.menu_logout:
                Toast.makeText(getApplicationContext(), "Logout",
                        Toast.LENGTH_SHORT).show();
                startActivity(logout.Logout_Process());  // logout에 대한 Intent를 return


                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        Log.d("test", "onPrepareOptionsMenu - 옵션메뉴가 " +
                "화면에 보여질때 마다 호출됨");

        if((Is_Login_Success.getLoginSuccess(context)!=1 )) { // 로그인 한 상태: 로그인은 안보이게, 로그아웃은 보이게
            menu.getItem(0).getSubMenu().getItem(0).setVisible(true); //Login Option Menu
            menu.getItem(0).getSubMenu().getItem(1).setVisible(false);//Logout Option Menu

        }else{ // 로그 아웃 한 상태 : 로그인 보이게, 로그아웃은 안보이게
            menu.getItem(0).getSubMenu().getItem(0).setVisible(false);
            menu.getItem(0).getSubMenu().getItem(1).setVisible(true);
        }
        return true;
    }

    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    StringBuilder sb = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                        ///  text.setText(json);
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                orderJSON = result;
                beforeJSON = result; // recently  // GetDATA

                // 다를 때만 show list
                show_Restaurant_List();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
 
}

////////============================= 추가될 수 있는 Code ==============================///////

// example 코드 IMG를 Server에서 가져와서 띄우기, Thread 이용
// 비효율적
//    Thread restaurnts_IMG = new Thread(){
//        @Override
//        public void run(){
//            try {
//                URL url = new URL(Server_URL+"img/m2.png");
//                HttpURLConnection con = (HttpURLConnection)url.openConnection();
//                con.setDoInput(true);
//                con.connect();
//                InputStream is = con.getInputStream();
//                bmImg = BitmapFactory.decodeStream(is);
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    };


//    //   Http Post로 주고 받기
//    class InsertData extends AsyncTask<String, Void, String>{
//        // ProgressDialog progressDialog;
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            //progressDialog = ProgressDialog.show(MainActivity.this,
//            //     "Please Wait", null, true, true);
//        }
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//            Log.d(Tag, result);
//            tTost.setText(result);
//
//        }
//
//
//        @Override
//        protected String doInBackground(String... params) {
//            String res_ID2 = (String)params[1];
//            String serverURL = (String)params[0];//url
//            String postParameters = "res_ID="+res_ID2;
//            try {
//                URL url = new URL(serverURL);
//
//                HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                con.setReadTimeout(5000);
//                con.setConnectTimeout(5000);
//                con.setRequestMethod("POST");
//                con.connect();
//
//                OutputStream outputStream = con.getOutputStream();
//                outputStream.write(postParameters.getBytes("UTF-8"));
//                outputStream.flush();
//                outputStream.close();
//
//
//                int responseStatusCode = con.getResponseCode();
//
//                InputStream inputStream;
//                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
//                    inputStream = con.getInputStream();
//                }
//                else{
//                    inputStream = con.getErrorStream();
//                }
//
//
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//                StringBuilder sb = new StringBuilder();
//                String json = null;
//
//                while((json = bufferedReader.readLine()) != null){
//                    sb.append(json +"\n");
//                }
//
//                bufferedReader.close();
//
//                return sb.toString().trim();
//
//            } catch (Exception e) {
//                return new String("Error: " + e.getMessage());
//            }
//        }
//    }