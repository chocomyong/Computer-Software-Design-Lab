package com.example.main_project_five0605.showuserorder;
public class ListViewItem_ShowUserOrder {
    private String order_ID ;
    private String menu_name ;
    private String menu_price;
    private String menu_num;
    private String ok;

    public void set_order_ID(String menu_ID) {
        this.order_ID = menu_ID ;
    }
    public void set_menu_name(String menu_name) {
        this.menu_name = menu_name ;
    }
    public void set_menu_price(String menu_price) {
        this.menu_price = menu_price ;
    }
    public void set_menu_num(String menu_num) { this.menu_num = menu_num ; }
    public void set_ok(String ok){ this.ok = ok;}

    public String get_order_ID() {
        return this.order_ID ;
    }
    public String get_menu_name() {
        return this.menu_name ;
    }
    public String get_menu_price() {
        return this.menu_price ;
    }
    public String get_menu_num() {  return this.menu_num; }
    public String get_ok(){ return this.ok; }

}


