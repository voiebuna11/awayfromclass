package com.example.afc.main;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afc.R;

import java.util.ArrayList;

public class RecyclerSuggestionListAdapter extends RecyclerView.Adapter<RecyclerSuggestionListAdapter.ViewHolder> {
    private Context ctx;
    private ArrayList<String> mList;

    public RecyclerSuggestionListAdapter(Context ctx, ArrayList<String> mList){
        this.ctx = ctx;
        this.mList = mList;
    }

    @Override
    public RecyclerSuggestionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_suggestion_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerSuggestionListAdapter.ViewHolder holder, final int i) {
        holder.mText.setText(mList.get(i));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public AppCompatTextView mText;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mText = (AppCompatTextView) itemView.findViewById(R.id.suggestion_text);
            mText.setTextAppearance(ctx, R.style.Widget_AppTheme_BorderlessButton);
        }

        @Override
        public void onClick(View view) {
            int i = getLayoutPosition();
            InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            Intent intent = new Intent(ctx, SearchListActivity.class);
            intent.putExtra("EXTRA_SEARCH", mList.get(i));
            ctx.startActivity(intent);
        }
    }
    public void alert(String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
    }
}

