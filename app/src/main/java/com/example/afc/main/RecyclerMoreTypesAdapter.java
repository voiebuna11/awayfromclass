package com.example.afc.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.afc.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerMoreTypesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> feedItemList;
    private Context mContext;

    public RecyclerMoreTypesAdapter(Context context, ArrayList<Object> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {

        if (position % 3 == 0) {
            return 1;
        } else if (position % 2 == 0) {
            return 2;
        } else {
            return 3;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =null;
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_one, parent, false);
                viewHolder = new ViewHolderOne(view); break;
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_two, parent, false);
                viewHolder = new ViewHolderTwo(view); break;
            case 3:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_three, parent, false);
                viewHolder = new ViewHolderThree(view); break;
            default: break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Object dc_list = feedItemList.get(position);

        final int pos = position * 3;

        switch (holder.getItemViewType()){
            case 1:
                ViewHolderOne holderOne = (ViewHolderOne) holder;
                String text = feedItemList.get(position).toString();
                holderOne.nameForOne.setText("MERGE PENTRU 1");
                break;
            case 2:
                ViewHolderTwo holderTwo = (ViewHolderTwo) holder;
                holderTwo.imageForTwo.setBackgroundResource(R.drawable.afc_border);
                break;
            case 3:
                ViewHolderThree holderThree = (ViewHolderThree) holder;
                holderThree.nameForThree.setText("MERGE PENTRU 3");
                break;
            default: break;
        }
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0 / 0);
    }

    //****************  VIEW HOLDER 1 ******************//

    public class ViewHolderOne extends RecyclerView.ViewHolder {

        public TextView nameForOne;

        public ViewHolderOne(View itemView) {
            super(itemView);
            nameForOne = (TextView)itemView.findViewById(R.id.template_one_name);
        }
    }


    //****************  VIEW HOLDER 2 ******************//

    public class ViewHolderTwo extends RecyclerView.ViewHolder{

        public ImageView imageForTwo;

        public ViewHolderTwo(View itemView) {
            super(itemView);
            imageForTwo = (ImageView) itemView.findViewById(R.id.template_two_image);
        }
    }

    //****************  VIEW HOLDER 3 ******************//

    public class ViewHolderThree extends RecyclerView.ViewHolder{

        public TextView nameForThree;

        public ViewHolderThree(View itemView) {
            super(itemView);
            nameForThree = (TextView) itemView.findViewById(R.id.template_three_name);
        }
    }
}
