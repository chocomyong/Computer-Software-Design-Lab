package com.example.main_project_five0605;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main_project_five0605.settingmenu.ListViewAdapter_SettingMenu;
import com.example.main_project_five0605.settingmenu.ListViewItem_SettingMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Fragment3 extends Fragment {
    String orderJSON ;
    String beforeJSON, afterJSON;
    Context context;
    RecyclerView mRecyclerView;

    LinearLayoutManager linearLayoutManager;
    static final String TAG_RESULTS = "result";
    static final String TAG_ID = "menu_ID";
    static final String TAG_MENU = "menu_name";
    static final String TAG_MENU_PRICE = "menu_price";
    static final String TAG_CUR = "cur";

    public static Toast mToast ;
    JSONArray peoples = null;
    String menu_ID, menu_name, menu_price, cur;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment3, container, false);
        context = getContext();
        mToast = Toast.makeText(context, "null",Toast.LENGTH_SHORT);
        linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView = (RecyclerView)view.findViewById(R.id.listView);


        // 유연한 ui control을 위하여 200 milliseconds의 delay를 준다.
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable()  {
            public void run() {
                // 고객이 선택가능한 Menu에 대하여 제한을 주는 기능
                ShowCurrentUser task = new ShowCurrentUser();
                task.execute("http://115.22.203.91/show_menu_staff.php", SaveSharedPreference_Restaurants.getRestaurantsID(context).trim());

            }
        }, 200); // 0.5
        return view;
    }
    //현재
    public void showcurMenu() {
        try {
            JSONObject jsonObj = new JSONObject(orderJSON);//전체 JSON
            peoples = jsonObj.getJSONArray(TAG_RESULTS);
            ListViewAdapter_SettingMenu adapter_menu = new ListViewAdapter_SettingMenu();


            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);
                menu_ID = c.getString(TAG_ID);
                menu_name = c.getString(TAG_MENU);
                menu_price = c.getString(TAG_MENU_PRICE);
                cur = c.getString(TAG_CUR);
                ListViewItem_SettingMenu li = new ListViewItem_SettingMenu();
                li.set_menu_ID(menu_ID);
                li.set_menu_name(menu_name);
                li.set_menu_price(menu_price);
                li.set_cur(cur);
                adapter_menu.addItem(li);
            }
            mRecyclerView.setLayoutManager(linearLayoutManager);

            // recyclerView의 성능 개선 setItemViewCache를 사용하면 기존 Thread로 Server에서 IMAGE로 불러왔을때의 좋지 않은 성능을 끌어올릴정도로 매우 좋은 기능
            // 본 프로젝트는 delay가 생기는 Thread로의 UI작업이 아닌 하여 생긴 딜레이를 Glide lib를 사용하여 No delay의 성능을 보인다.
            mRecyclerView.setItemViewCacheSize(peoples.length());  // recyclerView를 Cache를 사용하여 매우 좋은 performance를 보인다.
            mRecyclerView.setAdapter(adapter_menu);


            adapter_menu.setOnCheckedChangedListener(new ListViewAdapter_SettingMenu.OnItemCheckedListener() {
                @Override
                public void onCheckedChanged(int position, boolean isChecked) {
                    String res_ID="";
                    String menu_ID ="";
                    String cur = "";

                    ListViewItem_SettingMenu data = adapter_menu.getItem(position);

                    res_ID = SaveSharedPreference_Restaurants.getRestaurantsID(context).trim();
                    menu_ID = data.get_menu_ID();
                    cur=isChecked?"1":"0";

                    String postParameters = "res_ID="+res_ID+"&menu_ID="+menu_ID+"&cur="+cur;
                    SettingCurMenu setting = new SettingCurMenu();
                    setting.execute("http://115.22.203.91/setting_cur_menu.php", postParameters);



                }
            });

 
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//
//        Handler mHandler = new Handler();
//        mHandler.postDelayed(new Runnable()  {
//            public void run() {
//                ShowCurrentUser task = new ShowCurrentUser();
//                task.execute("http://115.22.203.91/show_menu_staff.php", SaveSharedPreference_Restaurants.getRestaurantsID(context).trim());
//
//            }
//        }, 200);
//
//    }

    class ShowCurrentUser extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            orderJSON = result;
            beforeJSON = result;
            showcurMenu();
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
                    sb.append(json + "\n");
                }
                bufferedReader.close();
                return sb.toString().trim();

            } catch (Exception e) {

                return new String("Error: " + e.getMessage());
            }

        }
    }

    class ShowCurrentUser2 extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            orderJSON = result;
            afterJSON = result;
            if(!beforeJSON.equals(afterJSON)){
                showcurMenu();
                beforeJSON = afterJSON;

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
                    sb.append(json + "\n");
                }
                bufferedReader.close();
                return sb.toString().trim();

            } catch (Exception e) {

                return new String("Error: " + e.getMessage());
            }

        }
    }



////////////////////    String initialJSON, beforeCurJSON, afterCurJSON;
    class SettingCurMenu extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            mToast.setText(result);
//            mToast.show();
        }


        @Override
        protected String doInBackground(String... params) {
            String serverURL = (String) params[0];//url
            try {
                URL url = new URL(serverURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(5000);
                con.setConnectTimeout(5000);
                con.setRequestMethod("POST");
                con.connect();
                OutputStream outputStream = con.getOutputStream();
                outputStream.write(params[1].getBytes("UTF-8"));
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