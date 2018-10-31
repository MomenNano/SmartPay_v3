package com.android.nfc.smartpay_v3.DBA;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;


import com.android.nfc.smartpay_v3.Classes.Card;
import com.android.nfc.smartpay_v3.Classes.PaymentInfo;

import java.util.ArrayList;

/**
 * Created by Fifty on 6/20/2018.
 */

public class LocalDBA extends SQLiteOpenHelper  {

    private static final  String DATABASE_NAME = "smartpay.db";
    private static final  String CARDS_TABLE_NAME = "Cards";
    private static final  String CARDS_COLUMN_CARD_ID = "c_id";
    private static final  String CARDS_COLUMN_CARD_NO = "c_no";
    private static final  String CARDS_COLUMN_BANK_NAME = "c_bank";
    private static final  String CARDS_COLUMN_HOLDER_NAME = "c_holder_name";
    private static final  String CARDS_COLUMN_BALANCE = "c_balance";
    private static final  String CARDS_COLUMN_PASSWORD = "c_password";
    private static final  String CARDS_COLUMN_ICON = "c_icon";
    private static final  String CARDS_COLUMN_FLAG = "c_flag";
    private static final  String PAYMENT_TRANSACTION_TABLE_NAME = "PaymentsTransaction";
    private static final  String PAYMENT_TRANSACTION_ID = "p_id";
    private static final  String PAYMENT_TRANSACTION_BILL_AMOUNT = "p_bill_Amount";
    private static final  String PAYMENT_TRANSACTION_DATE = "p_payment_date";
    private static final  String PAYMENT_TRANSACTION_TIME = "p_time";
    private static final  String PAYMENT_TRANSACTION_COMPANEY_ID = "P_company_id";
    private static final  String PAYMENT_TRANSACTION_COMPANEY_TYPE = "p_company_type";
    private static final  String PAYMENT_TRANSACTION_Companey_name = "p_company_name";
    private static final  String PAYMENT_CARDS_TABLE_NAME = "paymentCards";
    private static final  String PAYMENT_CARDS_ID = "pc_id";
    private static final  String PAYMENT_CARDS_BILL_AMOUNT = "pc_bill_Amount";
    private static LocalDBA mInstance;

