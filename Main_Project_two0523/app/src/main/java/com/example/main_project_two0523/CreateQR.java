package com.example.main_project_two0523;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class CreateQR extends AppCompatActivity {

    private ImageView qrcode;
    Context context;
    String user_ID="";
    int total_price=0;
    public static Toast mToast ;
    MoneyFormat mF;
    Button back;
    Timer timer;
    TimerTask TT;
    String orderJSON = "";
    String beforeJSON ="";
    String afterJSON = "";
    TextView totalPrice;
    Vibrator vb;
    int index=0;
    ScheduledThreadPoolExecutor exec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_main);
        context = CreateQR.this;
        index=0;
        mToast = Toast.makeText(context, "null",Toast.LENGTH_SHORT);
        qrcode = (ImageView)findViewById(R.id.qrcode);
        totalPrice = (TextView)findViewById(R.id.totalPrice);
        back = (Button)findViewById(R.id.back);
        mF = new MoneyFormat();// Money Format에 대한 객체 생성 및, 초기화
        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);



        Intent menu_intent = getIntent();
        user_ID = menu_intent.getStringExtra("user_ID");
        total_price = menu_intent.getIntExtra("total_price", 0);

        totalPrice.setText(mF.myFormatter.format(total_price) +" 원" ); ;

        // QR result JSON PASING : { "user_ID" : "태호", "rating" : "5", "res_ID": "2" }

        String result = "{" +
                "\"user_ID\":\""+SaveSharedPreference.getUserID(context)+"\", " +
                "\"rating\":\""+SaveSharedPreference_Rating.getRating(context)+"\", " +
                "\"res_ID\":\""+SaveSharedPreference_Restaurants.getRestaurantsID(context)+"\"}";
//
//        mToast.setText(result);
//        mToast.show();

        try {
            result = new String(result.getBytes("UTF-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(result, BarcodeFormat.QR_CODE, 300, 300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        completePay task = new completePay();
        task.execute("http://115.22.203.91/show_bill.php", SaveSharedPreference.getUserID(context));

        Runnable r = new Runnable() {
            public void run() {
                completePay2 task2 = new completePay2();
                task2.execute("http://115.22.203.91/show_bill.php", SaveSharedPreference.getUserID(context));
            }
        };

        exec = new ScheduledThreadPoolExecutor(1);
        exec.scheduleWithFixedDelay(r , 0, 1000, TimeUnit.MILLISECONDS);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SaveSharedPreference_Rating.clearRating(context);
                goback();
            }
        });

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
    public void onBackPressed() {
        //super.onBackPressed();
        goback();
    }
    public void goback(){

        Intent in = new Intent(context, Activity_Bill_Main.class);
        in.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(in);
    }
    class completePay extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            orderJSON = result;
            beforeJSON = result;
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
    class completePay2 extends AsyncTask<String, Void, String> {
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
            // JSON parsing 전의 string의 {}과 같은 특수문자를 거르지 못하여 equal이 잘 안된다.

            // 결제 완료시, STAFF APP에서 QR SCAN을 하게 되면 Databse의 rating TABLE의 insertion operation이 수행 되며
            // 해당 계정의 bill이 초기화된다. 즉 , CreateQR class는 bill TABLE이 초기화 되는 시점
            // 즉 JSON parsing 시에 {"result":[]} 이 출력될 때 == 해당 계정의 bill이 없을 떄 이다.
            // beforeJSON은 결제 전 까지의 JSON, afterJSON 결제 후  {"result":[]} 이다.
            if( beforeJSON.compareTo(afterJSON)!=0 && result.trim().equals("{\"result\":[]}")){
//            if(result.trim().equals("{\"result\":[]}")){
                    vb.vibrate(500);
                    mToast.setText("결제완료 : "+totalPrice.getText().toString()   );
                    mToast.show();
                    Intent in = new Intent(context, Activity_Menu_Main.class);
//                in.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    in.addFlags(  FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(in);

            }
        }


//        public void aaaa(ArrayList<Float> a){
//            int b =a.size();
//            int z = Double.parseDouble()
//        }
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
}