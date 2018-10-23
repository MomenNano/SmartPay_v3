package com.android.nfc.smartpay_v3.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.nfc.smartpay_v3.Classes.Account;
import com.android.nfc.smartpay_v3.DBA.Configuration;
import com.android.nfc.smartpay_v3.DBA.DBA;
import com.android.nfc.smartpay_v3.DBA.LocalDBA;
import com.android.nfc.smartpay_v3.R;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.android.nfc.smartpay_v3.DBA.MySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.nfc.smartpay_v3.DBA.Configuration.MY_PREFERENCE;

public class LoginActivity extends Activity {

    //String myurl = "http://452d5e27.ngrok.io/login";
    String myurl = Configuration.LOGIN_URL;
    LinearLayout loginLayout, registerLayout;
    Button openLogin, openRegister, loginBtn;
    ImageView logo;
    EditText l_username, l_password, r_username, r_password, r_confirm_password, r_user_phone_no;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            loginLayout.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginLayout = (LinearLayout) findViewById(R.id.login_Layout);
        registerLayout = (LinearLayout) findViewById(R.id.register_layout);
        openLogin = (Button) findViewById(R.id.open_login);
        openRegister = (Button) findViewById(R.id.open_register);
        loginBtn = (Button) findViewById(R.id.login);
        logo = (ImageView) findViewById(R.id.logo);

        openLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logo.setVisibility(View.VISIBLE);
                loginLayout.setVisibility(View.VISIBLE);
                registerLayout.setVisibility(View.GONE);
            }
        });
        openRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerLayout.setVisibility(View.VISIBLE);
                loginLayout.setVisibility(View.GONE);
                logo.setVisibility(View.GONE);
            }
        });
        handler.postDelayed(runnable, 1000);


        /*LocalDBA localDBA = new LocalDBA(this);
        Cursor result =localDBA.autoLogin();
        if(result!=null){
            DBA dba = new DBA();
            dba.updateData();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }*/
//        SharedPreferences sharedPreferences = getSharedPreferences(Configuration.MY_PREFERENCE,MODE_PRIVATE);
//        if(sharedPreferences != null && sharedPreferences.getString(Configuration.KEY_PREFERENCE_USERNAME,null) != null){
//            Intent intent = new Intent(getBaseContext(),MainActivity.class);
//            startActivity(intent);
//        }

    }

    public void login(View view) {
         l_username = (EditText) findViewById(R.id.login_username);
         l_password = (EditText) findViewById(R.id.login_password);


         RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);

         StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST,myurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String id = jsonObject.getString(Configuration.KEY_PREFERENCE_USER_ID);

                            if(jsonObject.getString("code").compareToIgnoreCase("1")==0){
                                //localDBA.insertAccount(username.getText().toString(),password.getText().toString());
                                SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFERENCE,MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Configuration.KEY_PREFERENCE_USERNAME,l_username.getText().toString());
                                editor.putString(Configuration.KEY_PREFERENCE_USER_ID,id);
                                editor.apply();
                                Toast.makeText(getBaseContext(),"Login Successfully",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getBaseContext(),MainActivity.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(getBaseContext(),"Wrong username or password",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            //e.printStackTrace();
                        }
                        Log.d("sucess","sucessfull login");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"connection error",Toast.LENGTH_LONG).show();
                        Log.d("error","error in login");

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Configuration.kEY_USERNAME, l_username.getText().toString());
                params.put(Configuration.KEY_PASSWORD, l_password.getText().toString());
                System.out.println(params.get(Configuration.kEY_USERNAME));
                System.out.println(params.get(Configuration.KEY_PASSWORD));
                return params;
            }
        };

        requestQueue.add(jsonObjectRequest);

    }
    public void registerToServer(View view){ //Register send Account information from account object the server and storing them in the database
        r_username = findViewById(R.id.r_username);
        r_password = findViewById(R.id.r_password);
        r_confirm_password = findViewById(R.id.r_confirm_password);
        r_user_phone_no = findViewById(R.id.r_user_phone_no);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Configuration.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString(Configuration.KEY_RESULT).compareTo("successfully") == 0){
                                openSession(r_username.getText().toString(),jsonObject.getString(Configuration.KEY_USER_ID));
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
                parms.put(Configuration.kEY_USERNAME,r_username.getText().toString());
                parms.put(Configuration.KEY_PASSWORD,r_password.getText().toString());
                parms.put(Configuration.KEY_PHONE_NO,r_user_phone_no.getText().toString());
                return parms;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }
    public void openSession(String username,String userID){
        SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFERENCE,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Configuration.KEY_PREFERENCE_USER_ID,userID);
        editor.putString(Configuration.KEY_PREFERENCE_USERNAME,username);
        editor.apply();
        Toast.makeText(getBaseContext(),"Registered Successfully",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getBaseContext(),MainActivity.class);
        startActivity(intent);
    }


}
