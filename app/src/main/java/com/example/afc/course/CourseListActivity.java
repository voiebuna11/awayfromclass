package com.example.afc.course;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.afc.R;
import com.example.afc.activities.BaseActivity;
import com.example.afc.app.Config;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CourseListActivity extends BaseActivity {
    FloatingActionButton addCourseBtn;

    ArrayList<Course> mCourseList;

    RecyclerView mCoursesRecyclerView;
    RecyclerView.LayoutManager mCoursesLayoutManager;
    RecyclerView.Adapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.sidemenu_courses));
        mCourseList = new ArrayList<Course>();

        addCourseBtn = findViewById(R.id.add_new_course);

        if(sessionData.get(Config.KEY_TYPE).equals("std")) addCourseBtn.hide();
        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddCourseActivity.class);
                startActivity(intent);
            }
        });

        mCoursesRecyclerView = (RecyclerView) findViewById(R.id.course_list);
        mCoursesRecyclerView.setHasFixedSize(true);

        mCoursesLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mCoursesRecyclerView.setLayoutManager(mCoursesLayoutManager);

        mAdapter = new RecyclerCourseListAdapter(getApplicationContext(), mCourseList);
        mCoursesRecyclerView.setAdapter(mAdapter);

        jsonParseCourseList();
    }

    private void jsonParseCourseList() {
        startLoadingBar();
        String url = "";

        if(sessionData.get(Config.KEY_TYPE).equals("std")){
            url = session.getAFCLink() + "/afc/courses/get_subscribed_courses.php";
        } else {
            url = session.getAFCLink() + "/afc/courses/get_created_courses.php";
        }
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject j = new JSONObject(response);
                    JSONArray jsonArray = j.getJSONArray("course_list");
                    mCourseList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject intermediar = jsonArray.getJSONObject(i);

                        mCourseList.add(
                                new Course(
                                        intermediar.getInt("id"),
                                        intermediar.getString("name"),
                                        intermediar.getString("enrollment"),
                                        intermediar.getString("author_id"),
                                        intermediar.getString("folder")
                                )
                        );
                    }
                    mAdapter.notifyDataSetChanged();
                    stopLoadingBar();
                } catch (JSONException e) {
                    e.printStackTrace();
                    alert(e.toString());
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
                return params;
            }
        };
        mQueue.add(request);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_course_list;
    }

    @Override
    public int getToolbarResource() {
        return R.id.top_menu_layout;
    }

    @Override
    protected void onResume() {
        super.onResume();
        jsonParseCourseList();
    }
}
