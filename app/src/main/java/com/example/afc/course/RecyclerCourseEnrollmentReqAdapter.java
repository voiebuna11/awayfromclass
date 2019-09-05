package com.example.afc.course;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.afc.R;
import com.example.afc.app.Config;
import com.example.afc.app.SessionManagement;
import com.example.afc.user.ProfileActivity;
import com.example.afc.user.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecyclerCourseEnrollmentReqAdapter extends RecyclerView.Adapter<RecyclerCourseEnrollmentReqAdapter.ViewHolder> {
    private Context ctx;
    private ArrayList<User> mList;
    private String mCourseId;
    private SessionManagement session;
    private HashMap<String, String> sessionData;

    public RecyclerCourseEnrollmentReqAdapter(Context ctx, ArrayList<User> mList, String mCourseId){
        this.ctx = ctx;
        this.mList = mList;
        this.mCourseId = mCourseId;
        this.session = new SessionManagement(ctx);
        this.sessionData = session.getUserDetails();
    }

    @Override
    public RecyclerCourseEnrollmentReqAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_enrollment_layout, parent, false);
        RecyclerCourseEnrollmentReqAdapter.ViewHolder vh = new RecyclerCourseEnrollmentReqAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerCourseEnrollmentReqAdapter.ViewHolder holder, final int i) {
        String url = session.getAFCLink() + "/afc/assets/profile_pics/" + mList.get(i).getPic();
        String name = mList.get(i).getLname() + " " + mList.get(i).getFname();

        if(mList.get(i).getType().equals("std"))
            holder.mType.setText(ctx.getResources().getString(R.string.user_std));
        else
            holder.mType.setText(ctx.getResources().getString(R.string.user_prof));

        holder.mName.setText(name);

        //set profile image
        Glide.with(ctx)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.mPic);

        //accept enroll request
        holder.mAcceptReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonSetEnrollmentRequest(mCourseId, i, "1");
            }
        });

        //deny enroll request
        holder.mDenyReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonSetEnrollmentRequest(mCourseId, i, "0");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mName;
        private TextView mType;
        private ImageView mPic;
        private TextView mAcceptReq;
        private TextView mDenyReq;

        private ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mName = (TextView) itemView.findViewById(R.id.enrollment_user_name);
            mType = (TextView) itemView.findViewById(R.id.enrollment_user_email);
            mPic = (ImageView) itemView.findViewById(R.id.enrollment_user_avatar);
            mAcceptReq = (TextView) itemView.findViewById(R.id.enroll_req_accept);
            mDenyReq = (TextView) itemView.findViewById(R.id.enroll_req_deny);
        }

        @Override
        public void onClick(View view) {
            int i = getLayoutPosition();

            Intent intent = new Intent(ctx, ProfileActivity.class);
            intent.putExtra("EXTRA_USER", (Serializable) mList.get(i));
            ctx.startActivity(intent);
        }
    }
    public void alert(String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
    }

    public void jsonSetEnrollmentRequest(final String course_id, final int position, final String status) {
        RequestQueue mQueue =  Volley.newRequestQueue(ctx);
        String url = session.getAFCLink() + "/afc/courses/set_course_enroll.php";
        final StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                switch (response){
                    case "success: req_accepted": alert(ctx.getResources().getString(R.string.course_enrollment_accepted_req)); break;
                    case "success: req_denied": alert(ctx.getResources().getString(R.string.course_enrollment_denied_req)); break;
                    default: alert(response); break;
                }

                mList.remove(position);
                notifyDataSetChanged();
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
                params.put("course_id", course_id); //parametrii POST
                params.put("status", status);
                params.put("user_id", mList.get(position).getId());
                params.put("author_id", sessionData.get(Config.KEY_ID));
                return params;
            }
        };
        mQueue.add(request);
    }
}


