package com.example.afc.content;

import android.os.Bundle;
import android.text.InputType;
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

public class AddContentActivity extends BaseTogglelessActivity {

    EditText mContentName, mContentDescription;
    String contentName, contentDescription, courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(sessionData.get(Config.KEY_TYPE).equals("std")){
            finish();
        }


        setTitle(getString(R.string.content_add_title));
        mContentName = (EditText) findViewById(R.id.content_add_name);
        mContentDescription = (EditText) findViewById(R.id.content_add_description);
        courseId = getIntent().getStringExtra("EXTRA_COURSE_ID");

        //set first letter CAPITAL
        mContentName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        mContentDescription.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        //focus name on activity start
        mContentName.requestFocus();

        stopLoadingBar();
    }

    public void createContent(View view){
        contentName = mContentName.getText().toString();
        contentDescription = mContentDescription.getText().toString();

        RequestQueue mQueue =  Volley.newRequestQueue(getApplicationContext());
        String url = session.getAFCLink() + "/afc/courses/add_new_content.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                switch(response){
                    case "success: data_registered": alert(getString(R.string.content_add_success)); finish(); break;
                    case "error: content_already": alert(getString(R.string.content_add_already)); break;
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
                params.put("content_name", contentName);
                params.put("content_description", contentDescription);
                params.put("course_id", courseId);
                return params;
            }
        };
        mQueue.add(request);

    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_add_content;
    }

    @Override
    public int getToolbarResource() { return R.id.top_menu_layout; }
}
