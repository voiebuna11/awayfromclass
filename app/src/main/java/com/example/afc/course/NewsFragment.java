package com.example.afc.course;

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
import com.example.afc.user.News;
import com.example.afc.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewsFragment extends BaseFragment {
    private static final String TAG = "Continut";
    private String mCourseId;
    ArrayList<News> mNewsList = new ArrayList<News>();;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    TextView mNewsListEmpty;

    public NewsFragment(String course_id){
        this.mCourseId = course_id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.course_news_list);
        mNewsListEmpty = (TextView) view.findViewById(R.id.course_list_news_empty);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerCourseNewsAdapter(getActivity(), mNewsList);
        mRecyclerView.setAdapter(mAdapter);
        jsonParseNewsList();
        return view;
    }

    private void jsonParseNewsList() {
        mNewsList.clear();
        String url = session.getAFCLink() + "/afc/courses/get_course_news.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject j = new JSONObject(response);
                    JSONArray jsonArray = j.getJSONArray("news_list");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject intermediar = jsonArray.getJSONObject(i);

                        String jsonString = intermediar.getString("author");
                        JSONObject author = new JSONObject(jsonString);

                        mNewsList.add(new News(
                                intermediar.getInt("event_id"),
                                intermediar.getInt("event_author_id"),
                                intermediar.getInt("event_target_id"),
                                intermediar.getString("event_target_type"),
                                intermediar.getString("event_type"),
                                intermediar.getString("event_action"),
                                intermediar.getString("event_date"),
                                new User(
                                        author.getString("user_id"),
                                        author.getString("user_type"),
                                        author.getString("user_user"),
                                        author.getString("user_email"),
                                        author.getString("user_fname"),
                                        author.getString("user_lname"),
                                        author.getString("user_city"),
                                        author.getString("user_phone"),
                                        author.getString("user_year"),
                                        author.getString("user_spec"),
                                        author.getString("user_pic"),
                                        author.getString("user_chat_id")
                                )
                        ));
                    }
                    if(mNewsList.size() == 0) mNewsListEmpty.setVisibility(View.VISIBLE);

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
                params.put("course_id", mCourseId); //parametrii POST
                return params;
            }
        };
        mQueue.add(request);
    }

    @Override
    protected int getLayoutResource() { return R.layout.course_news_fragment; }

}
