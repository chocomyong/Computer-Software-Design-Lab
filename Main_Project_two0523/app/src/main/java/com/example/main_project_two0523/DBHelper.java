package com.example.main_project_two0523;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "groupDB", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
            String sql = " create table IF NOT exists  selected_menu( "
                + " _id integer primary key autoincrement , "   ///_id 필수 column 이다
                + "res_ID text,"
                + " menu_ID integer , "
                + " menu_name text , "
                + " menu_price integer , "
                + " number_of_menu integer );";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS selected_menu");
        onCreate(db);
    }
}
