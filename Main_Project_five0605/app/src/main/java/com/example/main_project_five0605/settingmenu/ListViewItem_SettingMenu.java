package com.example.main_project_five0605.settingmenu;
public class ListViewItem_SettingMenu {
    private String menu_ID ;
    private String menu_name ;
    private String menu_price;
    private String cur;

    public void set_menu_ID(String menu_ID) {
        this.menu_ID = menu_ID ;
    }
    public void set_menu_name(String menu_name) {
        this.menu_name = menu_name ;
    }
    public void set_menu_price(String menu_price) {
        this.menu_price = menu_price ;
    }
    public void set_cur(String cur){ this.cur = cur;}

    public String get_menu_ID() {
        return this.menu_ID ;
    }
    public String get_menu_name() {
        return this.menu_name ;
    }
    public String get_menu_price() {
        return this.menu_price ;
    }
    public String get_cur(){ return this.cur; }
}