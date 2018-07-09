package com.android.nfc.smartpay_v3.Classes;

/**
 * Created by Fifty on 6/25/2018.
 */

public class Account {
    int userid ;
    String username ;
    String fullName ;
    String password;
    String phone;
    String email;
    double balance;
    String address ;
    int flag;

    public Account(int userid, String username, String fullName, String password, String phone, String email, double balance, String address, int flag) {
        this.userid = userid;
        this.username = username;
        this.fullName = fullName;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.balance = balance;
        this.address = address;
        this.flag = flag;
    }

    public Account() {

    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
