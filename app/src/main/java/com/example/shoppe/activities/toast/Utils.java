package com.example.shoppe.activities.toast;

import android.content.Context;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

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

    public static void toastyInfo(Context context, String message){
        Toasty.info(context, message, Toast.LENGTH_SHORT, true).show();
    }
    public static  long getTimestamp(){
        return System.currentTimeMillis();
    }

    public static String formatTimestampDate(Long timestamp){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);

        String date = DateFormat.format("dd/MM/yyyy",calendar).toString();
        return date;
    }


}
