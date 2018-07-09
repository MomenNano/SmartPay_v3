package com.android.nfc.smartpay_v3.Classes;

import java.util.Date;

/**
 * Created by Fifty on 5/8/2018.
 */

public class PaymentInfo {
    String companeyName ;
    int CompaneyType ;
    Date paymentDate ;
    String stringDate;
    String stringTime;
    double billAmount;

    public String getCompaneyName() {
        return companeyName;
    }

    public void setCompaneyName(String companeyName) {
        this.companeyName = companeyName;
    }

    public int getCompaneyType() {
        return CompaneyType;
    }

    public void setCompaneyType(int companeyType) {
        CompaneyType = companeyType;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }


    public double getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(double billAmount) {
        this.billAmount = billAmount;
    }

    public String getStringDate() {

        return stringDate;
    }

    public void setStringDate(String stringDate) {
        this.stringDate = stringDate;
    }

    public String getStringTime() {
        return stringTime;
    }

    public void setStringTime(String stringTime) {
        this.stringTime = stringTime;
    }
}
