package com.example.main_project_two0523;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.main_project_two0523.bill.ListViewAdapter_Bill;
import com.example.main_project_two0523.bill.ListViewItem_Bill;
import com.example.main_project_two0523.dialog.Menu_Dialog;
import com.example.main_project_two0523.dialog.Menu_Dialog_ClickListener;
import com.example.main_project_two0523.dialog.Rating_Dialog;
import com.example.main_project_two0523.dialog.Rating_Dialog_ClickListener;
import com.example.main_project_two0523.menu.ListViewItem_Menu;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Activity_Bill_Main extends  AppCompatActivity{
    private String Server_URL = ServerURL.getServerurl();
    String orderJSON;
    TextView  title;
    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "menu_ID";
    private static final String TAG_NAME = "menu_name";
    private static final String TAG_price = "menu_price";
    private static final String TAG_NUM = "menu_num";


    public static Toast mToast ;
    JSONArray peoples = null;
    RecyclerView list;
    Button pay, calling;
    TextView totalPrice, isEmpty;
    String menu_ID;
    String menu_name;
    String menu_price;
    String menu_num;
    Toolbar toolbar;
    LinearLayoutManager linearLayoutManager;
    Logout_Process logout;
    Context context;
    MoneyFormat mF;
    MultiFormatWriter multiFormatWriter;
    BitMatrix bitMatrix;
    BarcodeEncoder barcodeEncoder;
    Bitmap bitmap;

    // 계산서의 실시간 갱신.
    String beforeJSON="", afterJSON="";

    int total_price=0;
    Rating_Dialog rating_dialog;
    int temprating = 0;

    Timer timer;
    TimerTask TT;
    ScheduledThreadPoolExecutor exec;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bill_main);
        linearLayoutManager = new LinearLayoutManager(this);
        context = Activity_Bill_Main.this;
        logout = new Logout_Process(context);
        mF = new MoneyFormat();// Money Format에 대한 객체 생성 및, 초기화


        pay = (Button) findViewById(R.id.pay);
        calling = (Button)findViewById(R.id.calling);

        isEmpty = (TextView)findViewById(R.id.isEmpty);
        title = (TextView) findViewById(R.id.title);
        list = (RecyclerView) findViewById(R.id.listView);
        totalPrice = (TextView)findViewById(R.id.totalPrice);
        // Toast 초기화
        mToast = Toast.makeText(context, "null",Toast.LENGTH_SHORT);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// back 버튼
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        title.setText("계산서");
        // 리스트뷰의 각 리스트 아래 구분선

        //list.addItemDecoration(new DividerItemDecoration(Menu_Main.this, DividerItemDecoration.VERTICAL));

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // RatingBar (평점) 기본 값 : 5
                SaveSharedPreference_Rating.setRating(context, 5);
                if ( total_price == 0  ){// 최종 가격이 0 이라면, 다시 메뉴 선택 화면으로 이동
                    mToast.setText("주문한 메뉴가 없습니다.\n메뉴선택 페이지로 이동합니다.");
                    mToast.show();
                    Intent go_select_menu  = new Intent(context, Activity_Menu_Main.class);
                    go_select_menu.addFlags( FLAG_ACTIVITY_CLEAR_TOP  );
                    startActivity(go_select_menu);
                }else{// 구매한 가격이 있다먄 평점 등록 후, 결제 QR 생성
                    show_dialog();
                }
            }
        });

        calling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //STAFF APP TABLE을 할당해야 사용할 수 있는 기능.

                CallStaff callstaff = new CallStaff();
                callstaff.execute(Server_URL+ "call_staff.php", SaveSharedPreference.getUserID(context),
                                                                SaveSharedPreference_Restaurants.getRestaurantsID(context));
            }
        });

        ShowBill task = new ShowBill();
        task.execute("http://115.22.203.91/show_bill.php", SaveSharedPreference.getUserID(context));

        Runnable r = new Runnable() {
            public void run() {
                ShowBillAfter task2 = new ShowBillAfter();
                task2.execute("http://115.22.203.91/show_bill.php", SaveSharedPreference.getUserID(context));
            }
        };

        exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleWithFixedDelay(r , 0, 1000, TimeUnit.MILLISECONDS);




    }
    public void show_dialog() {
        rating_dialog = new Rating_Dialog(context, new Rating_Dialog_ClickListener() {
            @Override
            public void onPositiveClick() {
                Intent qr_intent = new Intent(context, CreateQR.class);
                //total_price 결제 금액
                qr_intent.putExtra("user_ID",SaveSharedPreference.getUserID(context));
                qr_intent.putExtra("total_price",total_price);
                startActivityForResult(qr_intent,0);
                rating_dialog.dismiss();
            }
            @Override
            public void onNegativeClick() {
                rating_dialog.dismiss();
            }


        });

        rating_dialog.OnRatingBarChangeListenerstener(new Rating_Dialog.OnRatingBarChangeListenerstener() {
            @Override
            public void onChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                mToast.setText("평가해주세요!! : " +rating);
//                mToast.show();
                SaveSharedPreference_Rating.setRating(context, rating);
            }
        });
        rating_dialog.setCanceledOnTouchOutside(true); // 다이얼로그 바깥 터치시 다이얼로그
        rating_dialog.setCancelable(true);// 다이얼로그 back 버튼으로 취소 가능
        rating_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        rating_dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        rating_dialog.show();

    }

    @Override
    protected void onPause() {
        if(!exec.isTerminated()){
            exec.shutdown();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
//             timer.schedule(TT, 0, 500); //Timer 실행

    }

    // onStop은 Activity 가 삭제된 상태이다.

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_login:
                Toast.makeText(getApplicationContext(), "Login : User"+ Is_Login_Success.getLoginSuccess(context) ,
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_logout:
                Toast.makeText(getApplicationContext(), "Logout",
                        Toast.LENGTH_SHORT).show();
               // timer.cancel();
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




    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    protected void show_Bill_List() {
        try {
            JSONObject jsonObj = new JSONObject(orderJSON);//전체 JSON
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            ListViewAdapter_Bill adapter_bill = new ListViewAdapter_Bill();
            list.setLayoutManager(linearLayoutManager);
            total_price=0;

            int idx = peoples.length();
            if(idx > 0 ){

                isEmpty.setVisibility(View.GONE);
            }else{
                isEmpty.setVisibility(View.VISIBLE);
            }
            int temp_price = 0, temp_num=0;
            for (int i = 0; i < idx; i++) {
                JSONObject c = peoples.getJSONObject(i);
                menu_ID = c.getString(TAG_ID);
                menu_name = c.getString(TAG_NAME);
                menu_price = c.getString(TAG_price);
                menu_num = c.getString(TAG_NUM);

///Integer.parseInt(menu_num);
                temp_price = Integer.parseInt(menu_price);
                temp_num=Integer.parseInt(menu_num);
                ListViewItem_Bill li = new ListViewItem_Bill();
                li.set_IMG(Server_URL + "img/menu/m" + menu_ID + ".png");
                li.set_menu_ID(menu_ID);
                li.set_menu_name(menu_name);
                li.set_menu_price( menu_price );
                li.set_menu_number(menu_num);
                total_price+=( temp_price*temp_num );

                adapter_bill.addItem(li);
            }
            totalPrice.setText(mF.myFormatter.format(total_price)+" 원");

            adapter_bill.setHasStableIds(true);
            list.setItemViewCacheSize(adapter_bill.getItemCount()/2);
            list.setAdapter(adapter_bill);

//            adapter_bill.notifyDataSetChanged();;

            adapter_bill.setOnClickListener(new ListViewAdapter_Bill.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    // Client는 RecyclerView for Bill을 Control할 수 없음.
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    class ShowBill extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            orderJSON = result;
            beforeJSON = result;
            show_Bill_List();
        }
        @Override
        protected String doInBackground(String... params) {
            String user_ID = (String) params[1];
            String serverURL = (String) params[0];//url
            String postParameters = "user_ID="+ user_ID+"&res_ID="+SaveSharedPreference_Restaurants.getRestaurantsID(context).toString().trim();
            try {
                URL url = new URL(serverURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(400);
                con.setConnectTimeout(400);
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




    class ShowBillAfter extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            orderJSON = result;
            afterJSON = result;

            // 서버 통신 결과가 다를때만 갱신 이외에는 무시, OVERHEAD 적음.
            if( !beforeJSON.equals(afterJSON)){
                beforeJSON = afterJSON;
                show_Bill_List();
            }
        }
        @Override
        protected String doInBackground(String... params) {
            String user_ID = (String) params[1];
            String serverURL = (String) params[0];//url
            String postParameters = "user_ID="+ user_ID+"&res_ID="+SaveSharedPreference_Restaurants.getRestaurantsID(context).toString().trim();

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


    class CallStaff extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            mToast.setText(result);
            mToast.show();

        }


        @Override
        protected String doInBackground(String... params) {
            String user_ID = (String) params[1];
            String res_ID = (String) params[2];

            String serverURL = (String) params[0];//url
            String postParameters = "user_ID=" + user_ID+"&res_ID="+res_ID;



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