package com.example.main_project_two0523.orders;
public class ListViewItem_Orders {
    private String menu_IMG ;
    private String menu_ID ;
    private String menu_name ;
    private String menu_price;
    private String Number_Of_Menu;

    public void set_IMG(String menu_IMG) {this.menu_IMG = menu_IMG ;  }
    public void set_menu_ID(String menu_ID) {
        this.menu_ID = menu_ID ;
    }
    public void set_menu_name(String menu_name) {
        this.menu_name = menu_name ;
    }
    public void set_menu_price(String menu_price) {
        this.menu_price = menu_price ;
    }
    public void set_menu_number(String Number_Of_Menu) {
        this.Number_Of_Menu = Number_Of_Menu ;
    }


    public String get_IMG() {
        return menu_IMG;
    }
    public String get_menu_ID() {
        return this.menu_ID ;
    }
    public String get_menu_name() {
        return this.menu_name ;
    }
    public String get_menu_price() {
        return this.menu_price ;
    }
    public String get_menu_number(){ return this.Number_Of_Menu ; }
}