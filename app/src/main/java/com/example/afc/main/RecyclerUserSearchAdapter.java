package com.example.afc.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.afc.R;
import com.example.afc.user.User;

import java.util.ArrayList;

class RecyclerUserSearchAdapter extends RecyclerView.Adapter<RecyclerUserSearchAdapter.ViewHolder> {
    private Context ctx;
    private ArrayList<User> mList;

    public RecyclerUserSearchAdapter(Context ctx, ArrayList<User> mList){
        this.ctx = ctx;
        this.mList = mList;
    }

    @Override
    public RecyclerUserSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_user_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerUserSearchAdapter.ViewHolder holder, final int i) {
        holder.mName.setText(mList.get(i).getFname());
        holder.mEmail.setText(mList.get(i).getEmail());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mName;
        public TextView mEmail;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mName = (TextView) itemView.findViewById(R.id.search_user_name);
            mEmail = (TextView) itemView.findViewById(R.id.search_user_email);
        }

        @Override
        public void onClick(View view) {
            int i = getLayoutPosition();

            alert(mList.get(i).getId());
        }
    }
    public void alert(String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
    }
}

