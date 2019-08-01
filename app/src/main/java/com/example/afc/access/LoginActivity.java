package com.example.afc.access;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.afc.R;
import com.example.afc.app.SessionManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private String regexUser = "\\w+?";

    private EditText user_nameTv, user_passwordTv;
    private ProgressDialog progressDialog;

    private SessionManagement session;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user_nameTv = (EditText) findViewById(R.id.login_user);
        user_passwordTv = (EditText) findViewById(R.id.login_password);

        session = new SessionManagement(getApplicationContext());
        mQueue =  Volley.newRequestQueue(getApplicationContext());
    }

    public void loginUser(View view){
        String user_name = user_nameTv.getText().toString();
        String user_password = user_passwordTv.getText().toString();

        if (!user_name.matches(regexUser) || user_name.length()<5) {
            popUp(this, getString(R.string.alert_regex_title), getString(R.string.alert_regex_user_message), getString(R.string.alert_button_tryagain));
            return;
        }
        if (!user_password.matches(regexUser) || user_password.length()<5) {
            popUp(this, getString(R.string.alert_regex_title), getString(R.string.alert_regex_password_message), getString(R.string.alert_button_tryagain));
            return;
        }
        progressDialog = showProgressDialog(this, getString(R.string.progress_login), true);
        jsonParseLoginUser(user_name, user_password, this);
    }

    public void jsonParseLoginUser(final String user_name, final String user_password, final Context _context) {
        String url = session.getAFCLink() + "/afc/access/login.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject j = new JSONObject(response);
                    JSONArray jsonArray = j.getJSONArray("user_data"); 
                    
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject intermediar = jsonArray.getJSONObject(i);
                        int id = intermediar.getInt("id");
                        if(id==0) {
                            progressDialog.dismiss();
                            popUp(_context, getString(R.string.alert_wrong_account_title), getString(R.string.alert_wrong_account_message), getString(R.string.alert_button_tryagain));
                            return;
                        }

                        String type =  intermediar.getString("type");
                        String user = intermediar.getString("user");
                        String email = intermediar.getString("email");
                        String lname  = intermediar.getString("lname");
                        String fname = intermediar.getString("fname");
                        String phone = intermediar.getString("phone");
                        String city  = intermediar.getString("city");
                        String year = intermediar.getString("year");
                        String spec = intermediar.getString("spec");
                        String pic = intermediar.getString("pic");

                        String user_id = id+"";
                        session.createSession(user_id, type, user, email, fname, lname, phone, city, year, spec, pic);
                        progressDialog.dismiss();
                        goToLoadingData();
                    }
                    
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    popUp(_context, getString(R.string.alert_error_title), response, getString(R.string.alert_button_tryagain));

                }
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
                params.put("user_name", user_name); //parametrii POST
                params.put("user_password", user_password);
                return params;
            }
        };
        mQueue.add(request);
    }

    public void goToRegister(View view){
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        // Closing all the Activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add new Flag to start new Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
        showProgressDialog(this, getString(R.string.progress_loading), false);
        finish();
    }

    public void goToLoadingData(){
        Intent intent = new Intent(getApplicationContext(), LoadDataActivity.class);
        // Closing all the Activities
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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

    public ProgressDialog showProgressDialog(Context context, String message, Boolean cancelable){
        ProgressDialog m_Dialog = new ProgressDialog(context);
        m_Dialog.setMessage(message);
        m_Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_Dialog.setCancelable(cancelable);
        m_Dialog.show();
        return m_Dialog;
    }

    @Override
    public void onBackPressed() {}
}