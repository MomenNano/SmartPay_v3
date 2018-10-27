package com.android.nfc.smartpay_v3.Activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.nfc.smartpay_v3.DBA.BackgroundService;
import com.android.nfc.smartpay_v3.DBA.Configuration;
import com.android.nfc.smartpay_v3.Fragments.AddCardFragment;
import com.android.nfc.smartpay_v3.Fragments.HistoryFragment;
import com.android.nfc.smartpay_v3.Fragments.MapFragment;
import com.android.nfc.smartpay_v3.Fragments.MyCardsFragment;
import com.android.nfc.smartpay_v3.Fragments.PayFragment;
import com.android.nfc.smartpay_v3.Fragments.WalletFragment;
import com.android.nfc.smartpay_v3.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //jgfghjj
    final static String MY_PREFERENCE = "user_session";
    private static final  String FULLNAME = "full_name";
    private static final  String USER_ID = "user_id";
    private static final  String PHONE = "phone_no";
    private static final  String EMAIL = "email";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BackgroundService.servicesRun = true;
        Intent service = new Intent(getBaseContext(),BackgroundService.class);
        startService(service);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView usernameTextView = (TextView) headerView.findViewById(R.id.tv_username);
        TextView emailTextView = (TextView) headerView.findViewById(R.id.tv_email);
        SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFERENCE,MODE_PRIVATE);
        usernameTextView.setText(sharedPreferences.getString(FULLNAME,null));
        emailTextView.setText(sharedPreferences.getString(EMAIL,null));
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new WalletFragment()).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fragmentManager = getFragmentManager();
        int id = item.getItemId();

        if (id == R.id.nav_my_cards) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new MyCardsFragment()).commit();
        } else if (id == R.id.nav_add_card) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new AddCardFragment()).commit();

        } else if (id == R.id.nav_pay) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new PayFragment()).commit();
            Intent intent = new Intent(getBaseContext(),PurchaserMainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_map) {
        fragmentManager.beginTransaction().replace(R.id.content_frame, new MapFragment()).commit();

        } else if (id == R.id.nav_history) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new HistoryFragment()).commit();
        }
        else if (id == R.id.nav_wallet){
            fragmentManager.beginTransaction().replace(R.id.content_frame, new WalletFragment()).commit();
        }
        else if (id == R.id.nav_logout){
            SharedPreferences sharedPreferences = getSharedPreferences(Configuration.MY_PREFERENCE,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
            Intent intent = new Intent(getBaseContext(),LoginActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
