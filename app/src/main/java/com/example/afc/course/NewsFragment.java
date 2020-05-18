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
import com.example.afc.content.Content;
import com.example.afc.user.News;
import com.example.afc.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class NewsFragment extends BaseFragment {
    private static final String TAG = "Continut";
    private String mCourseId, mCourseFolder;
    ArrayList<News> mNewsList = new ArrayList<News>();;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    TextView mNewsListEmpty;

    public NewsFragment(String course_id, String course_folder){
        this.mCourseId = course_id;
        this.mCourseFolder = course_folder;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.course_news_list);
        mNewsListEmpty = (TextView) view.findViewById(R.id.course_list_news_empty);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerCourseNewsAdapter(getActivity(), mNewsList, mCourseFolder);
        mRecyclerView.setAdapter(mAdapter);

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

                        String jsonAuthorString = intermediar.getString("author");
                        String jsonItemString = intermediar.getString("item");
                        JSONObject author = new JSONObject(jsonAuthorString);
                        JSONObject item = new JSONObject(jsonItemString);

                        mNewsList.add(new News(
                                intermediar.getInt("event_id"),
                                intermediar.getInt("event_author_id"),
                                intermediar.getString("event_target_id"),
                                intermediar.getInt("event_context_id"),
                                intermediar.getString("event_target_type"),
                                intermediar.getString("event_context"),
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
                                        author.getString("user_chat_id"))
                        ));

                        switch (intermediar.getString("event_target_type")){
                            case "content":
                            case "file":
                                mNewsList.get(i).setContent(new Content(
                                        item.getInt("content_id"),
                                        Integer.parseInt(mCourseId),
                                        item.getString("content_name"),
                                        item.getString("content_description")
                                        ));
                                break;
                            case "user":
                                mNewsList.get(i).setTargetUser(new User(
                                        item.getString("target_user_id"),
                                        item.getString("target_user_type"),
                                        item.getString("target_user_user"),
                                        item.getString("target_user_email"),
                                        item.getString("target_user_fname"),
                                        item.getString("target_user_lname"),
                                        item.getString("target_user_city"),
                                        item.getString("target_user_phone"),
                                        item.getString("target_user_year"),
                                        item.getString("target_user_spec"),
                                        item.getString("target_user_pic"),
                                        item.getString("target_user_chat_id")
                                ));
                            default: break;

                        }
                    }
                    if(mNewsList.size() == 0)
                        mNewsListEmpty.setVisibility(View.VISIBLE);
                    else
                        mNewsListEmpty.setVisibility(View.GONE);

                    //SORT DESCENDING
                    Collections.sort(mNewsList, new Comparator<News>() {
                        @Override
                        public int compare(News n1, News n2) {
                            return n2.getId() - n1.getId(); // Descending
                        }
                    });

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

    @Override
    public void onResume() {
        super.onResume();
        jsonParseNewsList();
    }
}
