package com.android.nfc.smartpay_v3.Classes;

/**
 * Created by Fifty on 6/11/2018.
 */

public class BillInfo {
    double billAmount ;
    Companey companey;
    public BillInfo(){

    }

    public BillInfo(double billAmount, Companey companey) {
        this.billAmount = billAmount;
        this.companey = companey;
    }

    public double getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(double billAmount) {
        this.billAmount = billAmount;
    }

    public Companey getCompaney() {
        return companey;
    }

    public void setCompaney(Companey companey) {
        this.companey = companey;
    }
}
