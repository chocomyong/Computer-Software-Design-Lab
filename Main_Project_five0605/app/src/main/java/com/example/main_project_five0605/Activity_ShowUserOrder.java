package com.example.main_project_five0605;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.main_project_five0605.settingmenu.ListViewItem_SettingMenu;
import com.example.main_project_five0605.showuserorder.ListViewAdapter_ShowUserOrder;
import com.example.main_project_five0605.showuserorder.ListViewItem_ShowUserOrder;
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

public class Activity_ShowUserOrder extends AppCompatActivity {
    private String Server_URL = ServerURL.getServerurl();
    private Toast mToast;
    Toolbar toolbar;
    LinearLayoutManager linearLayoutManager;
    TextView title;
    RecyclerView list;
    Context context;
    Logout_Process logout;
    MoneyFormat mF;
    String Qr_Scan;
//    Order_ProgressDIalog progressDig;

    String orderJSON="";
    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID      = "order_ID";
    private static final String TAG_NAME    = "menu_name";
    private static final String TAG_price   = "menu_price";
    private static final String TAG_NUM     = "menu_num";
    private BackPressCloseHandler backPressCloseHandler;

    String beforeJSON, afterJSON;
    String user_ID;
    String table_number="";
    JSONArray peoples = null;

    private IntentIntegrator qrScan;
    ScheduledThreadPoolExecutor exec;

    Button table;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_order);
        context = Activity_ShowUserOrder.this;
        linearLayoutManager = new LinearLayoutManager(this);


        mF = new MoneyFormat();

        title = (TextView) findViewById(R.id.title);
        list = (RecyclerView) findViewById(R.id.listView);
        mToast = Toast.makeText( context, "null",Toast.LENGTH_SHORT);
        table = (Button)findViewById(R.id.table);
        backPressCloseHandler = new BackPressCloseHandler(this);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        finalPrice = (TextView)findViewById(R.id.totalPrice);
        // Logout ??? ?????? ?????? ?????????
        logout = new Logout_Process(context);

//        Intent user_intent = getIntent();
//        user_ID = user_intent.getStringExtra("user_ID");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);// back ??????
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        title.setText(SAVE_CLIENT.getUser_ID(context)+"??? ?????? ?????????");
        title.setTextSize(15);
        MyTableNumber mtn = new MyTableNumber();
        mtn.execute(Server_URL+"mytable.php", SAVE_CLIENT.getUser_ID(context) );


        //title.setText(user_ID+"??? ");


        // ??? ????????? ?????? Table??? setting?????? ?????? activity ??? ??????.
        table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setting_table = new Intent(context, Activity_SettingTable.class);
                startActivity(setting_table);


            }
        });


        // ?????? ?????? ????????? ?????? ?????????,  drawer
