package com.example.afc.main;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.afc.R;
import com.example.afc.app.Config;
import com.example.afc.app.SessionManagement;
import com.example.afc.course.Course;
import com.example.afc.course.CourseViewActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


class RecyclerCourseSearchAdapter extends RecyclerView.Adapter<RecyclerCourseSearchAdapter.ViewHolder> {
    private Context ctx;
    private ArrayList<Course> mList;
    private SessionManagement session;
    private HashMap<String, String> sessionData;


    public RecyclerCourseSearchAdapter(Context ctx, ArrayList<Course> mList){
        this.ctx = ctx;
        this.mList = mList;
        this.session = new SessionManagement(ctx);
        this.sessionData = session.getUserDetails();
    }

    @Override
    public RecyclerCourseSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_course_layout, parent, false);
        RecyclerCourseSearchAdapter.ViewHolder vh = new RecyclerCourseSearchAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerCourseSearchAdapter.ViewHolder holder, final int i) {
        holder.mName.setText(mList.get(i).getName());
        holder.mAuthor.setText("@"+mList.get(i).getAuthor());

        if(!mList.get(i).getEnroll().equals("null") || mList.get(i).getAuthor().equals(sessionData.get(Config.KEY_LNAME) + " " + sessionData.get(Config.KEY_FNAME))){
            holder.mEnroll.setImageResource(R.drawable.ic_course_enroll_confirmed);
        } else {
            holder.mEnroll.setImageResource(R.drawable.ic_course_enroll_add);

            holder.mEnroll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jsonEnrollToCourse(mList.get(i).getId()+"");
                    holder.mEnroll.setImageResource(R.drawable.ic_course_enroll_confirmed);
                }
            });
        }
}

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mName;
        public TextView mAuthor;
        public AppCompatImageButton mEnroll;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mName = (TextView) itemView.findViewById(R.id.search_course_name);
            mAuthor = (TextView) itemView.findViewById(R.id.search_course_author);
            mEnroll = (AppCompatImageButton) itemView.findViewById(R.id.search_course_enroll);
        }

        @Override
        public void onClick(View view) {
            int i = getLayoutPosition();

            Intent intent = new Intent(ctx, CourseViewActivity.class);
            intent.putExtra("EXTRA_COURSE_ID", mList.get(i).getId() + "");
            intent.putExtra("EXTRA_COURSE_NAME", mList.get(i).getName());
            intent.putExtra("EXTRA_COURSE_AUTHOR", mList.get(i).getAuthor());
            intent.putExtra("EXTRA_COURSE_FOLDER", mList.get(i).getFolder());
            ctx.startActivity(intent);
        }
    }
    public void alert(String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
    }

    public void jsonEnrollToCourse(final String course_id) {
        RequestQueue mQueue =  Volley.newRequestQueue(ctx);
        String url = session.getAFCLink() + "/afc/courses/add_course_enroll.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("success: data_registered")){
                    alert(ctx.getResources().getString(R.string.course_enrollment_request));
                } else {
                    alert(response);
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
                params.put("course_id", course_id); //parametrii POST
                params.put("user_id", sessionData.get(Config.KEY_ID));
                return params;
            }
        };
        mQueue.add(request);
    }
}


