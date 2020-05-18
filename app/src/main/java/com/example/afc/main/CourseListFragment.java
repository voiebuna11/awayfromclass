package com.example.afc.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.afc.R;
import com.example.afc.activities.BaseFragment;
import com.example.afc.app.Config;
import com.example.afc.course.Course;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CourseListFragment extends BaseFragment {
    private static final String TAG = "Continut";
    private String mSearch;
    ArrayList<Course> mCourseList;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    TextView mCourseListEmpty;

    public CourseListFragment(String search){
        this.mSearch = search;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mCourseList = new ArrayList<Course>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.search_course_list);
        mCourseListEmpty = (TextView) view.findViewById(R.id.search_list_course_empty);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerCourseSearchAdapter(getActivity(), mCourseList);
        mRecyclerView.setAdapter(mAdapter);

        jsonParseCourseList();
        return view;
    }

    private void jsonParseCourseList() {
        startLoadingBar();
        String url = session.getAFCLink() + "/afc/courses/get_search_courses.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject j = new JSONObject(response);
                    JSONArray jsonArray = j.getJSONArray("course_list");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject intermediar = jsonArray.getJSONObject(i);

                        mCourseList.add(
                                new Course(
                                        intermediar.getInt("id"),
                                        intermediar.getString("name"),
                                        intermediar.getString("enrollment"),
                                        intermediar.getString("author"),
                                        intermediar.getString("folder")
                                )
                        );
                    }
                    if(mCourseList.size() == 0) mCourseListEmpty.setVisibility(View.VISIBLE);

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
                params.put("search", mSearch); //parametrii POST
                params.put("user_id", sessionData.get(Config.KEY_ID));
                return params;
            }
        };
        mQueue.add(request);
    }

    @Override
    protected int getLayoutResource() { return R.layout.search_list_course_fragment; }
}
