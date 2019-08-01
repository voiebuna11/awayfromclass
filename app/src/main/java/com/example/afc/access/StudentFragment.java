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

public class StudentFragment extends Fragment {
    private static final String TAG = "Student";
    private EditText studUserTv, studEmailTv, studPasswordTv, studFNameTv, studLNameTv, studPhoneTv, studCityTv,
            studYearTv, studSpecTv;

    private String regexUser = "\\w+?",
            regexLetters = "[a-zA-Z]+",
            regexNumbers = "[0-9]+",
            regexLettersSpaces = "[a-zA-z\\s]*";

    private ProgressDialog progressDialog;

    private RequestQueue mQueue;
    private SessionManagement session;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_student_fragment, container, false);

        studUserTv = (EditText) view.findViewById(R.id.stud_user);
        studEmailTv = (EditText) view.findViewById(R.id.stud_email);
        studPasswordTv = (EditText) view.findViewById(R.id.stud_password);
        studFNameTv = (EditText) view.findViewById(R.id.stud_firstname);
        studLNameTv = (EditText) view.findViewById(R.id.stud_lastname);
        studPhoneTv = (EditText) view.findViewById(R.id.stud_phone);
        studCityTv = (EditText) view.findViewById(R.id.stud_city);
        studYearTv = (EditText) view.findViewById(R.id.stud_year);
        studSpecTv= (EditText) view.findViewById(R.id.stud_specialization);

        //Register event
        Button studRegisterBtn = (Button) view.findViewById(R.id.stud_signup);
        studRegisterBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                registerStudent();
            }
        });

        mQueue =  Volley.newRequestQueue(getActivity());
        session = new SessionManagement(getActivity());
        return view;
    }

    private void registerStudent(){
        String studUser = studUserTv.getText().toString();
        String studEmail = studEmailTv.getText().toString();
        String studPassword = studPasswordTv.getText().toString();
        String studFName = studFNameTv.getText().toString();
        String studLName = studLNameTv.getText().toString();
        String studPhone = studPhoneTv.getText().toString();
        String studCity = studCityTv.getText().toString();
        String studYear = studYearTv.getText().toString();
        String studSpec = studSpecTv.getText().toString();

        if (!studUser.matches(regexUser) || studUser.length()<5) {
            popUp(getActivity(), getString(R.string.alert_regex_title), getString(R.string.alert_regex_user_message), getString(R.string.alert_button_tryagain));
            return;
        }
        if (!validate(studEmail) || studEmail.length()<7) {
            popUp(getActivity(), getString(R.string.alert_regex_title), getString(R.string.alert_regex_email_message), getString(R.string.alert_button_tryagain));
            return;
        }
        if (!studPassword.matches(regexUser) || studPassword.length()<5) {
            popUp(getActivity(), getString(R.string.alert_regex_title), getString(R.string.alert_regex_password_message), getString(R.string.alert_button_tryagain));
            return;
        }
        if (!studFName.matches(regexLettersSpaces) || studFName.length()<3) {
            popUp(getActivity(), getString(R.string.alert_regex_title), getString(R.string.alert_regex_fname_message), getString(R.string.alert_button_tryagain));
            return;
        }
        if (!studLName.matches(regexLettersSpaces) || studLName.length()<3) {
            popUp(getActivity(), getString(R.string.alert_regex_title), getString(R.string.alert_regex_lname_message), getString(R.string.alert_button_tryagain));
            return;
        }
        if (!studPhone.matches(regexNumbers) || studPhone.length()<8) {
            popUp(getActivity(), getString(R.string.alert_regex_title), getString(R.string.alert_regex_phone_message), getString(R.string.alert_button_tryagain));
            return;
        }
        if (!studCity.matches(regexLettersSpaces) || studCity.length()<3) {
            popUp(getActivity(), getString(R.string.alert_regex_title), getString(R.string.alert_regex_city_message), getString(R.string.alert_button_tryagain));
            return;
        }
        if (!studYear.matches(regexNumbers) || studYear.length()<1) {
            popUp(getActivity(), getString(R.string.alert_regex_title), getString(R.string.alert_regex_year_message), getString(R.string.alert_button_tryagain));
            return;
        }
        if (!studSpec.matches(regexLetters) || studSpec.length()<2) {
            popUp(getActivity(), getString(R.string.alert_regex_title), getString(R.string.alert_regex_spec_message), getString(R.string.alert_button_tryagain));
            return;
        }

        progressDialog = showProgressDialog(getActivity(), getString(R.string.progress_register), true);
        jsonParseRegisterUser(studUser, studPassword, studEmail, studFName, studLName, studPhone, studCity, studYear, studSpec);
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
                params.put("user_type", "std");
                return params;
            }
        };
        mQueue.add(request);
    }

    private void alert(String text){
        Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
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
