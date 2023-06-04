package com.example.shoppe.activities.toast;

import android.content.Context;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class Utils {
    public static  void toast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public static  void toastySuccess(Context context, String message){
        Toasty.success(context, message, Toast.LENGTH_SHORT, true).show();//thành công
    }

    public static  void toastyError(Context context, String message){
        Toasty.error(context, message, Toast.LENGTH_SHORT, true).show(); // thông báo lỗi
    }

    public static  long getTimestamp(){
        return System.currentTimeMillis();
    }


}
