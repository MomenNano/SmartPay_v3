package com.android.nfc.smartpay_v3.DBA;

/**
 * Created by Fifty on 6/29/2018.
 */

public class Configuration {
    //SERVER URLS
    public static final String REGISTER_URL = "http://17aab495.ngrok.io/register/purchaser";
    public static final String LOGIN_URL = "http://17aab495.ngrok.io/login";
    public static final String SEND_PAYMENT_INFO_URL = "http://17aab495.ngrok.io/add/seller/payment";
    public static final String PAY_TRANSACTION_URL = "";
    public static final String PAYMENT_HISTORY_URL = "";
    public static final String DEDUCT_MONEY_URL = "http://17aab495.ngrok.io/deduct";
    public static final String ADD_NEW_CARD_URL = "http://17aab495.ngrok.io/addCard";
    public static final String  GET_STORES_URL = "";
    public static final String CHECK_PAYMENT_INFO_URL = "http://c419caf9.ngrok.io/add/seller/payment/check";



    //DATA KEYS ON SERVER
    public static final String KEY_RESULT = "";
    public static final String KEY_MESSAGE = "message";
    //User Tables Keys
    public static final String KEY_USER_ID = "";
    public static final String kEY_USERNAME = "userName";
    public static final String KEY_FULL_NAME = "fullName";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE_NO = "phoneNO";
    //Cards Table Keys
    public static final String KEY_CARD_NO = "card_no";
    public static final String KEY_CARD_BANK_NAME = "card_bankname";
    public static final String KEY_CARD_BALANCE = "card_balance";
    public static final String KEY_CARD_ICON = "card_icon";
    public static final String KEY_CARD_HOLDER_NAME = "cardHolderName";
    public static final String KEY_CARD_EX_DATE = "cardExDate";
    public static final String KEY_PHONE_SERIAL_NO = "serialNo";
    public static final String KEY_PURCHASER_ID = "purchaserId";
    //Local DataBase Tables And Keys
    public static final  String TABLE_PAYMENT_TRANSACTION_TABLE_NAME = "PaymentsTransaction";
    public static final  String TABLE_PAYMENT_TRANSACTION_ID = "p_id";
    public static final  String TABLE_PAYMENT_TRANSACTION_UNIQUE_ID = "p_unique_id";
    public static final  String TABLE_PAYMENT_TRANSACTION_BILL_AMOUNT = "p_bill_Amount";
    public static final  String TABLE_PAYMENT_TRANSACTION_DATE = "p_payment_date";
    public static final  String TABLE_PAYMENT_TRANSACTION_TIME = "p_time";
    public static final  String TABLE_PAYMENT_TRANSACTION_COMPANY_ID = "p_company_id";
    public static final  String TABLE_PAYMENT_TRANSACTION_COMPANY_BANK_ACCUONT = "p_company_bank_account";
    public static final  String TABLE_PAYMENT_TRANSACTION_COMPANY_TYPE = "p_company_type";
    public static final  String TABLE_PAYMENT_TRANSACTION_COMPANY_NAME = "p_company_name";
    public static final String TABLE_PAYMENT_TRANSACTION_PURCHASER_ID = "p_purchaser_id";
    public static final  String TABLE_PAYMENT_TRANSACTION_PURCHASER_NAME = "p_purchaser_name";
    public static final  String TABLE_PAYMENT_TRANSACTION_PURCHASER_CARD_ID = "p_purchaser_card_id";
    public static final  String TABLE_PAYMENT_TRANSACTION_FLAG = "p_flag";
    public static final  String TABLE_PAYMENT_TRANSACTION_SEND_FLAG = "p_send";

    //Company Tables Key
    public static final String KEY_COMPANY_NAME = "";
    public static final String KEY_COMPANY_TYPE = "";
    public static final String KEY_COMPANY_LATITUDE = "";
    public static final String KEY_COMPANY_LONGITUDE = "";
    //PaymentTransaction Table Key
    public static final String KEY_PAYMENT_BILL_AMOUNT = "";
    public static final String KEY_PAYMENT_COMPANY_NAME = "";
    public static final String KEY_PAYMENT_COMPANY_TYPE = "";
    public static final String KEY_PAYMENT_DATE = "";
    public static final String KEY_PAYMENT_TIME = "";
    //Sheared Preference Keys
    public static final String MY_PREFERENCE = "user_session";
    public static final String KEY_PREFERENCE_USER_ID = "user_id";
    public static final String KEY_PREFERENCE_USERNAME = "username";


    public static final String MONEY_TO_DEDUCT ="MoneyToDeduct" ;

    //PAYMENT INFO JSON KEYS
    public static final String PAYMENT_TRANSACTION_ID = "transactionId";
    public static final String PAYMENT_TRANSACTION_UNIQUE_ID = "uniqueId";
    public static final  String PAYMENT_TRANSACTION_BILL_AMOUNT = "billAmount";
    public static final  String PAYMENT_TRANSACTION_DATE = "date";
    public static final  String PAYMENT_TRANSACTION_TIME = "time";
    public static final  String PAYMENT_TRANSACTION_COMPANY_ID = "sellerId";
    public static final  String PAYMENT_TRANSACTION_COMPANY_BANK_ACCUONT = "sellerBankAccount";
    public static final  String PAYMENT_TRANSACTION_PURCHASER_ID = "purchaserId";
    public static final  String PAYMENT_TRANSACTION_PURCHASER_CARD_ID = "purchaserCardID";

    public static final String CODE ="code" ;
    public static final String MESSAGE ="message" ;
    public static final String DATABASE_NAME = "smartpay.db";
}
