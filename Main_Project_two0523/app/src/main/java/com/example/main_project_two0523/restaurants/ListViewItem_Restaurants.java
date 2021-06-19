package com.example.main_project_two0523.restaurants;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class ListViewItem_Restaurants {

    Bitmap bmImg;
    private String res_IMG ;

    private String res_ID ;
    private String res_name ;
    private float res_rating;

    public void setIcon(Bitmap bmImg) {
        this.bmImg = bmImg ;
    }
    public void set_IMG(String res_IMG) {
        this.res_IMG = res_IMG ;
    }
    public void set_res_ID(String res_ID) {
        this.res_ID = res_ID ;
    }
    public void set_res_name(String res_name) {
        this.res_name = res_name ;
    }
    public void set_res_rating(float res_rating) {
        this.res_rating = res_rating ;
    }


    public Bitmap getIcon() {  return this.bmImg ;  }
    public String get_IMG() {
        return res_IMG;
    }
    public String get_res_ID() {
        return this.res_ID ;
    }
    public String get_res_name() {
        return this.res_name ;
    }
    public float get_res_rating(){ return this.res_rating; }
}

