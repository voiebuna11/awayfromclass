package com.example.afc.chat;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.afc.R;

import java.util.ArrayList;

class RecyclerChatRoomAdapter extends RecyclerView.Adapter<RecyclerChatRoomAdapter.ViewHolder> {
    private Context ctx;
    private ArrayList<Message> mList;
    private int mUserId;
    private String urlPic;


    public RecyclerChatRoomAdapter(Context ctx, ArrayList<Message> mList, int mUserId, String urlPic){
        this.mList = mList;
        this.ctx = ctx;
        this.mUserId = mUserId;
        this.urlPic = urlPic;
    }

    @Override
    public RecyclerChatRoomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerChatRoomAdapter.ViewHolder holder, final int i) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        ViewGroup.MarginLayoutParams mBodyLayoutParams = (ViewGroup.MarginLayoutParams) holder.mBody.getLayoutParams();

        int backgroundResId = R.drawable.message_to_user_solo_layout;
        int idFrom = mList.get(i).getFrom();
        int idFromPrev = 0; //previous message id on the list
        int idFromNext = 0; //next message id on the list

        if(i > 0) idFromPrev = mList.get(i-1).getFrom();
        if(i < getItemCount()-1) idFromNext = mList.get(i+1).getFrom();

        //dp calculations
        int dp_8 = (int) (8* scale + 0.5f);
        int dp_10 = (int) (10* scale + 0.5f);
        int dp_35 = (int) (35* scale + 0.5f);

        holder.mBody.setCardElevation(0);

        //LAYOUT OPTIONS AND IMAGING
        //If message was sent by chat partner
        if(mUserId != idFrom){
            //text color
            holder.mText.setTextColor(ctx.getResources().getColor(R.color.colorBlack));

            if(i < getItemCount()-1 && i>0) {
                if (idFrom != idFromPrev && idFrom == idFromNext) {
                    mBodyLayoutParams.setMargins(0, dp_8, 0, 0);
                    backgroundResId = R.drawable.message_to_user_top_layout;
                } else if (idFrom == idFromPrev && idFrom != idFromNext) {
                    mBodyLayoutParams.setMargins(0, 0, 0, 0);
                    backgroundResId = R.drawable.message_to_user_bottom_layout;
                }  else if (idFrom == idFromPrev && idFrom == idFromNext){
                    mBodyLayoutParams.setMargins(0, 0, 0, 0);
                    backgroundResId = R.drawable.message_to_user_middle_layout;
                } else {
                    mBodyLayoutParams.setMargins(0, dp_8, 0, 0);
                    backgroundResId = R.drawable.message_to_user_solo_layout;
                }
            }
            if(i == getItemCount()-1){
                if(idFrom == idFromPrev){
                    mBodyLayoutParams.setMargins(0, 0, 0, 0);
                    backgroundResId = R.drawable.message_to_user_bottom_layout;
                } else {
                    mBodyLayoutParams.setMargins(0, dp_8, 0, 0);
                    backgroundResId = R.drawable.message_to_user_solo_layout;
                }
            }
            if(i == 0){
                if(idFrom == idFromNext){
                    mBodyLayoutParams.setMargins(0, dp_8, 0, 0);
                    backgroundResId = R.drawable.message_to_user_top_layout;
                } else {
                    mBodyLayoutParams.setMargins(0, dp_8, 0, 0);
                    backgroundResId = R.drawable.message_to_user_solo_layout;
                }
            }

            holder.mBody.requestLayout();
            holder.mText.setBackgroundDrawable(ContextCompat.getDrawable(ctx, backgroundResId));

            //side of message
            holder.mLine.setGravity(Gravity.LEFT);

            //profile image visibility
            if(i < getItemCount()-1){
                if(idFrom != mList.get(i+1).getFrom()){
                    holder.mPic.setVisibility(View.VISIBLE);
                } else {
                    holder.mPic.setVisibility(View.INVISIBLE);
                }
            } else {
                holder.mPic.setVisibility(View.VISIBLE);
            }
        //if message was sent by current user
        } else {
            //text color
            holder.mText.setTextColor(ctx.getResources().getColor(R.color.colorBackground));

            //drawable background (first message, middle message, last message)
            if(i < getItemCount()-1 && i>0) {
                if (idFrom != idFromPrev && idFrom == idFromNext) {
                    mBodyLayoutParams.setMargins(0, dp_8, 0, 0);
                    backgroundResId = R.drawable.message_from_user_top_layout;
                } else if (idFrom == idFromPrev && idFrom != idFromNext) {
                    mBodyLayoutParams.setMargins(0, 0, 0, 0);
                    backgroundResId = R.drawable.message_from_user_bottom_layout;
                }  else if (idFrom == idFromPrev && idFrom == idFromNext){
                    mBodyLayoutParams.setMargins(0, 0, 0, 0);
                    backgroundResId = R.drawable.message_from_user_middle_layout;
                } else {
                    mBodyLayoutParams.setMargins(0, dp_8, 0, 0);
                    backgroundResId = R.drawable.message_from_user_solo_layout;
                }
            }
            if(i == getItemCount()-1){
                if(idFrom == idFromPrev){
                    mBodyLayoutParams.setMargins(0, 0, 0, 0);
                    backgroundResId = R.drawable.message_from_user_bottom_layout;
                } else {
                    mBodyLayoutParams.setMargins(0, dp_8, 0, 0);
                    backgroundResId = R.drawable.message_from_user_solo_layout;
                }
            }
            if(i == 0){
                if(idFrom == idFromNext){
                    mBodyLayoutParams.setMargins(0, dp_8, 0, 0);
                    backgroundResId = R.drawable.message_from_user_top_layout;
                } else {
                    mBodyLayoutParams.setMargins(0, dp_8, 0, 0);
                    backgroundResId = R.drawable.message_from_user_solo_layout;
                }
            }

            holder.mBody.requestLayout();
            holder.mText.setBackgroundDrawable(ContextCompat.getDrawable(ctx, backgroundResId));

            //side of message
            holder.mLine.setGravity(Gravity.RIGHT);

            //profile image visibility
            holder.mPic.setVisibility(View.INVISIBLE);
        }
        //padding of layout (needs to be programatically set because of layout changing)
        holder.mText.setPadding(dp_10, dp_8, dp_10, dp_8);

        //text message
        holder.mText.setText(mList.get(i).getText());

        //set profile image(for chat partner)
        Glide.with(ctx)
                .load(urlPic)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.mPic);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private AppCompatTextView mText;
        private CardView mBody;
        private RelativeLayout mLine;
        private ImageView mPic;

        private ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mText = (AppCompatTextView) itemView.findViewById(R.id.message_layout_text);
            mBody = (CardView) itemView.findViewById(R.id.message_layout_body);
            mLine = (RelativeLayout) itemView.findViewById(R.id.message_layout_line);
            mPic = (ImageView) itemView.findViewById(R.id.message_layout_user_pic);
        }

        @Override
        public void onClick(View view) {
            int i = getLayoutPosition();
        }

    }
}
