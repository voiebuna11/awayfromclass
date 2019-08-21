package com.example.afc.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afc.R;

import java.util.ArrayList;

class RecyclerFileListAdapter extends RecyclerView.Adapter<RecyclerFileListAdapter.ViewHolder> {
    private Context ctx;
    private ArrayList<MyFile> mList;

    public RecyclerFileListAdapter(Context ctx, ArrayList<MyFile> mList){
        this.ctx = ctx;
        this.mList = mList;
    }

    @Override
    public RecyclerFileListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerFileListAdapter.ViewHolder holder, final int i) {
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
        }
    }
    public void alert(String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
    }
}

