package com.example.main_project_two0523.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.main_project_two0523.MoneyFormat;
import com.example.main_project_two0523.R;
import com.example.main_project_two0523.menu.ListViewItem_Menu;

import java.util.ArrayList;

public class Menu_Dialog extends Dialog {

    TextView title;
    Button option_negative, option_positive;
    Button numdown, numup;
    TextView num, menu_price;
    ImageView menu_IMG;


    int first_menu_price; // Dialog 처음 시작 시 입력되는 가격, menu 의 개수에 따라 first_menu_price의 배수만큼 증가.

    private Context context;
    private Menu_Dialog_ClickListener click_listener;
    private MoneyFormat mF = null;
    public Menu_Dialog(@NonNull Context context, Menu_Dialog_ClickListener click_listener) {
        super(context);
        this.context = context;
        this.click_listener = click_listener;
        mF = new MoneyFormat();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_menu_dialog);

        option_negative = (Button) findViewById(R.id.option_negative);
        option_positive = (Button) findViewById(R.id.option_positive);
        title = (TextView) findViewById(R.id.option_title);
        menu_IMG = (ImageView) findViewById(R.id.img_menu_ID);
        menu_price = (TextView) findViewById(R.id.menu_price);

        num = (TextView) findViewById(R.id.num);
        numup = (Button) findViewById(R.id.numup);
        numdown = (Button) findViewById(R.id.numdown);


        option_positive.setOnClickListener(v -> {
            this.click_listener.onPositiveClick();
            dismiss();
        });

        option_negative.setOnClickListener(v -> {
            this.click_listener.onNegativeClick();
            dismiss();
        });


        numup.setOnClickListener(v ->{
            this.click_listener.onNumberUpClick();
        });
        numdown.setOnClickListener(v-> {

            this.click_listener.onNumberDownClick();
        });

    }

    public void setText_Title(String str) {
        title.setText(str);
    }

    public void setIMG(String str) {
        Glide.with(menu_IMG)
                .load(str)
                .into(menu_IMG);
    }

    public void set_menu_price(String str){
        first_menu_price = Integer.parseInt(str);
        menu_price.setText(mF.myFormatter.format(Integer.parseInt(str)));
    }


    public void set_num(int n){
        num.setText(String.valueOf(n)+"");
    }


    public int get_menu_price(){
        return Integer.parseInt(menu_price.getText().toString());
    }


}
