package com.example.main_project_two0523;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.main_project_two0523.dialog.Menu_Dialog;
import com.example.main_project_two0523.dialog.Menu_Dialog_ClickListener;
import com.example.main_project_two0523.menu.ListViewAdapter_Menu;
import com.example.main_project_two0523.menu.ListViewItem_Menu;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Activity_Menu_Main extends AppCompatActivity {
    private String Server_URL = ServerURL.getServerurl();


    String orderJSON;
    TextView text, title,isEmpty;
    Bitmap bmImg;
    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "menu_ID";
    private static final String TAG_NAME = "menu_name";
    private static final String TAG_price = "menu_price";

    public static Toast mToast ;
    JSONArray peoples = null;
    ArrayList<HashMap<String, String>> personList;
    RecyclerView list;
    Button ordering;


    String id;
    String name;
    String price;

    Toolbar toolbar;

    Menu_Dialog menu_dialog;
    DBHelper helper;
    SQLiteDatabase db_order;

    String res_ID;
    String res_name;
    LinearLayoutManager linearLayoutManager;

    Logout_Process logout;
    Context context;
    String beforeJSON="", afterJSON="";
    Runnable r ;
    int Number_Of_Menu; // Dialog 처음 시작 시 입력되는 가격, menu 의 개수에 따라 first_menu_price의 배수만큼 증가.
    int cur_price; // dialog 에서 사용하는 현재 가격
    int first_price;

    ScheduledThreadPoolExecutor exec;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_main);
        context = Activity_Menu_Main.this;
        linearLayoutManager = new LinearLayoutManager(this);
        logout = new Logout_Process(context);


        // Menu Activity 처리
        ordering = (Button) findViewById(R.id.ordering);
        text = (TextView) findViewById(R.id.text);
        title = (TextView) findViewById(R.id.title);
        list = (RecyclerView) findViewById(R.id.listView);
        // Toast 초기화
        mToast = Toast.makeText(context, "null",Toast.LENGTH_SHORT);

        isEmpty = (TextView)findViewById(R.id.isEmpty);
        // SQLITE 초기화

        helper = new DBHelper(context);
        db_order = helper.getWritableDatabase();
        //helper.onUpgrade(db_order, 1, 2);
        helper.onCreate(db_order);
        db_order.close();



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// back 버튼
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        title.setText(SaveSharedPreference_Restaurants.getRestaurantsName(context)+"(메뉴)");




        InsertData task = new InsertData();
        task.execute("http://115.22.203.91/show_menu.php", SaveSharedPreference_Restaurants.getRestaurantsID(context).trim());

        r = new Runnable() {
            public void run() {
                InsertDataAfter task2 = new InsertDataAfter();
                task2.execute("http://115.22.203.91/show_menu.php", SaveSharedPreference_Restaurants.getRestaurantsID(context).trim());
            }
        };

        exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleWithFixedDelay(r , 0, 2000, TimeUnit.MILLISECONDS);




        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, Activity_Bill_Main.class);
                startActivity(it);
            }
        });
    }



    @Override
    public void onPause() {
        super.onPause();

        if(!exec.isTerminated()){
            exec.shutdown();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(exec.isTerminated() ){
//            mToast.setText("실시간 수신 재실행 activity Focusing");
//            mToast.show();
            exec = new ScheduledThreadPoolExecutor(1);
            exec.scheduleWithFixedDelay(r , 0, 1000, TimeUnit.MILLISECONDS);
        }
    }


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


    protected void show_Menu_List() {
        try {

            JSONObject jsonObj = new JSONObject(orderJSON);//전체 JSON
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            ListViewAdapter_Menu adapter_menu = new ListViewAdapter_Menu();
            list.setLayoutManager(linearLayoutManager);
            linearLayoutManager.setItemPrefetchEnabled(true);



            int idx = peoples.length();
            if(idx > 0 ){

                isEmpty.setVisibility(View.GONE);
            }else{
                isEmpty.setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < idx; i++) {
                JSONObject c = peoples.getJSONObject(i);
                id = c.getString(TAG_ID);
                name = c.getString(TAG_NAME);
                price = c.getString(TAG_price);



                ListViewItem_Menu li = new ListViewItem_Menu();
                li.set_IMG(Server_URL + "img/menu/m" + id + ".png");
                li.set_menu_ID(id);
                li.set_menu_name(name);
                li.set_menu_price(price);

                adapter_menu.addItem(li);
            }

            adapter_menu.setHasStableIds(true);
            list.setItemViewCacheSize(adapter_menu.getItemCount()/2);
            list.setAdapter(adapter_menu);


            adapter_menu.setOnClickListener(new ListViewAdapter_Menu.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    ListViewItem_Menu data = adapter_menu.getItem(position);
                    //menu_dialog.setTitle(data.get_menu_name());
                    show_dialog(data);
                    menu_dialog.setText_Title(data.get_menu_name());
                    menu_dialog.setIMG(Server_URL + "/img/menu/m" + data.get_menu_ID() + ".png");
                    menu_dialog.set_menu_price(data.get_menu_price());

                    cur_price       = Integer.valueOf(data.get_menu_price());
                    first_price     = cur_price;
                    Number_Of_Menu  = 1;
                    id = data.get_menu_ID();
                    name = data.get_menu_name();
                }
            });

            ordering.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent order_intent = new Intent(context, Activity_Order_Main.class);
                    startActivity(order_intent);

                }
            });




        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void show_dialog(ListViewItem_Menu data) {


        menu_dialog = new Menu_Dialog(Activity_Menu_Main.this, new Menu_Dialog_ClickListener() {
            @Override
            public void onPositiveClick() {
                String temp="";

                db_order = helper.getWritableDatabase();
                String sql = " INSERT INTO selected_menu( res_ID, menu_ID, menu_name, menu_price, number_of_menu) " +
                        " values( '"+SaveSharedPreference_Restaurants.getRestaurantsID(context)+"', '"+Integer.parseInt(id)+"', '"+name+"', '"+(first_price)+"', '"+Number_Of_Menu+"' );";
//                String sql = " INSERT INTO selected_menu( res_ID, menu_ID, menu_name, menu_price, number_of_menu) " +
//                        " values( '"+SaveSharedPreference_Restaurants.getRestaurantsID(context)+"', '"+Integer.parseInt(id)+"', '"+name+"', '"+(first_price)+"', '"+Number_Of_Menu+"' );";

                db_order.execSQL(sql);


                db_order.close();
                menu_dialog.dismiss();
            }
            @Override
            public void onNegativeClick() {

                menu_dialog.dismiss();
            }
            @Override
            public void onNumberUpClick() {
                if (Number_Of_Menu >= 10) {
                    mToast.setText("10개 초과 주문 할 수 없습니다.\nnum : "+Number_Of_Menu);
                    mToast.show();
                } else {
                    ++Number_Of_Menu;
                    menu_dialog.set_num(Number_Of_Menu);
                    cur_price += first_price ;
                    menu_dialog.set_menu_price(String.valueOf(cur_price ));

                }


            }

            @Override
            public void onNumberDownClick() {
                if (Number_Of_Menu <= 1) {
                    mToast.setText("1개 미만 주문 할 수 없습니다\nnum : "+Number_Of_Menu);
                    mToast.show();

                } else {
                    --Number_Of_Menu;
                    menu_dialog.set_num(Number_Of_Menu);
                    cur_price -= first_price ;
                    menu_dialog.set_menu_price(String.valueOf(cur_price));
                }
            }
        });
        menu_dialog.setCanceledOnTouchOutside(true); // 다이얼로그 바깥 터치시 다이얼로그
        menu_dialog.setCancelable(true);// 다이얼로그 back 버튼으로 취소 가능
        menu_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        menu_dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        menu_dialog.show();
    }

    class InsertData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            orderJSON = result;
            beforeJSON = result;
            show_Menu_List();


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
                    sb.append(json + "\n");
                }
                bufferedReader.close();
                return sb.toString().trim();

            } catch (Exception e) {

                return new String("Error: " + e.getMessage());
            }

        }
    }




    class InsertDataAfter extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            orderJSON = result;
            afterJSON = result;

            if( beforeJSON.compareTo(afterJSON)!=0){
                show_Menu_List();
                beforeJSON = afterJSON;
//                if(menu_dialog.isShowing()){
//                    menu_dialog.cancel();// 메뉴를 고르는 와중에 메뉴 사용이 불가능 하게 된다면 삭제.
//                    mToast.setText("선택한 메뉴는 현재 이용할 수 없습니다!!!");
//                    mToast.show();
//                }
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
                    sb.append(json + "\n");
                }
                bufferedReader.close();
                return sb.toString().trim();

            } catch (Exception e) {

                return new String("Error: " + e.getMessage());
            }

        }
    }

}