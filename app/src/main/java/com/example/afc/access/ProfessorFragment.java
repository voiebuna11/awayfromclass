package com.example.afc.access;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.afc.R;
import com.example.afc.app.SessionManagement;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfessorFragment extends Fragment {
    private static final String TAG = "Student";
    private EditText profUserTv, profEmailTv, profPasswordTv, profFNameTv, profLNameTv, profPhoneTv, profCityTv;

    private String regexUser = "\\w+?",
            regexNumbers = "[0-9]+",
            regexLettersSpaces = "[a-zA-z\\s]*";

    private ProgressDialog progressDialog;

    private RequestQueue mQueue;
    private SessionManagement session;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_professor_fragment, container, false);

        profUserTv = (EditText) view.findViewById(R.id.prof_user);
        profEmailTv = (EditText) view.findViewById(R.id.prof_email);
        profPasswordTv = (EditText) view.findViewById(R.id.prof_password);
        profFNameTv = (EditText) view.findViewById(R.id.prof_firstname);
        profLNameTv = (EditText) view.findViewById(R.id.prof_lastname);
        profPhoneTv = (EditText) view.findViewById(R.id.prof_phone);
        profCityTv = (EditText) view.findViewById(R.id.prof_city);

        //Register event
        Button profRegisterBtn = (Button) view.findViewById(R.id.prof_signup);
        profRegisterBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                registerProfessor();
            }
        });

        mQueue =  Volley.newRequestQueue(getActivity());
        session = new SessionManagement(getActivity());
        return view;
    }

    private void registerProfessor(){
        String profUser = profUserTv.getText().toString();
        String profEmail = profEmailTv.getText().toString();
        String profPassword = profPasswordTv.getText().toString();
        String profFName = profFNameTv.getText().toString();
        String profLName = profLNameTv.getText().toString();
        String profPhone = profPhoneTv.getText().toString();
        String profCity = profCityTv.getText().toString();

        if (!profUser.matches(regexUser) || profUser.length()<5) {
            popUp(getActivity(), getString(R.string.alert_regex_title), getString(R.string.alert_regex_user_message), getString(R.string.alert_button_tryagain));
            return;
        }
        if (!validate(profEmail) || profEmail.length()<7) {
            popUp(getActivity(), getString(R.string.alert_regex_title), getString(R.string.alert_regex_email_message), getString(R.string.alert_button_tryagain));
            return;
        }
        if (!profPassword.matches(regexUser) || profPassword.length()<5) {
            popUp(getActivity(), getString(R.string.alert_regex_title), getString(R.string.alert_regex_password_message), getString(R.string.alert_button_tryagain));
            return;
        }
        if (!profFName.matches(regexLettersSpaces) || profFName.length()<3) {
            popUp(getActivity(), getString(R.string.alert_regex_title), getString(R.string.alert_regex_fname_message), getString(R.string.alert_button_tryagain));
            return;
        }
        if (!profLName.matches(regexLettersSpaces) || profLName.length()<3) {
            popUp(getActivity(), getString(R.string.alert_regex_title), getString(R.string.alert_regex_lname_message), getString(R.string.alert_button_tryagain));
            return;
        }
        if (!profPhone.matches(regexNumbers) || profPhone.length()<8) {
            popUp(getActivity(), getString(R.string.alert_regex_title), getString(R.string.alert_regex_phone_message), getString(R.string.alert_button_tryagain));
            return;
        }
        if (!profCity.matches(regexLettersSpaces) || profCity.length()<3) {
            popUp(getActivity(), getString(R.string.alert_regex_title), getString(R.string.alert_regex_city_message), getString(R.string.alert_button_tryagain));
            return;
        }

        progressDialog = showProgressDialog(getActivity(), getString(R.string.progress_register), true);
        jsonParseRegisterUser(profUser, profPassword, profEmail, profFName, profLName, profPhone, profCity, "0", "PROF");
    }

    public void jsonParseRegisterUser(final String us_user, final String us_password, final String us_email,
                                      final String us_fname, final String us_lname, final String us_phone, final String us_city,
                                      final String us_year, final String us_spec) {
        String url = session.getAFCLink() + "/afc/access/register.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                switch(response){
                    case "success: data_registered":
                        alert(getString(R.string.register_success));
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                    case "error: data_missing":
                        popUp(getActivity(), getString(R.string.alert_error_title), getString(R.string.alert_error_sent_data), getString(R.string.alert_button_tryagain));
                        break;
                    case "error: user_already":
                        popUp(getActivity(), getString(R.string.alert_error_title), getString(R.string.register_already), getString(R.string.alert_button_tryagain));
                        break;
                    default:
                        popUp(getActivity(), getString(R.string.alert_error_title), response, getString(R.string.alert_button_ok));
                        break;
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
                params.put("user_name", us_user); //parametrii POST
                params.put("user_password", us_password);
                params.put("user_email", us_email);
                params.put("user_fname", us_fname);
                params.put("user_lname", us_lname);
                params.put("user_phone", us_phone);
                params.put("user_city", us_city);
                params.put("user_year", us_year);
                params.put("user_spec", us_spec);
                params.put("user_type", "prof");
                return params;
            }
        };
        mQueue.add(request);
    }

    private void alert(String text){
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
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
}
