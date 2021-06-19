package com.example.main_project_two0523;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

import com.example.main_project_two0523.orders.ListViewAdapter_Orders;
import com.example.main_project_two0523.orders.ListViewItem_Orders;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Activity_Order_Main extends AppCompatActivity {
    private String Server_URL = ServerURL.getServerurl();

    private Toast mToast;
    Toolbar toolbar;
    LinearLayoutManager linearLayoutManager;
    DBHelper helper;
    SQLiteDatabase db_order, db_order_write;
    TextView title, text, finalPrice, isEmpty;
    RecyclerView list;
    Button reset, bill, ordering;
    ListViewAdapter_Orders selected_menu_Adapter;
    Context context;
    Logout_Process logout;
    MoneyFormat mF;
    Order_ProgressDIalog progressDig;
    ArrayList<Orders_JSON> OJ = null;
    String ordersJSON="";
    String postParameters;// 주문 시에 return 되는 input data가 일치 하는지 확인하기 위해 global variable로 선언

//    ArrayList<> // JSON형태로 보낼것.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_main);
        context = Activity_Order_Main.this;
        progressDig = new Order_ProgressDIalog(context);
        mF = new MoneyFormat();
        text = (TextView) findViewById(R.id.text);
        title = (TextView) findViewById(R.id.title);
        list = (RecyclerView) findViewById(R.id.listView);
        mToast = Toast.makeText(Activity_Order_Main.this, "null",Toast.LENGTH_SHORT);
        reset = (Button) findViewById(R.id.reset);
        bill = (Button) findViewById(R.id.bill);
        ordering = (Button) findViewById(R.id.ordering);
        isEmpty = (TextView)findViewById(R.id.isEmpty);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        finalPrice = (TextView)findViewById(R.id.totalPrice);
        // Logout 을 위한 객체 초기화
        logout = new Logout_Process(context);
        linearLayoutManager = new LinearLayoutManager(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// back 버튼
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        title.setText("주문 페이지");

        helper = new DBHelper(Activity_Order_Main.this);
        db_order = helper.getWritableDatabase();
        helper.onCreate(db_order); // 인수는 아무거나 입력하면 됨.
        show_selected_menu(); // RecyclerView 뿌리기



        bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, Activity_Bill_Main.class);
                startActivity(it);
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetRecylerView();

            }
        });

        ordering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db_order = helper.getReadableDatabase();
                Cursor c = db_order.rawQuery("select  menu_ID, menu_name, sum(menu_price), sum(number_of_menu) from selected_menu  where res_ID ='"+SaveSharedPreference_Restaurants.getRestaurantsID(context).toString().trim()+"' group by  menu_ID; ", null);
                OrderToPHP OTP = new OrderToPHP();

                // ordering data JSON parsing
                ordersJSON="{\"orders\":[";
                while(c.moveToNext()){
                    ordersJSON +="{\"user_ID\":\""+SaveSharedPreference.getUserID(context)+"\",";
                    //restaurants ID ( ? )
                    ordersJSON +="\"res_ID\":\""+SaveSharedPreference_Restaurants.getRestaurantsID(context)+"\",";

                    ordersJSON +="\"menu_ID\":\""+c.getString(0)+"\",";
                    ordersJSON +="\"menu_name\":\""+c.getString(1)+"\",";
                    ordersJSON +="\"menu_price\":\""+Integer.parseInt(c.getString(2) )+"\",";
                    //Last record의 경우 마지막, 가 필요하지 않음.
                    if(c.isLast()==true){ //next가 없다면. 콤마를 제외 이것이 마지막 record
                        ordersJSON +="\"menu_num\":\""+c.getString(3)+"\"}";
                    }else{
                        ordersJSON +="\"menu_num\":\""+c.getString(3)+"\"},";
                    }
                }
                ordersJSON+="]}";
                //ordering.php에 post형식으로 통신
                OTP.execute(Server_URL+"ordering.php",ordersJSON);


               // mToast.setText(ordersJSON);
               // mToast.show();
                c.close();
                db_order.close();

            }
        });

        // menu name별 각 가격과 menu 개수를 query 하여 총 가격을 구한다.
        db_order = helper.getReadableDatabase();
        Cursor c = db_order.rawQuery(
                "select menu_price, sum(number_of_menu) from  " +
                        "(select menu_ID, menu_name, menu_price, number_of_menu from selected_menu where res_ID='" + SaveSharedPreference_Restaurants.getRestaurantsID(context) + "'  " +
                        " order by _id DESC ) m " +
                        "group by menu_ID   ; ", null);
        String s = "";
        int to=0;
        while (c.moveToNext()) {
            to+= Integer.parseInt(c.getString(0))*Integer.parseInt(c.getString(1));
        }
        finalPrice.setText(mF.myFormatter.format(to) +" 원");


        c.close();
        db_order.close();

    }


    public void ResetRecylerView(){
        db_order = helper.getWritableDatabase();
        db_order.execSQL("delete from selected_menu  where res_ID = '"+SaveSharedPreference_Restaurants.getRestaurantsID(context)+"' ; ");
        show_selected_menu();
        selected_menu_Adapter.notifyDataSetChanged();
        db_order.close();
        finalPrice.setText("0 원");
    }

    // 선택된 Menu에 대한 list를 RecyclerVierw로 _id를 descening order로 sorting 한 scalar table의 menu_name별로 각 column을 뽑는다.
    public void show_selected_menu() {
        selected_menu_Adapter = new ListViewAdapter_Orders();
        int cnt = 0;
        db_order = helper.getReadableDatabase();
        Cursor c = db_order.rawQuery(
                "select menu_ID, menu_name, menu_price, sum(number_of_menu) from  " +
                        "(select menu_ID, menu_name, menu_price, number_of_menu from selected_menu where res_ID='" + SaveSharedPreference_Restaurants.getRestaurantsID(context) + "'  " +
                        " order by _id DESC ) m " +
                        "group by menu_ID   ; ", null);
        String s = "";
        int to=0;
        while (c.moveToNext()) {
            cnt++;
            ListViewItem_Orders li = new ListViewItem_Orders();
            li.set_IMG(Server_URL + "img/menu/m" + c.getString(0) + ".png");
            li.set_menu_ID(c.getString(0));
            li.set_menu_name(c.getString(1));
            li.set_menu_price(Integer.parseInt(c.getString(2)) +"" );
            li.set_menu_number(c.getString(3));
            to+= Integer.parseInt(c.getString(2))*Integer.parseInt(c.getString(3));
            s+= c.getString(1)+", "+c.getString(2)+", "+c.getString(3)+ "\n";
            selected_menu_Adapter.addItem(li);
        }
//        mToast.setText(s+", 현재 최종가격 " + to);
//        mToast.show();
        finalPrice.setText(mF.myFormatter.format(to) + " 원");

        if (cnt > 0) {
            isEmpty.setVisibility(View.GONE);
        } else {
            isEmpty.setVisibility(View.VISIBLE);
        }
        //selected_menu_Adapter.setHasStableIds(true);
        list.setItemViewCacheSize(cnt);
        selected_menu_Adapter.notifyDataSetChanged();
        list.setLayoutManager(linearLayoutManager);

        list.setAdapter(selected_menu_Adapter);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Snackbar.make(v, "Here's a Snackbar", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
//                mToast.setText("Restaurant's fab");
//                mToast.show();
                Intent it = new Intent(context, Activity_Bill_Main.class);
                startActivity(it);
            }
        });
        selected_menu_Adapter.setOnClickListener(new ListViewAdapter_Orders.OnItemClickListener() {
            ListViewItem_Orders data;

            @Override
            public void onItemClick(View v, int position) {
                // list click, -> 사용 하지 않는곳 삭제 해도 무방 , interface
            }

            @Override
            public void onItemNumUpClick(View v, int position) {
                // 메뉴 하나에 대해서 Query 진행.
                // 메뉴의 first price와 number를 곱하면 해당 메뉴별 total price가 된다.

                data = selected_menu_Adapter.getItem(position);
                db_order = helper.getReadableDatabase();
                // 해당 메뉴에 대한
                int temp_price = 0, temp_num = 0;


                Cursor c = db_order.rawQuery("select menu_price, sum(number_of_menu) from selected_menu where menu_ID='"+data.get_menu_ID()+
                        "' group by menu_ID  order by _id DESC " +
                        "  ;  ",null);
                if (c.moveToNext()) {
                    temp_price = c.getInt(0);
                    temp_num = c.getInt(1);
                }

                db_order = helper.getWritableDatabase();
                db_order.execSQL("delete from selected_menu where menu_ID='"+data.get_menu_ID().trim()+"'; ");

                temp_num = temp_num + 1;
                db_order.execSQL("INSERT INTO selected_menu( res_ID,  menu_ID, menu_name, menu_price, number_of_menu) values('"
                        + SaveSharedPreference_Restaurants.getRestaurantsID(context) + "','"
                        + data.get_menu_ID().trim() + "','"
                        + data.get_menu_name().trim() + "','"
                        + temp_price  + "', '"
                        + temp_num + "');");


                db_order.close();
                c.close();
                show_selected_menu();
                selected_menu_Adapter.notifyDataSetChanged();
            }

            @Override
            public void onItemNumDownClick(View v, int position) {
                data = selected_menu_Adapter.getItem(position);

                db_order = helper.getReadableDatabase();
                // 해당 메뉴에 대한
                int temp_price = 0, temp_num = 0;


                Cursor c = db_order.rawQuery("select menu_price, sum(number_of_menu) from selected_menu where menu_ID='"+data.get_menu_ID()+"' group by menu_ID  order by _id DESC " +
                        "  ;  ",null);
                if (c.moveToNext()) {
                    temp_price = c.getInt(0);
                    temp_num = c.getInt(1);
                }


                if (temp_num == 1) {
                    mToast.setText("1개 미만 주문 할 수 없습니다. ");
                    mToast.show();
                } else {


                    db_order = helper.getWritableDatabase();
                    db_order.execSQL("delete from selected_menu where menu_ID='"+data.get_menu_ID().trim()+"'; ");

                    // db_order = helper.getWritableDatabase();
                    temp_num = temp_num - 1;

                    db_order.execSQL("INSERT INTO selected_menu( res_ID,  menu_ID, menu_name, menu_price, number_of_menu) values('"
                            + SaveSharedPreference_Restaurants.getRestaurantsID(context) + "','"
                            + data.get_menu_ID().trim() + "','"
                            + data.get_menu_name().trim() + "','"
                            + temp_price  + "', '"
                            + temp_num + "');");


                    c.close();

                }
                db_order.close();

                show_selected_menu();
                selected_menu_Adapter.notifyDataSetChanged();



            }

            @Override
            public void destroyItemClick(View v, int position) {

                data = selected_menu_Adapter.getItem(position);

                String t_menu_name = "";
                t_menu_name = data.get_menu_name();
                mToast.setText(data.get_menu_name().trim() + " 삭제");
                mToast.show();
                db_order = helper.getWritableDatabase();
                db_order.execSQL("delete from selected_menu where   menu_name ='" + data.get_menu_name().trim().toString() + "' ; ");
                db_order.close();

                show_selected_menu();
                selected_menu_Adapter.notifyDataSetChanged();


                // destroyItemClick에 대해서는 추가로 각 메뉴의 가격을 계산할 필요없이 RecyclerView의 갱신 내용을
                // 재 query하면 된다.
                int ftp_total = 0;

                int ftp_first = 0; // 각 메뉴에 대한 각 가격
                db_order = helper.getReadableDatabase();

                Cursor c = db_order.rawQuery("select sum(menu_price), sum(number_of_menu) from selected_menu  group by menu_name; ", null);
                while (c.moveToNext()) {
                    // 각 menu 하나의 가격을 계산
                    ftp_total += ftp_first = Integer.parseInt(c.getString(0))  * (Integer.parseInt(c.getString(1)));
                }
                finalPrice.setText(mF.myFormatter.format(ftp_total) + " 원");

                c.close();
                db_order.close();
            }
        });


    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        goMenu();

    }

    public void goMenu(){
        Intent in = new Intent(context, Activity_Menu_Main.class);
        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(in);
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

//        mToast.setText(menu.getItem(0).getSubMenu().getItem(1).getTitle());
//        mToast.show();
        if((Is_Login_Success.getLoginSuccess(context)!=1 )) { // 로그인 한 상태: 로그인은 안보이게, 로그아웃은 보이게
            menu.getItem(0).getSubMenu().getItem(0).setVisible(true); //Login Option Menu
            menu.getItem(0).getSubMenu().getItem(1).setVisible(false);//Logout Option Menu

        }else{ // 로그 아웃 한 상태 : 로그인 보이게, 로그아웃은 안보이게
            menu.getItem(0).getSubMenu().getItem(0).setVisible(false);
            menu.getItem(0).getSubMenu().getItem(1).setVisible(true);
        }
        return true;
    }

    class OrderToPHP extends AsyncTask<String, Void, String> {
        //ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDig.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDig.show();
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDig.dismiss(); //progress Dialog 종료

            if(result.equals("1")){
               // Toast.makeText(getApplicationContext(), "주문완료"+result, Toast.LENGTH_SHORT).show();
                ResetRecylerView();
                Intent order_intent = new Intent(getApplicationContext(), Activity_Bill_Main.class);
                startActivity(order_intent);

            }else{
                ResetRecylerView();
                Intent menu_intent = new Intent(getApplicationContext(), Activity_Restaurant_Main.class);
                startActivity(menu_intent);
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected String doInBackground( String... params) {


            String serverURL = (String)params[0];
            String json = (String)params[1];
            postParameters = "data="+json;
            try {



                URL url = new URL(serverURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                con.setReadTimeout(2000);
                con.setConnectTimeout(2000);
                con.setRequestMethod("POST");
//                setDoInput(boolean) : Server 통신에서 입력 가능한 상태로 만듬
//                setDoOutput(boolean) : Server 통신에서 출력 가능한 상태로 만듬
                con.setDoOutput(true);
                con.setDoInput(true);
                con.connect();

                //send JSON
//                OutputStreamWriter wr=new OutputStreamWriter(con.getOutputStream());
//                wr.write(ordersJSON);//onPreExecute 메소드의 data 변수의 파라미터 내용을 POST 전송명령
//                wr.flush();//OutputStreamWriter 버퍼 메모리 비우기


                OutputStream outputStream = con.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                int responseStatusCode = con.getResponseCode();
                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = con.getInputStream();
                }
                else{
                    inputStream = con.getErrorStream();
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
