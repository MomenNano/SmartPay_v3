package com.android.nfc.smartpay_v3.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.nfc.smartpay_v3.Classes.Account;
import com.android.nfc.smartpay_v3.Classes.Companey;
import com.android.nfc.smartpay_v3.DBA.Configuration;
import com.android.nfc.smartpay_v3.DBA.DBA;
import com.android.nfc.smartpay_v3.DBA.MySingleton;
import com.android.nfc.smartpay_v3.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    final static String MY_PREFERENCE = "user_session";
    private static final  String FULLNAME = "full_name";
    private static final  String USER_ID = "user_id";
    private static final  String PHONE = "phone_no";
    private static final  String EMAIL = "email";
    private EditText username;
    private EditText password;
    private EditText confirmPassword;
    private EditText fullname;
    private EditText email;
    private EditText phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
    public void register(View view){
        username = (EditText) findViewById(R.id.et_username);
        password = (EditText) findViewById(R.id.et_password);
        confirmPassword = (EditText) findViewById(R.id.et_confirm_password);
        fullname = (EditText) findViewById(R.id.et_fullName);
        email = (EditText) findViewById(R.id.et_email);
        phone = (EditText) findViewById(R.id.et_phone_no);
        Account account = new Account();

        if(password.getText().toString().compareTo(confirmPassword.getText().toString())==0){
            account.setUsername(username.getText().toString());
            account.setFullName(fullname.getText().toString());
            account.setPassword(password.getText().toString());
            account.setPhone(phone.getText().toString());
            account.setEmail(email.getText().toString());
            registerToServer(account);
        }
        else{
            Toast.makeText(getBaseContext(), "Password and Confirm password isn't the same", Toast.LENGTH_SHORT).show();
        }



    }


    public void registerToServer(final Account account){ //Register send Account information from account object the server and storing them in the database

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString(Configuration.KEY_RESULT).compareTo("successfully") == 0){
                                openSession(account,jsonObject.getString(Configuration.KEY_USER_ID));
                            }
                            else if (jsonObject.getString(Configuration.KEY_RESULT).compareTo("failed") == 0){
                                Toast.makeText(getBaseContext(), jsonObject.getString(Configuration.KEY_MESSAGE), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), "Something went wrong try again", Toast.LENGTH_SHORT).show();
            }

        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parms = new HashMap<>();
                parms.put(Configuration.kEY_USERNAME,account.getUsername());
                parms.put(Configuration.KEY_FULL_NAME,account.getFullName());
                parms.put(Configuration.KEY_PASSWORD,account.getPassword());
                parms.put(Configuration.KEY_EMAIL,account.getEmail());
                parms.put(Configuration.KEY_PHONE_NO,account.getPhone());
                return parms;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }
    public void openSession(Account account,String userID){
        SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFERENCE,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FULLNAME,account.getFullName());
        editor.putString(EMAIL,account.getEmail());
        editor.putString(PHONE,account.getPhone());
        editor.putString(USER_ID,userID);
        editor.commit();
        Toast.makeText(getBaseContext(),"Registered Successfully",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getBaseContext(),MainActivity.class);
        startActivity(intent);
    }


}
