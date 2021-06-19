package com.example.main_project_five0605.current_user;


public class ListViewItem_User {

    private String order_ID ;
    private String user_ID ;

    public void set_order_ID(String order_ID) {
        this.order_ID = order_ID.trim() ;
    }
    public void set_user_ID(String user_ID) {
        this.user_ID = user_ID.trim() ;
    }


    public String get_order_ID() {
        return this.order_ID ;
    }
    public String get_user_ID() {
        return this.user_ID ;
    }

}