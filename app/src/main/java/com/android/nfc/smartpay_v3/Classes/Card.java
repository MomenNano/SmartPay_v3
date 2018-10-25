package com.android.nfc.smartpay_v3.Classes;

/**
 * Created by Fifty on 6/11/2018.
 */

public class Card {
    String bankName;
    String cardNo;
    String cardHolderName;
    double cardBalance;
    int cardFlag;
    int cardIcon;
    String exDate;
    String stringBankName;
    int cardId;


    public Card(String bankName, String cardNo, int cardIcon) {
        this.bankName = bankName;
        this.cardNo = cardNo;
        this.cardIcon = cardIcon;
    }

    public Card() {

    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getBankName() {
        return bankName;
    }

    public String getStringBankName() {
        return stringBankName;
    }

    public void setStringBankName(String stringBankName) {
        this.stringBankName = stringBankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public int getCardIcon() {
        return cardIcon;
    }

    public void setCardIcon(int cardIcon) {
        this.cardIcon = cardIcon;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public double getCardBalance() {
        return cardBalance;
    }

    public void setCardBalance(double cardBalance) {
        this.cardBalance = cardBalance;
    }

    public int getCardFlag() {
        return cardFlag;
    }

    public void setCardFlag(int cardFlag) {
        this.cardFlag = cardFlag;
    }

    public String getExDate() {
        return exDate;
    }

    public void setExDate(String exDate) {
        this.exDate = exDate;
    }
}
