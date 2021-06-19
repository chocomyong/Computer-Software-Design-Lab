package com.example.main_project_five0605;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.example.main_project_five0605.current_user.ListViewAdapter_User;
import com.example.main_project_five0605.current_user.ListViewItem_User;
import com.example.main_project_five0605.showuserorder.ListViewAdapter_ShowUserOrder;

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

public class Fragment1 extends Fragment {
    String orderJSON;
    String beforeJSON, afterJSON;
    Context context;
    RecyclerView mRecyclerView;
    private String Server_URL = ServerURL.getServerurl();
    JSONArray peoples = null;

    LinearLayoutManager linearLayoutManager;
    static final String TAG_RESULTS = "result";
    static final String TAG_ORDER = "order_ID";
    static final String TAG_USER = "user_ID";
    Timer timer;
    TimerTask TT;
    String order_ID = "", user_ID = "";
    public static Toast mToast;
//    Vibrator vb;
    TextView isEmpty;
    Order_ProgressDIalog access_dialog;
    int idx=0;
    Runnable r;
    ScheduledThreadPoolExecutor exec;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);
        context = getContext();
//        vb = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        mToast = Toast.makeText(context, "null", Toast.LENGTH_SHORT);
        isEmpty = (TextView)view.findViewById(R.id.isEmpty);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.listView);
        linearLayoutManager = new LinearLayoutManager(getContext());
        //access_dialog = new Order_ProgressDIalog(context);



        ShowCurrentUser SCU = new ShowCurrentUser();
        SCU.execute(Server_URL + "show_bills.php",SaveSharedPreference_Restaurants.getRestaurantsID(context).trim());
        r = new Runnable() {
            public void run() {
                ShowCurrentUserAfter SCU = new ShowCurrentUserAfter();
                SCU.execute(Server_URL + "show_bills.php",SaveSharedPreference_Restaurants.getRestaurantsID(context).trim());
            }
        };
        //        scheduleAtFixedRate
        //        scheduleWithFixedDelay
        exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleWithFixedDelay(r , 0, 1000, TimeUnit.MILLISECONDS);


        return view;
//        return inflater.inflate(R.layout.fragment1,container,false);

    }


    protected void showcurUser() {
        try {

            JSONObject jsonObj = new JSONObject(orderJSON);//전체 JSON
            peoples = jsonObj.getJSONArray(TAG_RESULTS);
            ListViewAdapter_User adapter_menu = new ListViewAdapter_User();
            idx = peoples.length();
            if(idx > 0 ){
                isEmpty.setVisibility(View.INVISIBLE);
            }else{
                isEmpty.setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < idx; i++) {
                JSONObject c = peoples.getJSONObject(i);
                order_ID = c.getString(TAG_ORDER);
                user_ID = c.getString(TAG_USER);
                ListViewItem_User li = new ListViewItem_User();
                li.set_order_ID(order_ID);
                li.set_user_ID(user_ID);
                adapter_menu.addItem(li);

            }


            mRecyclerView.setItemViewCacheSize(peoples.length());
            mRecyclerView.setLayoutManager(linearLayoutManager);
            mRecyclerView.setAdapter(adapter_menu);




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
    public void onDestroy() {
        super.onDestroy();
    }
    class ShowCurrentUser extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // access_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //access_dialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            orderJSON = result;
            beforeJSON = result;
//            afterJSON = result;
            showcurUser();
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
    class ShowCurrentUserAfter extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //access_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //access_dialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //access_dialog.dismiss();
            orderJSON = result;

            afterJSON = result;

            if(!beforeJSON.equals(afterJSON)  ) {
                beforeJSON = afterJSON;
                showcurUser();
//                vb.vibrate(500);


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
}