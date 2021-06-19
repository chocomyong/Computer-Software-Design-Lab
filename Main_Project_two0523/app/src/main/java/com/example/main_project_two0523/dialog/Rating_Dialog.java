package com.example.main_project_two0523.dialog;

import android.app.Dialog;
import android.content.Context;
import android.media.Rating;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.main_project_two0523.MoneyFormat;
import com.example.main_project_two0523.R;
import com.example.main_project_two0523.SaveSharedPreference_Rating;
import com.example.main_project_two0523.orders.ListViewAdapter_Orders;

public class Rating_Dialog extends Dialog {


    Button option_negative, option_positive;


    TextView rating;

    RatingBar rb;

    private OnRatingBarChangeListenerstener listener = null;
    public interface OnRatingBarChangeListenerstener {
        void onChanged(RatingBar ratingBar, float rating, boolean fromUser);
    }
    public void OnRatingBarChangeListenerstener(OnRatingBarChangeListenerstener listener){
        this.listener = listener;
    }


    private Context context;
    private Rating_Dialog_ClickListener click_listener;
    private MoneyFormat mF = null;
    public Rating_Dialog(@NonNull Context context, Rating_Dialog_ClickListener click_listener) {
        super(context);
        this.context = context;
        this.click_listener = click_listener;
        mF = new MoneyFormat();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_dialog);

        option_negative = (Button) findViewById(R.id.option_negative);
        option_positive = (Button) findViewById(R.id.option_positive);
        rb = (RatingBar)findViewById(R.id.ratingbar);
        rating = (TextView)findViewById(R.id.rating);



        option_positive.setOnClickListener(v -> {
            this.click_listener.onPositiveClick();
             dismiss();
        });

        option_negative.setOnClickListener(v-> {
            this.click_listener.onNegativeClick();
            dismiss();
        });


        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                listener.onChanged(ratingBar, rating, fromUser);
            }
        });









        //        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                Rating_Dialog.this.click_listener.onRatingChanged();
//                Rating_Dialog.this.dismiss();
//            }
//        });

//        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                rb.setRating(rating);
//
//            }
//        });

    }
}
