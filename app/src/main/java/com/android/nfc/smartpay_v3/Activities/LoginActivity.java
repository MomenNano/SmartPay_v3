package com.android.nfc.smartpay_v3.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.nfc.smartpay_v3.DBA.Configuration;
import com.android.nfc.smartpay_v3.DBA.DBA;
import com.android.nfc.smartpay_v3.DBA.LocalDBA;
import com.android.nfc.smartpay_v3.R;

public class LoginActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*LocalDBA localDBA = new LocalDBA(this);
        Cursor result =localDBA.autoLogin();
        if(result!=null){
            DBA dba = new DBA();
            dba.updateData();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }*/
        SharedPreferences sharedPreferences = getSharedPreferences(Configuration.MY_PREFERENCE,MODE_PRIVATE);
        if(sharedPreferences != null && sharedPreferences.getString(Configuration.KEY_PREFERENCE_USERNAME,null) != null){
            Intent intent = new Intent(getBaseContext(),MainActivity.class);
            startActivity(intent);
        }

    }
    public void login(View view){
        EditText username = (EditText) findViewById(R.id.login_username);
        EditText password = (EditText) findViewById(R.id.login_password);
        LocalDBA localDBA = new LocalDBA(this);
        DBA dba = new DBA();
        int result = dba.login(username.getText().toString(),password.getText().toString());
        if (result!=-1){
           // localDBA.insertAccount(username.getText().toString(),password.getText().toString());
            SharedPreferences sharedPreferences = getSharedPreferences(Configuration.MY_PREFERENCE,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Configuration.KEY_PREFERENCE_USERNAME,username.getText().toString());
            editor.putInt("user_id",result);
            editor.commit();
            Toast.makeText(this,"Login Successfully",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(this,"sorry username or password is wrong",Toast.LENGTH_LONG).show();
        }
    }
    public void switchToRegister(View view){

        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }
}
