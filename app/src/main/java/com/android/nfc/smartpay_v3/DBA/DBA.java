package com.android.nfc.smartpay_v3.DBA;

import com.android.nfc.smartpay_v3.Classes.Companey;
import com.android.nfc.smartpay_v3.Classes.Card;
import com.android.nfc.smartpay_v3.Classes.PaymentInfo;
import com.android.nfc.smartpay_v3.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Fifty on 6/6/2018.
 */

public class DBA {
    public int [] cardsbankImage= {
            R.drawable.fispngsmall,
            R.drawable.omb,
            R.drawable.bok
    };
    public String [] cardBanksName = {
            "Fisal Islamic Bank",
            "Omdurman National Bank",
            "Bank Of Khartoum"
    };
    public Card createNewCardDBA(String CardHolderName, String CardNo, String CardEXDate, String BankName,int cardIcon){
        Card card = new Card();
        card.setBankName(BankName);
        card.setCardHolderName(CardHolderName);
        card.setCardIcon(1);
        card.setCardNo(CardNo);
        card.setCardIcon(cardIcon);
        return card;
    }

    public ArrayList<Companey> getCompaniesList(){
        ArrayList<Companey> companeyArrayList = new ArrayList<Companey>();
        companeyArrayList.add(new Companey("JAZZ Cafe",1,new LatLng(15.567163590802334,32.56096452856064)));
        companeyArrayList.add(new Companey("Hoash Al-Samak",2,new LatLng(15.562671900143107,32.57050897926092)));
        companeyArrayList.add(new Companey("Al-Tasamouh",3,new LatLng(15.541850969834007,32.570546194911)));

        return companeyArrayList;
    }

    public void insertPaymentInfoToDB(PaymentInfo paymentInfo) {

    }

    public ArrayList <PaymentInfo> getPaymentHistoriyList() {
        ArrayList<PaymentInfo> paymentInfosList = new ArrayList<>();
        for(int i=0 ; i<5 ;i++){
            Date today = new Date();
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setCompanyName("Riyadh Cafe");
            paymentInfo.setBillAmount("250");
            paymentInfo.setCompanyType(1);
            paymentInfo.setPaymentDate(today);
            paymentInfosList.add(paymentInfo);
        }
        return paymentInfosList;
    }

    public ArrayList getCardsList() {
        ArrayList<Card> cardsList = new ArrayList<>();

        for(int i=0 ; i<3 ;i++){
            Card card = new Card();
            card.setBankName(cardBanksName[i]);
            card.setCardNo("1234");
            card.setCardIcon(cardsbankImage[i]);
            cardsList.add(card);
        }
        return  cardsList;
    }

    public int login(String username, String password) {
        //send to server and check
        int result = 1;
        return result;
    }

    public void updateData() {
    }


    public int deductMoneyFromBank(double money) {
        //server return 1 if there is enough money in the bank Account and the deducting done successfully
        //return 2 if there is no enough money in bank account
        //return 3 if there is something went wrong
        return 1;
    }
}
