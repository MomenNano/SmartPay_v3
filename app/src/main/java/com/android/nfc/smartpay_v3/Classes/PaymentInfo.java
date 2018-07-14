package com.android.nfc.smartpay_v3.Classes;

import java.util.Date;

/**
 * Created by Fifty on 5/8/2018.
 */

public class PaymentInfo {
    String purchaserName;
    String companyName;
    int CompanyType;
    Date paymentDate ;
    String date;
    String stringTime;
    String billAmount;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getCompanyType() {
        return CompanyType;
    }

    public void setCompanyType(int companyType) {
        CompanyType = companyType;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }


    public String getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(String billAmount) {
        this.billAmount = billAmount;
    }

    public String getDate() {

        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStringTime() {
        return stringTime;
    }

    public void setStringTime(String stringTime) {
        this.stringTime = stringTime;
    }
}
