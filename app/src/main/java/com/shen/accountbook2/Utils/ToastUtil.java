
package com.shen.accountbook2.Utils;



import android.content.Context;
import android.widget.Toast;

import com.shen.accountbook2.global.AccountBookApplication;


public class ToastUtil {

    private static Context mContext;

    private ToastUtil(Context context) {
        mContext = context;
    }


    public static void show(String message){
        Toast.makeText(AccountBookApplication.getContext(),message, Toast.LENGTH_SHORT).show();
    }


}
