package com.android.nfc.smartpay_v3.Classes;

/**
 * Created by Fifty on 6/5/2018.
 */

public class BankItem {
    String bankName ;
    int bankicon;

    public BankItem(String bankName, int bankicon) {
        this.bankName = bankName;
        this.bankicon = bankicon;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public int getBankicon() {
        return bankicon;
    }

    public void setBankicon(int bankicon) {
        this.bankicon = bankicon;
    }
}