//        ((TextView) findViewById(R.id.nav)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "??????", Toast.LENGTH_LONG).show();
//            }
//        });


        ShowUserOrder task = new ShowUserOrder();
        task.execute(Server_URL+"show_user_order.php", SAVE_CLIENT.getUser_ID(context), SaveSharedPreference_Restaurants.getRestaurantsID(context));

        // ????????? ????????? recyclerView??? ?????????.
        Runnable r = new Runnable() {
            public void run() {
                ShowUserOrderAfter task = new ShowUserOrderAfter();
                task.execute(Server_URL+"show_user_order.php", SAVE_CLIENT.getUser_ID(context), SaveSharedPreference_Restaurants.getRestaurantsID(context)   );

            }
        };
        exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleWithFixedDelay(r , 0, 1000, TimeUnit.MILLISECONDS);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    @Override
    protected void onDestroy() {
        if(!exec.isTerminated()){
            exec.shutdown();
        }
        super.onDestroy();
    }


    protected void show_UserOrder() {
        try {
            int idx=0;
            JSONObject jsonObj = new JSONObject(orderJSON);//?????? JSON
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            ListViewAdapter_ShowUserOrder adapter_menu = new ListViewAdapter_ShowUserOrder();

            linearLayoutManager.setItemPrefetchEnabled(true);

            idx = peoples.length();
//            if(idx==0){// ?????? ????????? ????????? ??????.
//                Intent inMain = new Intent(context, Activity_MyRestaurant_Main.class);
//                inMain.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(inMain);
//
//            }
            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);
                ListViewItem_ShowUserOrder li = new ListViewItem_ShowUserOrder();
                li.set_order_ID(c.getString("order_ID"));
                li.set_menu_name(c.getString("menu_name"));
                li.set_menu_price(c.getString("menu_price"));
                li.set_menu_num(c.getString("menu_num"));
                li.set_ok(c.getString("ok"));
                adapter_menu.addItem(li);
            }


            list.setItemViewCacheSize(adapter_menu.getItemCount());
            list.setLayoutManager(linearLayoutManager);
            list.setAdapter(adapter_menu);

            // adapter??? onClick function??? ????????? override
            adapter_menu.setOnClickListener(new ListViewAdapter_ShowUserOrder.OnItemClickListener() {
                ListViewItem_ShowUserOrder data;
                @Override
                public void onItemClick(View v, int position) {

                }

                @Override
                public void onItemNumUpClick(View v, int position) {
                    data = adapter_menu.getItem(position);
                    int temp=0;
                    temp = Integer.parseInt(data.get_menu_num())+1;
                    UpDownMenuItem umi = new UpDownMenuItem();
                    umi.execute(Server_URL+"updown_menu_item.php", data.get_order_ID() , String.valueOf(temp));
                }

                @Override
                public void onItemNumDownClick(View v, int position) {
                    data = adapter_menu.getItem(position);
                    int temp=0;
                    if (data.get_menu_num().equals("1")){
                        mToast.setText("1??? ???????????? ?????? ??? ????????????."  );
                        mToast.show();
                    }else{
                        temp = Integer.parseInt(data.get_menu_num())-1;
                        UpDownMenuItem dmi = new UpDownMenuItem();
                        dmi.execute(Server_URL+"updown_menu_item.php", data.get_order_ID() , String.valueOf(temp));
                    }
                }

                @Override
                public void destroyItemClick(View v, int position) {
                    data = adapter_menu.getItem(position);
                    mToast.setText(data.get_menu_name()+" ?????? ??????");
                    mToast.show();
                    DestroySelectedMenu task = new DestroySelectedMenu();
                    task.execute(Server_URL+"destroy_selected_menu.php", data.get_order_ID());
                }
            });

            adapter_menu.setOnCheckedChangedListener(new ListViewAdapter_ShowUserOrder.OnItemCheckedListener() {
                @Override
                public void onCheckedChanged(int position, boolean isChecked) {
                    // ????????? ?????? ????????? ???????????? ????????? ??????.
                    // ????????? ?????? ???????????? ????????? ????????? ????????? ?????? ?????????.
                    ListViewItem_ShowUserOrder data = adapter_menu.getItem(position);
                    String ok =isChecked?"1":"0";
                    String order_ID=data.get_order_ID();

                    // ??? ?????????????????? ????????? ?????? ?????? postParameters??? ???????????? ?????????.
                    // ?????? ??? ???????????? code??? Compact?????? ????????? ??? ??????.

                    String postParameters = "order_ID="+order_ID+"&ok="+ok;
                    //ok ( ?????? ????????? ????????? ??????????????? ?????? Attribute(Column) )
                    SettingCurMenu_OK setok = new SettingCurMenu_OK();
                    setok.execute("http://115.22.203.91/setting_cur_menu_ok.php", postParameters);

                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


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
                startActivity(logout.Logout_Process());  // logout??? ?????? Intent??? return

                return true;
            case R.id.qr_scan:
//                Toast.makeText(getApplicationContext(), "QR SCAN",
//                        Toast.LENGTH_SHORT).show();
                qrScan = new IntentIntegrator(Activity_ShowUserOrder.this);
                qrScan.setOrientationLocked(false);
                qrScan.setPrompt("??????????????? ?????????!!!");
                qrScan.initiateScan();


                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //QR SCAN ?????? ??? ????????????
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
            Intent goMyMain = new Intent(context, Activity_MyRestaurant_Main.class); // ???????????? context target??? ?????? ???????????? Login Activity ??????.
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
    class ShowUserOrder extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            orderJSON = result;
            beforeJSON = result.trim();

            show_UserOrder();
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

    class ShowUserOrderAfter extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            orderJSON = result;
            afterJSON = result.trim();

            if(!beforeJSON.equals(afterJSON)  ) {
                show_UserOrder();
                beforeJSON = afterJSON;


            }
        }


        @Override
        protected String doInBackground(String... params) {
            String user_ID = (String) params[1];
            String res_ID  = (String) params[2];

            String serverURL = (String) params[0];//url
            String postParameters = "user_ID=" + user_ID+"&res_ID="+res_ID;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(500);
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

    class DestroySelectedMenu extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(!result.equals("1"))
            {
                mToast.setText("???????????? ?????? ");
                mToast.show();

            }
        }


        @Override
        protected String doInBackground(String... params) {


            String order_ID = (String) params[1];
            String serverURL = (String) params[0];//url
            String postParameters = "order_ID=" + order_ID;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(500);
                con.setConnectTimeout(500);
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


    class UpDownMenuItem extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(!result.equals("1"))
            {
                mToast.setText("?????? ?????? ?????? :"+ result);
                mToast.show();

            }
        }


        @Override
        protected String doInBackground(String... params) {


            String order_ID = (String) params[1];
            String menu_num = (String) params[2];
            String serverURL = (String) params[0];//url
            String postParameters = "order_ID=" + order_ID+"&menu_num="+menu_num;
            try {
                URL url = new URL(serverURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(500);
                con.setConnectTimeout(500);
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


    class MyTableNumber extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
                title.setText(SAVE_CLIENT.getUser_ID(context)+"??? ?????? ?????????( "+(result.equals("-1")?"-": result)+" )");

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



    class SettingCurMenu_OK extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("-1")){ // ??????????????? ???????????? ?????? ?????? - ???????????? ??????
                mToast.setText(result);  // ERROR ??????, ??????????????? ?????? NFOI
                mToast.show();
            }
         
        }
        @Override
        protected String doInBackground(String... params) {
            String postParameters = (String) params[1];
            String serverURL = (String) params[0];
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