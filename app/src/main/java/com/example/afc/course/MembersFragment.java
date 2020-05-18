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
import com.example.afc.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MembersFragment extends BaseFragment {
    private static final String TAG = "Continut";
    private String mCourseId;
    ArrayList<User> mUserList = new ArrayList<User>();;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    TextView mUserListEmpty;

    public MembersFragment(String course_id){
        this.mCourseId = course_id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.course_user_list);
        mUserListEmpty = (TextView) view.findViewById(R.id.course_list_user_empty);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerCourseMemberListAdapter(getActivity(), mUserList);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    private void jsonParseUserList() {
        mUserList.clear();
        String url = session.getAFCLink() + "/afc/courses/get_course_members.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject j = new JSONObject(response);
                    JSONArray jsonArray = j.getJSONArray("user_list");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject intermediar = jsonArray.getJSONObject(i);

                        mUserList.add(new User(
                                intermediar.getString("id"),
                                intermediar.getString("type"),
                                intermediar.getString("user"),
                                intermediar.getString("email"),
                                intermediar.getString("fname"),
                                intermediar.getString("lname"),
                                intermediar.getString("city"),
                                intermediar.getString("phone"),
                                intermediar.getString("year"),
                                intermediar.getString("spec"),
                                intermediar.getString("pic"),
                                intermediar.getString("chat_id")
                        ));
                    }
                    if(mUserList.size() == 0) mUserListEmpty.setVisibility(View.VISIBLE);

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
    protected int getLayoutResource() { return R.layout.course_members_fragment; }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            jsonParseUserList();
        }
    }
}
