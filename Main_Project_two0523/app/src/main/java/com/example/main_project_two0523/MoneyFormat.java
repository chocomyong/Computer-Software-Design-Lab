package com.example.main_project_two0523;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class MoneyFormat {
    public static DecimalFormat myFormatter = null;

    public static NumberFormat moneyFormat = null;
    public MoneyFormat(){
        moneyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());  // 나라별로 format
        myFormatter = new DecimalFormat("###,###");// 한국, 천원 단위로 콤마 포맷
    }
}




