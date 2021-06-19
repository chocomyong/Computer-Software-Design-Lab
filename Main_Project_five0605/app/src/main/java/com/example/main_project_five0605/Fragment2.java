package com.example.main_project_five0605;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.example.main_project_five0605.showincome.ListViewAdapter_Income;
import com.example.main_project_five0605.showincome.ListViewItem_Income;

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

public class Fragment2 extends Fragment {
    String orderJSON;
    String beforeJSON3, afterJSON3;
    Context context;
    RecyclerView mRecyclerView;
    private String Server_URL = ServerURL.getServerurl();

    LinearLayoutManager linearLayoutManager;
    static final String TAG_RESULTS = "result";
    static final String TAG_INCOME = "income";
    static final String TAG_ORDER_DAY = "orderDay";
    Timer timer;
    TimerTask TT;
    String income = "", orderDay = "";
    public static Toast mToast;
    JSONArray peoples = null;
    Vibrator vb;
    TextView isEmpty, total_income;
    MoneyFormat mF;
    Order_ProgressDIalog access_dialog;
    Runnable r;
    ScheduledThreadPoolExecutor execF3;
    int idx=0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, container, false);
        context = getContext();
        mToast = Toast.makeText(context, "null", Toast.LENGTH_SHORT);
        isEmpty = (TextView)view.findViewById(R.id.isEmpty);
        total_income = (TextView) view.findViewById(R.id.today_income);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.listView);
        mF = new MoneyFormat();

        linearLayoutManager = new LinearLayoutManager(getContext());

        ShowIncome s2 = new ShowIncome();
        s2.execute(Server_URL + "total_income_staff.php",SaveSharedPreference_Restaurants.getRestaurantsID(context).trim());
        r = new Runnable() {
            public void run() {
                ShowIncomeA s3 = new ShowIncomeA();
                s3.execute(Server_URL + "total_income_staff.php",SaveSharedPreference_Restaurants.getRestaurantsID(context).trim());
            }
        };
        //        scheduleAtFixedRate
        //        scheduleWithFixedDelay
        execF3 = new ScheduledThreadPoolExecutor(1);
        execF3.scheduleWithFixedDelay(r , 0, 1000, TimeUnit.MILLISECONDS);




        return view;
//        return inflater.inflate(R.layout.fragment1,container,false);

    }


//
//    public void showInfo(){
////        access_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
////        access_dialog.show();
//        Handler mHandler = new Handler();
//        mHandler.postDelayed(new Runnable()  {
//            public void run() {
//                ShowIncome  task = new  ShowIncome();
//                task.execute("http://115.22.203.91/total_income_staff.php", SaveSharedPreference_Restaurants.getRestaurantsID(context).trim());
////                access_dialog.dismiss();
//            }
//        }, 300); // 0.5
//
//    }

    protected void showincome() {
        try {

            JSONObject jsonObj = new JSONObject(orderJSON);//전체 JSON
            peoples = jsonObj.getJSONArray(TAG_RESULTS);
            ListViewAdapter_Income adapter_income = new ListViewAdapter_Income();
            idx = peoples.length();
            if(idx > 0 ){
                isEmpty.setVisibility(View.INVISIBLE);
            }else{
                isEmpty.setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < idx; i++) {
                JSONObject c = peoples.getJSONObject(i);
                if(i==0){
                    total_income.setText("오늘의 매출은 "+mF.myFormatter.format(Integer.parseInt( c.getString(TAG_INCOME))) + " 원 입니다" );
                }
                income = c.getString(TAG_INCOME);
                orderDay = c.getString(TAG_ORDER_DAY);
                ListViewItem_Income li = new ListViewItem_Income();
                li.set_income(income);
                li.set_incomeDay(orderDay);
                adapter_income.addItem(li);

            }


            mRecyclerView.setItemViewCacheSize(peoples.length());
            mRecyclerView.setLayoutManager(linearLayoutManager);
            mRecyclerView.setAdapter(adapter_income);





        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
    }
    class ShowIncome extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            orderJSON = result;
            beforeJSON3 = result;

            showincome();
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
    @Override
    public void onPause() {
        super.onPause();

        if(!execF3.isTerminated()){
            execF3.shutdown();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(execF3.isTerminated() ){
//            mToast.setText("실시간 수신 재실행 activity Focusing");
//            mToast.show();
            execF3 = new ScheduledThreadPoolExecutor(1);
            execF3.scheduleWithFixedDelay(r , 0, 1000, TimeUnit.MILLISECONDS);
        }
    }
    class ShowIncomeA extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            orderJSON = result;
            orderJSON = result;

            afterJSON3 = result;

            if(!beforeJSON3.equals(afterJSON3)  ) {
                beforeJSON3 = afterJSON3;
                showincome();
            }
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