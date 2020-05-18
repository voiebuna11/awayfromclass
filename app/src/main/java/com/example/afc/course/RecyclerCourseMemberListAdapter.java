package com.example.afc.course;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.afc.R;
import com.example.afc.app.SessionManagement;
import com.example.afc.chat.ChatRoomActivity;
import com.example.afc.user.ProfileActivity;
import com.example.afc.user.User;

import java.io.Serializable;
import java.util.ArrayList;

public class RecyclerCourseMemberListAdapter extends RecyclerView.Adapter<RecyclerCourseMemberListAdapter.ViewHolder> {
    private Context ctx;
    private ArrayList<User> mList;
    private SessionManagement session;

    public RecyclerCourseMemberListAdapter(Context ctx, ArrayList<User> mList){
        this.ctx = ctx;
        this.mList = mList;
        this.session = new SessionManagement(ctx);
    }

    @Override
    public RecyclerCourseMemberListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_user_layout, parent, false);
        RecyclerCourseMemberListAdapter.ViewHolder vh = new RecyclerCourseMemberListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerCourseMemberListAdapter.ViewHolder holder, final int i) {
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

        //send message
        holder.mSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, ChatRoomActivity.class);
                intent.putExtra("EXTRA_USER", (Serializable) mList.get(i));
                ctx.startActivity(intent);
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
        private AppCompatImageButton mSendMessage;

        private ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mName = (TextView) itemView.findViewById(R.id.search_user_name);
            mType = (TextView) itemView.findViewById(R.id.search_user_email);
            mPic = (ImageView) itemView.findViewById(R.id.search_user_avatar);
            mSendMessage = (AppCompatImageButton) itemView.findViewById(R.id.search_user_message);
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
}


