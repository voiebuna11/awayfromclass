package com.example.afc.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.afc.access.LoginActivity;
import com.example.afc.main.MainActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class SessionManagement {
    Timer internetChecker;
    AlertDialog.Builder noInternetDialog;
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public SessionManagement(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(Config.PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createSession(String id, String type, String user, String email, String fname, String lname, String phone, String city,
                                   String year, String spec, String pic){
        // Storing login value as TRUE
        editor.putBoolean(Config.IS_LOGIN, true);

        // Storing name in pref
        editor.putString(Config.KEY_ID, id);
        editor.putString(Config.KEY_TYPE, type);
        editor.putString(Config.KEY_USER, user);
        editor.putString(Config.KEY_EMAIL, email);
        editor.putString(Config.KEY_FNAME, fname);
        editor.putString(Config.KEY_LNAME, lname);
        editor.putString(Config.KEY_PHONE, phone);
        editor.putString(Config.KEY_CITY, city);
        editor.putString(Config.KEY_YEAR, year);
        editor.putString(Config.KEY_SPEC, spec);
        editor.putString(Config.KEY_PIC, pic);
        // commit changes
        editor.commit();
    }

    public void setAFCLink(String link){
        editor.putString(Config.KEY_LINK, link);
        editor.commit();
        goToMain();
    }

    public String getAFCLink(){
        //return pref.getString(KEY_LINK, null);
        return Config.KEY_LINK;
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(Config.KEY_ID, pref.getString(Config.KEY_ID, null));
        user.put(Config.KEY_TYPE, pref.getString(Config.KEY_TYPE, null));
        user.put(Config.KEY_USER, pref.getString(Config.KEY_USER, null));
        user.put(Config.KEY_EMAIL, pref.getString(Config.KEY_EMAIL, null));
        user.put(Config.KEY_FNAME, pref.getString(Config.KEY_FNAME, null));
        user.put(Config.KEY_LNAME, pref.getString(Config.KEY_LNAME, null));
        user.put(Config.KEY_PHONE, pref.getString(Config.KEY_PHONE, null));
        user.put(Config.KEY_CITY, pref.getString(Config.KEY_CITY, null));
        user.put(Config.KEY_YEAR, pref.getString(Config.KEY_YEAR, null));
        user.put(Config.KEY_SPEC, pref.getString(Config.KEY_SPEC, null));
        user.put(Config.KEY_PIC, pref.getString(Config.KEY_PIC, null));
        user.put(Config.KEY_CHAT_ID, pref.getString(Config.KEY_CHAT_ID, null));
        // return user
        return user;
    }

    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // Staring Activity
            _context.startActivity(i);
        }
    }

    public void logoutUser(){
        //Remove fcm token from online db
        registerToFirebase("abcd", "1");

        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        _context.startActivity(i);
    }

    private boolean isLoggedIn(){
        HashMap<String, String> user = getUserDetails();
        String username = user.get(Config.KEY_USER);

        if(username != null){
            return true;
        } else {
            return false;
        }
    }

    public void registerToFirebase(final String token, final String clearToken) {
        RequestQueue mQueue =  Volley.newRequestQueue(_context);
        final HashMap<String, String> user = getUserDetails();
        String url = getAFCLink() + "/afc/users/set_user_fcm.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                    //do nothing on response
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("chat_id", token); //parametrii POST
                params.put("clear_id", clearToken); //parametrii POST
                params.put("user_name", user.get(Config.KEY_USER));
                return params;
            }
        };
        mQueue.add(request);
    }

    public void storeRegIdInPref(String token) {
        editor.putString(Config.KEY_CHAT_ID, token);
        // commit changes
        editor.commit();
    }

    private void goToMain(){
        Intent intent = new Intent(_context, MainActivity.class);
        // Closing all the Activities
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        _context.startActivity(intent);
    }
}