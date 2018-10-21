package com.android.nfc.smartpay_v3.DBA;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.android.nfc.smartpay_v3.Classes.Card;
import com.android.nfc.smartpay_v3.Classes.PaymentInfo;

import java.util.ArrayList;

/**
 * Created by Fifty on 6/20/2018.
 */

public class LocalDBA extends SQLiteOpenHelper  {

    private static final  String DATABASE_NAME = "smartpay.db";
    private static final  String CARDS_TABLE_NAME = "Cards";
    private static final  String CARDS_COLUMN_CARDNO = "c_id";
    private static final  String CARDS_COLUMN_BANK_NAME = "c_bank";
    private static final  String CARDS_COLUMN_HOLDER_NAME = "c_holder_name";
    private static final  String CARDS_COLUMN_BALANCE = "c_balance";
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
                +"(c_id text primary key , c_bank text , c_holder_name text , c_balance real ,c_icon integer , c_flag integer)");
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






/*    public boolean insertAccount(int userid , String username , String password  , String phone , String email , double balance , String address , int flag){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("a_id",userid);
        contentValues.put("a_username",username);
        contentValues.put("a_password",password);
        contentValues.put("a_phone",phone);
        contentValues.put("a_email",email);
        contentValues.put("a_balance",balance);
        contentValues.put("a_address",address);
        contentValues.put("a_flag",1);
        db.insert("Accounts",null,contentValues);
        return true;
    }
    public boolean UpdateAccount(Integer id ,String username , String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("a_username",username);
        contentValues.put("a_password",password);
        db.update("Accounts",contentValues,"id = ?",new String[]{Integer.toString(id)});
        return true;
    }
    public Integer deleteAccount(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Accounts","id = ?",new String[]{Integer.toString(id)});
    }

    public ArrayList<Account> getAllAccounts(){
        ArrayList<Account> accountArrayList = new ArrayList<Account>();
        //hp = new hashmap
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("Select * from Accounts",null);
        result.moveToFirst();
        while (result.isAfterLast() == false){
            Account account = new Account();
            account.setUsername(result.getString(result.getColumnIndex(ACCOUNT_COLUMN_USERNAME)));
            account.setUserid(result.getInt(result.getColumnIndex(ACCOUNT_COLUMN_USER_ID)));
            account.setAddress(result.getString(result.getColumnIndex(ACCOUNT_COLUMN_ADDRESS)));
            account.setBalance(result.getDouble(result.getColumnIndex(ACCOUNT_COLUMN_BALANCE)));
            account.setEmail(result.getString(result.getColumnIndex(ACCOUNT_COLUMN_EMAIL)));
            account.setPassword(result.getString(result.getColumnIndex(ACCOUNT_COLUMN_PASSWORD)));
            account.setPhone(result.getString(result.getColumnIndex(ACCOUNT_COLUMN_PHONE)));
            account.setFlag(result.getInt(result.getColumnIndex(ACCOUNT_COLUMN_FLAG)));
            accountArrayList.add(account);
            result.moveToNext();
        }
        return accountArrayList;
    }*/

    public boolean insertCard(Card card){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("c_id",card.getCardNo());
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
            card.setCardNo(result.getString(result.getColumnIndex(CARDS_COLUMN_CARDNO)));
            card.setBankName(result.getString(result.getColumnIndex(CARDS_COLUMN_BANK_NAME)));
            card.setCardBalance(result.getDouble(result.getColumnIndex(CARDS_COLUMN_BALANCE)));
            card.setCardHolderName(result.getString(result.getColumnIndex(CARDS_COLUMN_HOLDER_NAME)));
            card.setCardIcon(result.getInt(result.getColumnIndex(CARDS_COLUMN_ICON)));
            card.setCardFlag(result.getInt(result.getColumnIndex(CARDS_COLUMN_FLAG)));
            cardArrayList.add(card);
            result.moveToNext();
        }
        return cardArrayList;
    }
    public boolean updateBalance(double balance,String cardNo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CARDS_COLUMN_BALANCE,balance);
        db.update(CARDS_TABLE_NAME,contentValues,CARDS_COLUMN_CARDNO+"="+cardNo,null);
        return true;
    }
}
