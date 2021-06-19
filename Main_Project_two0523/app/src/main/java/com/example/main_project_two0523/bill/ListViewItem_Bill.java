package com.example.main_project_two0523.bill;

import android.graphics.drawable.Drawable;


public class ListViewItem_Bill {
    public  Drawable iconDrawable ;
    private String menu_IMG ;

    private String user_ID;
    private String menu_ID ;
    private String menu_name ;
    private String menu_price;
    private String menu_num;

    public void set_IMG(String menu_IMG) {this.menu_IMG = menu_IMG.trim() ;  }
    public void set_user_ID(String user_ID){ this.user_ID = user_ID; }
    public void set_menu_ID(String menu_ID) {
        this.menu_ID = menu_ID.trim() ;
    }
    public void set_menu_name(String menu_name) {
        this.menu_name = menu_name.trim() ;
    }
    public void set_menu_price(String menu_price) {
        this.menu_price = menu_price.trim() ;
    }
    public void set_menu_number(String menu_num) { this.menu_num = menu_num;}

    public String get_IMG() {
        return this.menu_IMG;
    }
    public String get_user_ID(){ return this.user_ID;}
    public String get_menu_ID() {
        return this.menu_ID ;
    }
    public String get_menu_name() {
        return this.menu_name ;
    }
    public String get_menu_price() {
        return this.menu_price ;
    }
    public String get_menu_number() { return this.menu_num;}
}