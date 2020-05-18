package com.example.afc.content;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afc.R;

import java.util.ArrayList;

public class RecyclerContentListAdapter extends RecyclerView.Adapter<RecyclerContentListAdapter.ViewHolder> {
    private Context ctx;
    private ArrayList<Content> mList;
    private String mCourseId, mCourseFolder;

    public RecyclerContentListAdapter(Context ctx, ArrayList<Content> mList, String mCourseId, String mCourseFolder){
        this.ctx = ctx;
        this.mList = mList;
        this.mCourseId = mCourseId;
        this.mCourseFolder = mCourseFolder;
    }

    @Override
    public RecyclerContentListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_content_layout, parent, false);
        RecyclerContentListAdapter.ViewHolder vh = new RecyclerContentListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerContentListAdapter.ViewHolder holder, final int i) {
        String name = mList.get(i).getName();
        holder.mName.setText(name);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private AppCompatButton mName;

        private ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mName = (AppCompatButton) itemView.findViewById(R.id.content_name);
        }

        @Override
        public void onClick(View view) {
            int i = getLayoutPosition();

            Intent intent = new Intent(ctx, ContentViewActivity.class);
            intent.putExtra("EXTRA_CONTENT_ID", mList.get(i).getId() + "");
            intent.putExtra("EXTRA_CONTENT_NAME", mList.get(i).getName());
            intent.putExtra("EXTRA_CONTENT_FOLDER", mCourseFolder);
            intent.putExtra("EXTRA_CONTENT_DESCRIPTION", mList.get(i).getDescription());
            ctx.startActivity(intent);
        }
    }
    public void alert(String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
    }
}


