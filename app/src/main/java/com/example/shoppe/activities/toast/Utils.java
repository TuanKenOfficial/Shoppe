package com.example.shoppe.activities.toast;

import android.content.Context;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class Utils {

    public static final String AD_STATUS_AVAILABLE="Có sẵn";
    public static final String AD_STATUS_SOLD="Đã bán";

    public static final String[] categories = {
            "Điện thoại",
            "Máy tính/Laptop",
            "Xe cộ",
            "Nội thất & Trang trí nhà cửa",
            "Thời trang & Làm đẹp",
            "Sách",
            "Các môn thể thao",
            "Động vật",
            "Nông nghiệp",
            "Trái cây & Hoa quả",
            "Các doanh nghiệp",
            "Thể loại khác",
    };

    public static final String[] conditions ={"Mới", "Đã sử dụng","Đã tân trang"};
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
