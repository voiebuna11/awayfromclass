package com.example.afc.course;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.afc.R;
import com.example.afc.app.SessionManagement;
import com.example.afc.user.News;
import com.example.afc.user.User;

import java.util.ArrayList;

public class RecyclerCourseNewsAdapter extends RecyclerView.Adapter<RecyclerCourseNewsAdapter.ViewHolder> {
    private Context ctx;
    private ArrayList<News> mList;
    private SessionManagement session;

    public RecyclerCourseNewsAdapter(Context ctx, ArrayList<News> mList){
        this.ctx = ctx;
        this.mList = mList;
        this.session = new SessionManagement(ctx);
    }

    @Override
    public RecyclerCourseNewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_user_layout, parent, false);
        RecyclerCourseNewsAdapter.ViewHolder vh = new RecyclerCourseNewsAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerCourseNewsAdapter.ViewHolder holder, final int i) {
        User mUser = mList.get(i).getAuthor();

        String url = session.getAFCLink() + "/afc/assets/profile_pics/" + mUser.getPic();
        String name = mUser.getLname()+ " " + mUser.getFname();

        holder.mContent.setText(name);


        //set profile image
        Glide.with(ctx)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(holder.mPic);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mContent;
        private TextView mDate;
        private ImageView mPic;

        private ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mContent = (TextView) itemView.findViewById(R.id.news_content);
            mDate = (TextView) itemView.findViewById(R.id.news_date);
            mPic = (ImageView) itemView.findViewById(R.id.news_user_avatar);
        }

        @Override
        public void onClick(View view) {
            int i = getLayoutPosition();
            alert(mList.get(i).getTarget_type() + " = " + mList.get(i).getTarget_id());
        }
    }
    public void alert(String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
    }
}


