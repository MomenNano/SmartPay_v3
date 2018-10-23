package com.android.nfc.smartpay_v3.ClassesManagers;

import android.content.Context;

import com.android.nfc.smartpay_v3.Classes.PaymentInfo;
import com.android.nfc.smartpay_v3.DBA.LocalDBA;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Fifty on 7/3/2018.
 */

public class PaymentInfoManager {
    private static PaymentInfoManager massagesManeger;
    private ArrayList<PaymentInfo> paymentInfoArrayList = new ArrayList<PaymentInfo>();
    Context context;
    public PaymentInfoManager(Context context){
        this.context = context;
    }
    public ArrayList<PaymentInfo> getPaymentInfoArrayList(){
        LocalDBA localDBA = new LocalDBA(context);
        paymentInfoArrayList = localDBA.getAllPaymentTransaction();
        return paymentInfoArrayList;
    }

    public static PaymentInfoManager getMassagesManeger(Context context){


        if(massagesManeger == null){
            massagesManeger = new PaymentInfoManager(context);
        }

        return massagesManeger;
    }
}
