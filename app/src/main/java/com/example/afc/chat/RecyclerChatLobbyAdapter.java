package com.example.afc.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.afc.R;
import com.example.afc.app.DBManagement;
import com.example.afc.app.SessionManagement;
import com.example.afc.app.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

class RecyclerChatLobbyAdapter extends RecyclerView.Adapter<RecyclerChatLobbyAdapter.ViewHolder> {
    private Context ctx;
    private SessionManagement session;
    private HashMap<String, String> sessionData;
    private ArrayList<LobbyUser> mList;
    private int mUserId;
    private int newMessages=1;
    private DBManagement db;

    public RecyclerChatLobbyAdapter(Context ctx, ArrayList<LobbyUser> mList, int mUserId){
        this.ctx = ctx;
        this.mList = mList;
        this.mUserId = mUserId;
        this.session = new SessionManagement(ctx);
        this.db = new DBManagement(ctx);
        sessionData = this.session.getUserDetails();
    }

    @Override
    public RecyclerChatLobbyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lobby_user_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerChatLobbyAdapter.ViewHolder holder, final int i) {
        User mPartner = mList.get(i).getUser();
        Message mLastSavedMessage = db.getLastMessage(mList.get(i).getFrom(), mList.get(i).getTo());
        boolean isLastFromMe = mUserId==mList.get(i).getFrom();

        //time
        String last_hour = mList.get(i).getDate().split(" ")[1];
        last_hour = last_hour.substring(0, last_hour.length() - 3);

        //lname and fname
        String name = mPartner.getLname() + " " + mPartner.getFname();
        String url = session.getAFCLink()+"/afc/assets/profile_pics/"+mPartner.getPic();

        //last message
        String last_message = "";
        if(isLastFromMe) last_message += "You: ";
        last_message += mList.get(i).getText();

        holder.mLastMessage.setText(last_message);
        holder.mLastDate.setText(last_hour);
        holder.mPartnerName.setText(name);

        //Load profile image
        Glide.with(ctx)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.mPartnerImage);

        //check if last local saved message is the same with last online saved message
        if(mLastSavedMessage.getFrom() == mList.get(i).getFrom() && mLastSavedMessage.getTo() == mList.get(i).getTo() && mLastSavedMessage.getText().equals(mList.get(i).getText()) && mLastSavedMessage.getDate().equals(mList.get(i).getDate())){
            holder.mNewMessage.setVisibility(View.GONE);
            newMessages=0;
        } else {
            holder.mNewMessage.setVisibility(View.VISIBLE);
        }

        holder.mBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mNewMessage.setVisibility(View.GONE);

                Intent intent = new Intent(ctx, ChatRoomActivity.class);
                intent.putExtra("EXTRA_USER", (Serializable) mList.get(i).getUser());
                intent.putExtra("EXTRA_NEW_MESSAGES", newMessages+"");
                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mLastMessage;
        public TextView mLastDate;
        public ImageView mPartnerImage;
        public TextView mPartnerName;
        public TextView mNewMessage;
        public CardView mBody;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mPartnerImage = (ImageView) itemView.findViewById(R.id.lobby_user_avatar);
            mPartnerName = (TextView) itemView.findViewById(R.id.lobby_user_name);
            mLastMessage = (TextView) itemView.findViewById(R.id.lobby_user_last_message);
            mLastDate = (TextView) itemView.findViewById(R.id.lobby_user_last_date);
            mNewMessage = (TextView) itemView.findViewById(R.id.lobby_user_dot);
            mBody = (CardView) itemView.findViewById(R.id.lobby_layout_body);
        }

        @Override
        public void onClick(View view) {
            int i = getLayoutPosition();
            Intent intent = new Intent(ctx, ChatRoomActivity.class);
            intent.putExtra("EXTRA_USER", (Serializable) mList.get(i).getUser());
            intent.putExtra("EXTRA_NEW_MESSAGES", newMessages+"");
            ctx.startActivity(intent);
        }
    }
    public void alert(String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
    }
}
