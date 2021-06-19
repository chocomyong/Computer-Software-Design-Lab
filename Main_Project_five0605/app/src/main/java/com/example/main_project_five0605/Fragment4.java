package com.example.main_project_five0605;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main_project_five0605.dialog.initializeTable;
import com.example.main_project_five0605.dialog.initializeTable_Dialog;
import com.example.main_project_five0605.showtable.ListViewAdapter_ShowTable;
import com.example.main_project_five0605.showtable.ListViewItem_ShowTable;

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

public class Fragment4 extends Fragment implements View.OnClickListener {
    String orderJSON;
    String beforeJSON, afterJSON;
    Context context;
    RecyclerView list;
    private String Server_URL = ServerURL.getServerurl();

    LinearLayoutManager linearLayoutManager;
    static final String TAG_RESULTS = "result";
    static final String TAG_ORDER = "order_ID";
    static final String TAG_USER = "user_ID";
    Timer timer;
    TimerTask TT;
    String order_ID = "", user_ID = "";
    public static Toast mToast;
    JSONArray peoples = null;
//    Vibrator vb;
    TextView isEmpty;
    Button settingTable;
    Order_ProgressDIalog access_dialog;
    int idx=0;
    RelativeLayout Rel;
    initializeTable_Dialog initializeTable_dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment4, container, false);
        context = getContext();
//        vb = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
//        Rel = (RelativeLayout)view.findViewById(R.id.R);
//        Rel.setOnClickListener(this);

        mToast = Toast.makeText(context, "null", Toast.LENGTH_SHORT);
        isEmpty = (TextView)view.findViewById(R.id.isEmpty);
        list = (RecyclerView) view.findViewById(R.id.listView);
        settingTable = (Button)view.findViewById(R.id.settingTable);
        settingTable.setOnClickListener(this);
        linearLayoutManager = new LinearLayoutManager(getContext());
        //access_dialog = new Order_ProgressDIalog(context);


        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable()  {
            public void run() {

                ShowTable task = new ShowTable();
                task.execute(Server_URL+ "show_table.php",  SaveSharedPreference_Restaurants.getRestaurantsID(context));
            }
        }, 200); // 0.5




        return view;
//        return inflater.inflate(R.layout.fragment1,container,false);

    }
    public void show_dialog() {
        initializeTable_dialog = new initializeTable_Dialog(context, new initializeTable() {
            @Override
            public void onPositiveClick() {
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable()  {
                    public void run() {
//                        mToast.setText(initializeTable_dialog.getTableNum());
//                        mToast.show();
                        Initialize_Table task = new Initialize_Table();
                        task.execute("http://115.22.203.91/initialtable.php",initializeTable_dialog.getTableNum(),SaveSharedPreference_Restaurants.getRestaurantsID(context).trim()  );

                    }
                }, 200); // 0.5
                initializeTable_dialog.dismiss();
            }
            @Override
            public void onNegativeClick() {
                initializeTable_dialog.dismiss();
            }


        });


        initializeTable_dialog.setCanceledOnTouchOutside(true); // 다이얼로그 바깥 터치시 다이얼로그
        initializeTable_dialog.setCancelable(true);// 다이얼로그 back 버튼으로 취소 가능
        initializeTable_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initializeTable_dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        initializeTable_dialog.show();

    }
    // ViewGroup을 상속받은 Fragment내에서는 setOnclickListener를 사용할 수 없음.
    // 따라서 다시 override시켜야 Fragment 내의 Button event를 사용할 수 있다.
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.settingTable:
                show_dialog();

        }
    }

//
//    @Override
//    public void onPause() {
//        super.onPause();
//
//        if(!exec.isTerminated()){
//            exec.shutdown();
//        }
//
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if(exec.isTerminated() ){
////            mToast.setText("실시간 수신 재실행 activity Focusing");
////            mToast.show();
//            exec = new ScheduledThreadPoolExecutor(1);
//            exec.scheduleWithFixedDelay(r , 0, 1000, TimeUnit.MILLISECONDS);
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    protected void show_table() {
        try {
            int idx=0;
            JSONObject jsonObj = new JSONObject(orderJSON);//전체 JSON
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            ListViewAdapter_ShowTable adapter_menu = new ListViewAdapter_ShowTable();
            list.setLayoutManager(linearLayoutManager);
            linearLayoutManager.setItemPrefetchEnabled(true);


            ListViewItem_ShowTable li;
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
                li = new ListViewItem_ShowTable();
                li.set_ID( c.getString("table_ID") );
                li.set_occ(c.getString("occ"));
                //  a+=c.getString("occ");

                adapter_menu.addItem(li);

            }


            adapter_menu.setHasStableIds(true);
            list.setItemViewCacheSize(adapter_menu.getItemCount()/2);
            list.setAdapter(adapter_menu);






        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class Initialize_Table extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mToast.setText(result);
            mToast.show();

            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable()  {
                public void run() {

                    ShowTable task = new ShowTable();
                    task.execute(Server_URL+ "show_table.php",  SaveSharedPreference_Restaurants.getRestaurantsID(context));
                }
            }, 300);
        }


        @Override
        protected String doInBackground(String... params) {
            String index = (String) params[1];
            String res_ID = (String) params[2];

            String serverURL = (String) params[0];//url
            String postParameters = "index=" + index+"&res_ID="+res_ID;
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

    class ShowTable extends AsyncTask<String, Void, String> {
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
}