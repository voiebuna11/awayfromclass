package com.example.afc.access;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.afc.R;
import com.example.afc.main.MainActivity;
import com.example.afc.app.Config;
import com.example.afc.app.SessionManagement;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;

public class LoadDataActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    private SessionManagement session;
    HashMap<String, String> sessionData;
    private RequestQueue mQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_data);

        RotateAnimation anim = new RotateAnimation(0f, 350f, 25f, 25f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(700);
        // Start animating the image
        final ImageView splash = (ImageView) findViewById(R.id.loading_pen);
        splash.startAnimation(anim);
        //getAFCConnectionLink();

        mQueue =  Volley.newRequestQueue(getApplicationContext());
        session = new SessionManagement(getApplicationContext());

        session.checkLogin();
        sessionData = session.getUserDetails();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoadDataActivity.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String mToken = instanceIdResult.getToken();

                SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.PREF_NAME, 0);
                String regId = pref.getString(Config.KEY_CHAT_ID, null);

                if (TextUtils.isEmpty(regId)){

                    // Saving reg id to shared preferences
                    session.storeRegIdInPref(mToken);

                    // sending reg id to your server
                    session.registerToFirebase(mToken, "0");

                    Log.e(TAG, "TOKEN REGISTERED: " + mToken);
                }
                // Notify UI that registration has completed, so the progress indicator can be hidden.
                Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
                registrationComplete.putExtra("token", mToken);
                LocalBroadcastManager.getInstance(LoadDataActivity.this).sendBroadcast(registrationComplete);

                goToMain();
            }
        });
    }

    private void goToMain(){
        // redirect  to Main Activity
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        // Closing all the Activities
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Staring Activity
        getApplicationContext().startActivity(i);
    }

    private void alert(String text){
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
}
