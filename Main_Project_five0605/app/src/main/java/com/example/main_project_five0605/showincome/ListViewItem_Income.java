package com.example.main_project_five0605.showincome;


public class ListViewItem_Income {

    private String income ;
    private String incomeDay ;

    public void set_income(String income) {
        this.income = income.trim() ;
    }
    public void set_incomeDay(String incomeDay) {
        this.incomeDay = incomeDay.trim() ;
    }


    public String get_income() {
        return this.income ;
    }
    public String get_incomeDay() {
        return this.incomeDay ;
    }

}