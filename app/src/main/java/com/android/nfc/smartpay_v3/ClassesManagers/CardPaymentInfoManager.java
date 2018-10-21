package com.android.nfc.smartpay_v3.ClassesManagers;

import android.content.Context;


import com.android.nfc.smartpay_v3.Classes.PaymentInfo;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Fifty on 5/8/2018.
 */

public class CardPaymentInfoManager {
    private static CardPaymentInfoManager cardPaymentInfoManager;
    private ArrayList<PaymentInfo> paymentInfosList ;
    public CardPaymentInfoManager(Context context){
        cardPaymentInfoManager = this;
        ArrayList arrayList = new ArrayList();
        this.paymentInfosList = arrayList ;
        for(int i=0 ; i<5 ;i++){
            Date today = new Date();
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setCompanyName("Riyadh Cafe");
            paymentInfo.setBillAmount("250");
            paymentInfo.setCompanyType(1);
            paymentInfo.setDate(today);
            paymentInfosList.add(paymentInfo);
        }
    }
    public ArrayList<PaymentInfo> getPaymentInfosList(){
        return paymentInfosList;
    }

    public static CardPaymentInfoManager getPaymentInfoManager(Context context){
        if(cardPaymentInfoManager ==null){
            cardPaymentInfoManager = new CardPaymentInfoManager(context);
        }

        return cardPaymentInfoManager;
    }

}
