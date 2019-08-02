package com.example.afc.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.afc.R;
import com.example.afc.app.Config;
import com.example.afc.app.DBManagement;
import com.example.afc.app.SessionManagement;
import com.example.afc.chat.ChatLobbyActivity;
import com.example.afc.main.MainActivity;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Random;

/* This is a Base Activity. Includes features used by multiple activies across the APP */

public abstract class BaseActivity extends AppCompatActivity {
    public SessionManagement session;
    public HashMap<String, String> sessionData;
    public RequestQueue mQueue;

    public Toolbar toolbar;
    public ActionBarDrawerToggle toggleBtn;
    public DrawerLayout drawerLayout;
    public NavigationView nv;

    public RelativeLayout progressCircle;
    public DBManagement db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check session(if user is logged in)
        session = new SessionManagement(getApplicationContext());
        session.checkLogin();
        sessionData = session.getUserDetails();

        //setup activity layout elements
        setContentView(getLayoutResource());
        configureToolbar();
        configureSideMenu();
        configureSideMenuHeader();

        startLoadingBar();

        db = new DBManagement(getApplicationContext());
        mQueue =  Volley.newRequestQueue(getApplicationContext());
    }
    protected abstract int getLayoutResource();

    //Top menu intialization
    protected void configureToolbar() {
        toolbar = (Toolbar) findViewById(R.id.top_menu_layout);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    //Side menu initialization
    private void configureSideMenu(){
        nv = (NavigationView) findViewById(R.id.side_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        toggleBtn = new ActionBarDrawerToggle(this, drawerLayout, R.string.sidemenu_open, R.string.sidemenu_close);
        drawerLayout.addDrawerListener(toggleBtn);
        toggleBtn.syncState();

        //setare butoane meniu lateral
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Intent intent;
                switch (menuItem.getItemId()) {
                    case(R.id.nav_home):
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        return true;
                    case(R.id.nav_settings):
                        return true;
                    case(R.id.nav_messages):
                        intent = new Intent(getApplicationContext(), ChatLobbyActivity.class);
                        startActivity(intent);
                        return true;
                    case(R.id.nav_logout):
                        session.logoutUser();
                        return true;
                }
                return true;
            }
        });
    }

    public void configureSideMenuHeader(){
        final Context context = getApplicationContext();
        View header = nv.getHeaderView(0);

        final ImageView imageView = (ImageView) header.findViewById(R.id.menu_header_avatar);
        TextView mName = (TextView) header.findViewById(R.id.menu_header_name);
        TextView mEmail = (TextView) header.findViewById(R.id.menu_header_email);

        String url = session.getAFCLink()+"/afc/assets/profile_pics/"+sessionData.get("us_pic");
        String name = sessionData.get(Config.KEY_LNAME) + " " + sessionData.get(Config.KEY_FNAME);

        mName.setText(name);
        mEmail.setText(sessionData.get(Config.KEY_EMAIL));

        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

    public void startLoadingBar(){
        progressCircle = (RelativeLayout) findViewById(R.id.progress_rl);
        progressCircle.setVisibility(View.VISIBLE);
    }

    public void stopLoadingBar(){
        progressCircle.setVisibility(View.GONE);
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    public void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.PREF_NAME, 0);
        String regId = pref.getString(Config.KEY_CHAT_ID, null);

        if (!TextUtils.isEmpty(regId))
            alert(regId);
        else
            alert("Firebase Reg Id is not received yet!");
    }

    public void alert(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    public void popUp(final Context context, String title, String message, String button) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton(button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public int getRandom(int from, int to){
        Random r = new Random();
        return r.nextInt(to-from) + from;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggleBtn.onOptionsItemSelected(item)){
            return true;
        }

        switch (item.getItemId()) {
            //case R.id.nav_mail: stopLoadingBar(); return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
