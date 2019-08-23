package com.example.afc.access;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.afc.R;
import com.example.afc.app.Config;
import com.example.afc.app.SessionManagement;
import com.example.afc.main.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;

public class LoadDataActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private int REQUEST_PERMISSION;

    private SessionManagement session;
    HashMap<String, String> sessionData;
    private RequestQueue mQueue;
    Animation mRotateAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_data);

        mRotateAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotation);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        // Start animating the image
        final ImageView splash = (ImageView) findViewById(R.id.loading_pen);
        splash.startAnimation(mRotateAnimation);
        //getAFCConnectionLink();

        mQueue =  Volley.newRequestQueue(getApplicationContext());
        session = new SessionManagement(getApplicationContext());

        session.checkLogin();
        sessionData = session.getUserDetails();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoadDataActivity.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String mToken = instanceIdResult.getToken();
                Log.d(TAG, "TOKEN FOUND: " + mToken);

                SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.PREF_NAME, 0);
                String regId = pref.getString(Config.KEY_CHAT_ID, null);

                if (TextUtils.isEmpty(regId)){

                    // Saving reg id to shared preferences
                    session.storeRegIdInPref(mToken);

                    // sending reg id to your server
                    session.registerToFirebase(mToken, "0");

                    Log.d(TAG, "TOKEN REGISTERED: " + mToken);
                }
                // Notify UI that registration has completed, so the progress indicator can be hidden.
                Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
                registrationComplete.putExtra("token", mToken);
                LocalBroadcastManager.getInstance(LoadDataActivity.this).sendBroadcast(registrationComplete);
            }
        });
        //ask for file managing permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int readPermission = ContextCompat.checkSelfPermission(LoadDataActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermission = ContextCompat.checkSelfPermission(LoadDataActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (readPermission != PackageManager.PERMISSION_GRANTED || writePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LoadDataActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION);
            } else {
                goToMain();
            }
        }
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

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                goToMain();
            } else {
                // User refused to grant permission.
                finish();
            }
        }
    }
}
