package com.example.afc.course;

import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.afc.R;
import com.example.afc.activities.BaseTogglelessActivity;
import com.example.afc.app.Config;

import java.util.HashMap;
import java.util.Map;

public class AddCourseActivity extends BaseTogglelessActivity {

    EditText mCourseName, mCourseDescription;
    String courseName, courseDescription;

    String regexName = "[a-zA-ZÀ-ž\\s]*";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(sessionData.get(Config.KEY_TYPE).equals("std")){
            finish();
        }

        setTitle(getString(R.string.course_add_title));
        mCourseName = (EditText) findViewById(R.id.course_add_name);
        mCourseDescription = (EditText) findViewById(R.id.course_add_description);

        //set first letter CAPITAL
        mCourseName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        mCourseDescription.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        //focus name on activity start
        mCourseName.requestFocus();

        stopLoadingBar();
    }

    public void createCourse(View view){
        courseName = mCourseName.getText().toString();
        courseDescription = mCourseDescription.getText().toString();

        if (!courseName.matches(regexName) || courseName.length()<5) {
            alert(getString(R.string.alert_regex_course_name_message));
            return;
        }

        RequestQueue mQueue =  Volley.newRequestQueue(getApplicationContext());
        String url = session.getAFCLink() + "/afc/courses/add_new_course.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                switch(response){
                    case "success: data_registered": alert(getString(R.string.course_add_success)); finish(); break;
                    case "error: course_already": alert(getString(R.string.course_add_already)); break;
                    case "error: missing_data": alert(getString(R.string.alert_error_sent_data)); break;
                    default: alert(response);
                    break ;
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
                params.put("user_id", sessionData.get(Config.KEY_ID)); //parametrii POST
                params.put("course_name", courseName);
                params.put("course_description", courseDescription);
                return params;
            }
        };
        mQueue.add(request);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_add_course;
    }

    @Override
    public int getToolbarResource() { return R.id.top_menu_layout; }
}
