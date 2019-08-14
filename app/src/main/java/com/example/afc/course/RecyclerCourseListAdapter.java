package com.example.afc.course;

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

class RecyclerCourseListAdapter extends RecyclerView.Adapter<RecyclerCourseListAdapter.ViewHolder> {
    private Context ctx;
    private ArrayList<Course> mList;

    public RecyclerCourseListAdapter(Context ctx, ArrayList<Course> mList){
        this.ctx = ctx;
        this.mList = mList;
    }

    @Override
    public RecyclerCourseListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_list_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerCourseListAdapter.ViewHolder holder, final int i) {
        holder.mText.setText(mList.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public AppCompatButton mText;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mText = (AppCompatButton) itemView.findViewById(R.id.course_list_btn);
        }

        @Override
        public void onClick(View view) {
            int i = getLayoutPosition();

            Intent intent = new Intent(ctx, CourseProfViewActivity.class);
            intent.putExtra("EXTRA_COURSE_ID", mList.get(i).getId() + "");
            intent.putExtra("EXTRA_COURSE_NAME", mList.get(i).getName());
            ctx.startActivity(intent);
        }
    }
    public void alert(String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
    }
}

