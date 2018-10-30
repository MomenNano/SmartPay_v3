package com.android.nfc.smartpay_v3.ClassesManagers;

import android.content.Context;


import com.android.nfc.smartpay_v3.Classes.PaymentInfo;
import com.android.nfc.smartpay_v3.DBA.LocalDBA;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Fifty on 5/8/2018.
 */

public class CardPaymentInfoManager {
    private static CardPaymentInfoManager cardPaymentInfoManager;
    private ArrayList<PaymentInfo> paymentInfosList ;
    public CardPaymentInfoManager(Context context,int cardId){
        cardPaymentInfoManager = this;
        this.paymentInfosList = LocalDBA.getInstance(context).getCardPaymentTransaction(cardId);

    }
    public ArrayList<PaymentInfo> getPaymentInfosList(){
        return paymentInfosList;
    }

    public static CardPaymentInfoManager getPaymentInfoManager(Context context,int cardId){
        if(cardPaymentInfoManager ==null){
            cardPaymentInfoManager = new CardPaymentInfoManager(context,cardId);
        }

        return cardPaymentInfoManager;
    }

}