    public static synchronized LocalDBA getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LocalDBA(context);
        }
        return mInstance;
    }
    public LocalDBA(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table Cards"
                +"(c_id Integer primary key AUTOINCREMENT , c_no text , c_bank text , c_holder_name text , c_balance real , c_password text, c_icon integer , c_flag integer)");
        db.execSQL("create Table PaymentsTransaction"
                +"(p_id integer primary key AUTOINCREMENT ,p_unique_id , p_bill_Amount real , p_payment_date text , p_time text ,p_company_bank_account integer,p_company_id integer," +
                " p_company_type integer, p_company_name text,p_purchaser_id text,p_purchaser_name text,p_purchaser_card_id text,p_flag integer,p_send integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop Table If Exists Cards");
        onCreate(db);

    }

    public boolean insertPaymentTransaction(PaymentInfo paymentInfo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Configuration.TABLE_PAYMENT_TRANSACTION_BILL_AMOUNT,paymentInfo.getBillAmount());
        contentValues.put(Configuration.TABLE_PAYMENT_TRANSACTION_UNIQUE_ID,paymentInfo.getUniqueId());
        contentValues.put(Configuration.TABLE_PAYMENT_TRANSACTION_COMPANY_ID,paymentInfo.getCompanyId());
        contentValues.put(Configuration.TABLE_PAYMENT_TRANSACTION_COMPANY_BANK_ACCUONT,paymentInfo.getCompanyBankAccount());
        contentValues.put(Configuration.TABLE_PAYMENT_TRANSACTION_COMPANY_NAME,paymentInfo.getCompanyName());
        contentValues.put(Configuration.TABLE_PAYMENT_TRANSACTION_COMPANY_TYPE,paymentInfo.getCompanyType());
        contentValues.put(Configuration.TABLE_PAYMENT_TRANSACTION_DATE,paymentInfo.getStringDate());
        contentValues.put(Configuration.TABLE_PAYMENT_TRANSACTION_TIME,paymentInfo.getStringTime());
        contentValues.put(Configuration.TABLE_PAYMENT_TRANSACTION_PURCHASER_ID,paymentInfo.getPurchaserId());
        contentValues.put(Configuration.TABLE_PAYMENT_TRANSACTION_PURCHASER_NAME,paymentInfo.getPurchaserName());
        contentValues.put(Configuration.TABLE_PAYMENT_TRANSACTION_PURCHASER_CARD_ID,paymentInfo.getCardId());
        contentValues.put(Configuration.TABLE_PAYMENT_TRANSACTION_FLAG,0);
        contentValues.put(Configuration.TABLE_PAYMENT_TRANSACTION_SEND_FLAG,0);
        db.insert(Configuration.TABLE_PAYMENT_TRANSACTION_TABLE_NAME,null,contentValues);
        return true;
    }
    public boolean updatePaymentTransactionFlag(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Configuration.TABLE_PAYMENT_TRANSACTION_FLAG,1);
        db.update(Configuration.TABLE_PAYMENT_TRANSACTION_TABLE_NAME,contentValues
                ,Configuration.TABLE_PAYMENT_TRANSACTION_ID+"= ?"
                ,new String[]{Integer.toString(id)});
        return true;
    }
    public boolean updatePaymentTransactionSendFlag(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Configuration.TABLE_PAYMENT_TRANSACTION_SEND_FLAG,1);
        db.update(Configuration.TABLE_PAYMENT_TRANSACTION_TABLE_NAME,contentValues
                ,Configuration.TABLE_PAYMENT_TRANSACTION_ID+"= ?"
                ,new String[]{Integer.toString(id)});
        return true;
    }
    public boolean updatePaymentTransactionUniqueID(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Configuration.TABLE_PAYMENT_TRANSACTION_UNIQUE_ID,Build.SERIAL+id);
        db.update(Configuration.TABLE_PAYMENT_TRANSACTION_TABLE_NAME,contentValues
                ,Configuration.TABLE_PAYMENT_TRANSACTION_ID+"= ?"
                ,new String[]{Integer.toString(id)});
        return true;
    }


    public ArrayList<PaymentInfo> getAllPaymentTransaction(){
        ArrayList<PaymentInfo> paymentInfoArrayList = new ArrayList<PaymentInfo>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("Select * from PaymentsTransaction",null);
        result.moveToFirst();
        while (result.isAfterLast() == false){
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setTransactionId(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_ID)));
            paymentInfo.setUniqueId(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_UNIQUE_ID)));
            paymentInfo.setBillAmount(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_BILL_AMOUNT)));
            paymentInfo.setCompanyId(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_COMPANY_ID)));
            paymentInfo.setCompanyName(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_COMPANY_NAME)));
            paymentInfo.setCompanyType(result.getInt(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_COMPANY_TYPE)));
            paymentInfo.setStringDate(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_DATE)));
            paymentInfo.setStringTime(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_TIME)));
            paymentInfo.setPurchaserName(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_PURCHASER_NAME)));
            paymentInfo.setCardId(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_PURCHASER_CARD_ID)));
            paymentInfo.setFlag(Integer.valueOf(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_FLAG))));
            paymentInfoArrayList.add(paymentInfo);
            result.moveToNext();
        }
        return paymentInfoArrayList;
    }
    public ArrayList<PaymentInfo> getCardPaymentTransaction(int cardid){
        ArrayList<PaymentInfo> paymentInfoArrayList = new ArrayList<PaymentInfo>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("Select * from PaymentsTransaction Where p_purchaser_card_id="+cardid,null);
        result.moveToFirst();
        while (result.isAfterLast() == false){
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setTransactionId(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_ID)));
            paymentInfo.setUniqueId(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_UNIQUE_ID)));
            paymentInfo.setBillAmount(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_BILL_AMOUNT)));
            paymentInfo.setCompanyId(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_COMPANY_ID)));
            paymentInfo.setCompanyName(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_COMPANY_NAME)));
            paymentInfo.setCompanyType(result.getInt(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_COMPANY_TYPE)));
            paymentInfo.setStringDate(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_DATE)));
            paymentInfo.setStringTime(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_TIME)));
            paymentInfo.setPurchaserName(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_PURCHASER_NAME)));
            paymentInfo.setCardId(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_PURCHASER_CARD_ID)));
            paymentInfo.setFlag(Integer.valueOf(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_FLAG))));
            paymentInfoArrayList.add(paymentInfo);
            result.moveToNext();
        }
        return paymentInfoArrayList;
    }


    public ArrayList<PaymentInfo> getUncommittedPaymentTransaction(){
        ArrayList<PaymentInfo> paymentInfoArrayList = new ArrayList<PaymentInfo>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("Select * from PaymentsTransaction where p_flag=0 and p_send=0",null);
        result.moveToFirst();
        while (result.isAfterLast() == false){
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setTransactionId(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_ID)));
            paymentInfo.setUniqueId(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_UNIQUE_ID)));
            paymentInfo.setBillAmount(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_BILL_AMOUNT)));
            paymentInfo.setCompanyId(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_COMPANY_ID)));
            paymentInfo.setCompanyName(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_COMPANY_NAME)));
            paymentInfo.setCompanyType(result.getInt(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_COMPANY_TYPE)));
            paymentInfo.setStringDate(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_DATE)));
            paymentInfo.setStringTime(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_TIME)));
            paymentInfo.setPurchaserName(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_PURCHASER_NAME)));
            paymentInfo.setCardId(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_PURCHASER_CARD_ID)));
            paymentInfo.setPurchaserId(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_PURCHASER_ID)));
            paymentInfo.setCompanyBankAccount(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_COMPANY_BANK_ACCUONT)));
            paymentInfoArrayList.add(paymentInfo);
            result.moveToNext();
        }
        return paymentInfoArrayList;
    }

    public ArrayList<PaymentInfo> getNonResponsePaymentTransaction(){
        ArrayList<PaymentInfo> paymentInfoArrayList = new ArrayList<PaymentInfo>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("Select * from PaymentsTransaction where p_flag=0 and p_send=1",null);
        result.moveToFirst();
        while (result.isAfterLast() == false){
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setTransactionId(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_ID)));
            paymentInfo.setUniqueId(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_UNIQUE_ID)));
            paymentInfo.setBillAmount(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_BILL_AMOUNT)));
            paymentInfo.setCompanyId(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_COMPANY_ID)));
            paymentInfo.setCompanyName(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_COMPANY_NAME)));
            paymentInfo.setCompanyType(result.getInt(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_COMPANY_TYPE)));
            paymentInfo.setStringDate(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_DATE)));
            paymentInfo.setStringTime(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_TIME)));
            paymentInfo.setPurchaserName(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_PURCHASER_NAME)));
            paymentInfo.setCardId(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_PURCHASER_CARD_ID)));
            paymentInfo.setPurchaserId(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_PURCHASER_ID)));
            paymentInfo.setCompanyBankAccount(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_COMPANY_BANK_ACCUONT)));
            paymentInfo.setSendFlag(Integer.valueOf(result.getString(result.getColumnIndex(Configuration.TABLE_PAYMENT_TRANSACTION_SEND_FLAG))));

            paymentInfoArrayList.add(paymentInfo);
            result.moveToNext();
        }
        return paymentInfoArrayList;
    }







    public boolean insertCard(Card card){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("c_no",card.getCardNo());
        contentValues.put("c_bank",card.getBankName());
        contentValues.put("c_holder_name",card.getCardHolderName());
        contentValues.put("c_balance",card.getCardBalance());
        contentValues.put("c_icon",card.getCardIcon());
        contentValues.put("c_flag",card.getCardFlag());
        db.insert("Cards",null,contentValues);
        return true;
    }
    public ArrayList<Card> getAllCards(){
        ArrayList<Card> cardArrayList = new ArrayList<Card>();
        //hp = new hashmap
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("Select * from Cards",null);
        result.moveToFirst();
        while (result.isAfterLast() == false){
            Card card = new Card();
            card.setCardId(Integer.valueOf(result.getString(result.getColumnIndex(CARDS_COLUMN_CARD_ID))));
            card.setCardNo(result.getString(result.getColumnIndex(CARDS_COLUMN_CARD_NO)));
            card.setBankName(result.getString(result.getColumnIndex(CARDS_COLUMN_BANK_NAME)));
            card.setCardBalance(result.getDouble(result.getColumnIndex(CARDS_COLUMN_BALANCE)));
            card.setCardHolderName(result.getString(result.getColumnIndex(CARDS_COLUMN_HOLDER_NAME)));
            card.setCardIcon(result.getInt(result.getColumnIndex(CARDS_COLUMN_ICON)));
            card.setCardFlag(result.getInt(result.getColumnIndex(CARDS_COLUMN_FLAG)));
            card.setPassword(result.getString(result.getColumnIndex(CARDS_COLUMN_PASSWORD)));
            cardArrayList.add(card);
            result.moveToNext();
        }
        return cardArrayList;
    }
    public double getWalletBalance(){
        double balance = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("Select * from Cards",null);
        result.moveToFirst();
        while (result.isAfterLast() == false){
            balance=balance+result.getDouble(result.getColumnIndex(CARDS_COLUMN_BALANCE));
            result.moveToNext();
        }
        return balance;
    }
    public boolean updateBalance(double balance,int cardid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CARDS_COLUMN_BALANCE,balance);
        db.update(CARDS_TABLE_NAME,contentValues,CARDS_COLUMN_CARD_ID+"="+String.valueOf(cardid),null);
        return true;
    }
    public boolean withdrawBalance(double balance,int cardid){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("Select * from "+CARDS_TABLE_NAME+"where"+CARDS_COLUMN_CARD_ID+"="+cardid,null);
        result.moveToFirst();
        while (result.isAfterLast() == false) {
            Card card = new Card();
            card.setCardBalance(Double.valueOf(result.getString(result.getColumnIndex(CARDS_COLUMN_BALANCE))));
            ContentValues contentValues = new ContentValues();
            contentValues.put(CARDS_COLUMN_BALANCE, card.getCardBalance()-balance);
            db.update(CARDS_TABLE_NAME, contentValues, CARDS_COLUMN_CARD_ID + "=" + cardid, null);
            return true;
        }
        return false;
    }
}
