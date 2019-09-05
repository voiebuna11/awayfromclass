package com.example.afc.content;

import android.content.Intent;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContentsFragment extends BaseFragment {
    private static final String TAG = "Continut";
    private String mCourseId, mCourseFolder;
    ArrayList<Content> mContentList = new ArrayList<Content>();;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    TextView mContentListEmpty;

    public ContentsFragment(String course_id, String course_folder){
        this.mCourseId = course_id;
        this.mCourseFolder = course_folder;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.course_content_list);
        mContentListEmpty = (TextView) view.findViewById(R.id.course_list_content_empty);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerContentListAdapter(getActivity(), mContentList, mCourseId, mCourseFolder);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton addContentBtn = (FloatingActionButton) view.findViewById(R.id.add_new_content);
        addContentBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddContentActivity.class);
                intent.putExtra("EXTRA_COURSE_ID", mCourseId);
                startActivity(intent);
            }
        });

        return view;
    }

    private void jsonParseContentList() {
        mContentList.clear();
        String url = session.getAFCLink() + "/afc/courses/get_content_list.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject j = new JSONObject(response);
                    JSONArray jsonArray = j.getJSONArray("content_list");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject intermediar = jsonArray.getJSONObject(i);

                        mContentList.add(new Content(
                                Integer.parseInt(intermediar.getString("id")),
                                Integer.parseInt(intermediar.getString("course_id")),
                                intermediar.getString("name"),
                                intermediar.getString("description")
                        ));
                    }
                    if(mContentList.size() == 0)
                        mContentListEmpty.setVisibility(View.VISIBLE);
                    else
                        mContentListEmpty.setVisibility(View.GONE);

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
    protected int getLayoutResource() { return R.layout.course_contents_fragment; }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            jsonParseContentList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        jsonParseContentList();
    }
}
