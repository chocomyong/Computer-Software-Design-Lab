package com.example.main_project_two0523;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import androidx.annotation.NonNull;

public class Order_ProgressDIalog extends Dialog {
    public Order_ProgressDIalog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.order_dialog);
    }


}
