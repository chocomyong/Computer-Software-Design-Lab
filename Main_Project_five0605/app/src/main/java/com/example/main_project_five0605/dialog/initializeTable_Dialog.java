package com.example.main_project_five0605.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.main_project_five0605.R;


public class initializeTable_Dialog extends Dialog {


    Button option_negative, option_positive;


    EditText settable;
    private Context context;
    private initializeTable click_listener;
    RatingBar rb;

    private OnRatingBarChangeListenerstener listener = null;
    public interface OnRatingBarChangeListenerstener {
        void onChanged(RatingBar ratingBar, float rating, boolean fromUser);
    }




    public initializeTable_Dialog(@NonNull Context context, initializeTable click_listener) {
        super(context);
        this.context = context;
        this.click_listener = click_listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initialize_table_dialog);

        option_negative = (Button) findViewById(R.id.option_negative);
        option_positive = (Button) findViewById(R.id.option_positive);
        settable = (EditText)findViewById(R.id.settable);



        option_positive.setOnClickListener(v -> {
            this.click_listener.onPositiveClick();
             dismiss();
        });

        option_negative.setOnClickListener(v-> {
            this.click_listener.onNegativeClick();
            dismiss();
        });




    }

    public String getTableNum(){
        return settable.getText().toString();
    }
}
