package com.example.afc.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.afc.R;
import com.example.afc.user.ProfileActivity;
import com.example.afc.app.SessionManagement;
import com.example.afc.user.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

class RecyclerLastUserListAdapter extends RecyclerView.Adapter<RecyclerLastUserListAdapter.ViewHolder> {
    private Context ctx;
    private ArrayList<User> mList;
    private SessionManagement session;
    private Activity activity;
    private HashMap<String, String> sessionData;

    public RecyclerLastUserListAdapter(Context ctx, ArrayList<User> mList, Activity activity){
        this.ctx = ctx;
        this.mList = mList;
        this.activity = activity;
        this.session = new SessionManagement(ctx);
        sessionData = session.getUserDetails();
    }

    @Override
    public RecyclerLastUserListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.last_users_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerLastUserListAdapter.ViewHolder holder, final int i) {
        holder.mBody.setCardElevation(0);
        //set name
        holder.mTextName.setText(mList.get(i).getLname() + " " + mList.get(i). getFname());
        String url = session.getAFCLink()+"/afc/assets/profile_pics/"+mList.get(i).getPic();

        //set profile image
        Glide.with(ctx)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.mProfilePic);

        holder.mBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ProfileActivity.class);
                intent.putExtra("EXTRA_USER", (Serializable) mList.get(i));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // Apply activity transition
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(activity, (View) holder.mProfilePic, "profile_pic");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    activity.startActivity(intent, options.toBundle());
                    //activity.finish();

                } else {
                    ctx.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private AppCompatTextView mTextName;
        private AppCompatImageView mProfilePic;
        private CardView mBody;

        private ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTextName = (AppCompatTextView) itemView.findViewById(R.id.last_user_name);
            mProfilePic = (AppCompatImageView) itemView.findViewById(R.id.last_user_avatar);
            mBody = (CardView) itemView.findViewById(R.id.last_layout_body);
        }

        @Override
        public void onClick(View view) {
            int i = getLayoutPosition();
        }
    }
    public void alert(String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
    }
}

