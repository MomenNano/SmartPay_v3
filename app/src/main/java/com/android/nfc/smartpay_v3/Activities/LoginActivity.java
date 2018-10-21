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
        System.out.println("lol");

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
        final EditText username = (EditText) findViewById(R.id.login_username);
        final EditText password = (EditText) findViewById(R.id.login_password);
//        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//        startActivity(intent);


        final RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);

        /*final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, myurl,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("status").contains("true")){
                                //localDBA.insertAccount(username.getText().toString(),password.getText().toString());
                                SharedPreferences sharedPreferences = getSharedPreferences(Configuration.MY_PREFERENCE,MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Configuration.KEY_PREFERENCE_USERNAME,username.getText().toString());
                                editor.putString(Configuration.KEY_PREFERENCE_USER_ID,response.getString(Configuration.KEY_PREFERENCE_USER_ID));
                                editor.commit();
                                Toast.makeText(getApplicationContext(),"Login Successfully",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Wrong username or password",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            //e.printStackTrace();
                        }
                        Log.d("sucess","sucessfull login");
                        requestQueue.stop();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"connection error",Toast.LENGTH_LONG).show();
                        Log.d("error","error in login");

                        requestQueue.stop();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userName", username.getText().toString());
                params.put("password", password.getText().toString());
                return params;
            }
        };

        requestQueue.add(jsonObjectRequest);*/

        StringRequest stringRequest = new StringRequest(Request.Method.POST, myurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject JsonResponse = new JSONObject(response);
                            if (JsonResponse.getString("status").contains("true")) {


                                //localDBA.insertAccount(username.getText().toString(),password.getText().toString());
                                SharedPreferences sharedPreferences = getSharedPreferences(Configuration.MY_PREFERENCE, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Configuration.KEY_PREFERENCE_USERNAME, username.getText().toString());
                                editor.putString(Configuration.KEY_PREFERENCE_USER_ID, JsonResponse.getString(Configuration.KEY_PURCHASER_ID));
                                editor.putString(Configuration.KEY_PHONE_SERIAL_NO, Build.SERIAL);
                                editor.commit();

                                Toast.makeText(getApplicationContext(),"Login Successfully",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                startActivity(intent);
//                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//                                startActivity(intent);
                                //if(sharedPreferences != null && sharedPreferences.getString(Configuration.KEY_PREFERENCE_USERNAME,null) != null){
//                                    Intent intent = new Intent(getBaseContext(),MainActivity.class);
//                                    startActivity(intent);
                                //}
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Wrong username or password",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("sucess","sucessfull login");
                        //requestQueue.stop();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"connection error",Toast.LENGTH_LONG).show();
                        Log.d("error","error in login");
                        requestQueue.stop();

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("userName",username.getText().toString());
                params.put("password",password.getText().toString());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //requestQueue.add(stringRequest);
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

        /*//localDBA.insertAccount(username.getText().toString(),password.getText().toString());
        SharedPreferences sharedPreferences = getSharedPreferences(Configuration.MY_PREFERENCE,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Configuration.KEY_PREFERENCE_USERNAME,username.getText().toString());
        editor.putInt("user_id",response);
        editor.commit();
        Toast.makeText(this,"Login Successfully",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);

        LocalDBA localDBA = new LocalDBA(this);
        DBA dba = new DBA();
        /*int result = dba.login(username.getText().toString(),password.getText().toString());
        if (result!=-1){
            //localDBA.insertAccount(username.getText().toString(),password.getText().toString());
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
        }*/
    }
    public void register(View view){

        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

}
