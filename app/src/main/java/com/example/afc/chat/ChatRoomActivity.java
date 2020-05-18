package com.example.afc.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.afc.R;
import com.example.afc.activities.BaseTogglelessActivity;
import com.example.afc.app.NotificationManagement;
import com.example.afc.user.ProfileActivity;
import com.example.afc.app.Config;
import com.example.afc.user.User;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatRoomActivity extends BaseTogglelessActivity {
    private ArrayList<Message> mMessageList;

    private User mChatPartner;
    private EditText mMessageBox;
    private ImageButton mSendMessage;
    private ScrollView mChatScrollView;

    RecyclerView mChatRecyclerView;
    RecyclerView.LayoutManager mChatLayoutManager;
    RecyclerView.Adapter mAdapter;

    private ImageView mChatPartnerPic;
    private TextView mChatPartnerName;

    public BroadcastReceiver mRegistrationBroadcastReceiver;
    public NotificationManagement notificationManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorBackground), PorterDuff.Mode.SRC_ATOP);

        mChatPartnerName = (TextView) findViewById(R.id.top_menu_chat_room_name);
        mChatPartnerPic = (ImageView) findViewById(R.id.top_menu_chat_room_pic);
        mChatScrollView = (ScrollView) findViewById(R.id.chat_room_chat_box);
        mMessageBox = (EditText) findViewById(R.id.chat_rom_message_box);
        mSendMessage = (ImageButton) findViewById(R.id.chat_room_send_message);
        mChatRecyclerView = (RecyclerView) findViewById(R.id.chat_room_message_list);
        mChatRecyclerView.setHasFixedSize(true);

        mMessageList = new ArrayList<Message>();
        notificationManagement = new NotificationManagement(getApplicationContext());

        //Passed info from last activity
        mChatPartner = (User) getIntent().getSerializableExtra("EXTRA_USER");

        //Get all messages saved in local db
        mMessageList = db.getSavedMessages(Integer.parseInt(sessionData.get(Config.KEY_ID)), Integer.parseInt(mChatPartner.getId()));
        setChatDetails();
    }

    private void setChatDetails() {
        //lose focus from edittext
        //mMessageBox.clearFocus();

        //set profile image
        String url = session.getAFCLink()+"/afc/assets/profile_pics/" + mChatPartner.getPic();
        String name = mChatPartner.getLname() + " " + mChatPartner.getFname();

        //Load profile image
        //Set label of activity empty for custom loading elements
        setTitle("");
        mChatPartnerName.setText(name);
        Glide.with(getApplicationContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(RequestOptions.circleCropTransform())
                .into(mChatPartnerPic);

        mChatLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mChatRecyclerView.setLayoutManager(mChatLayoutManager);

        mAdapter = new RecyclerChatRoomAdapter(getApplicationContext(), mMessageList, Integer.parseInt(sessionData.get(Config.KEY_ID)), url);
        mChatRecyclerView.setAdapter(mAdapter);

        mChatScrollView.fullScroll(mChatScrollView.FOCUS_DOWN);
        mChatScrollView.scrollTo(0, mChatScrollView.getBottom());
        scrollToBottom();
        stopLoadingBar();

        Message mLastSavedMessage = db.getLastMessage(Integer.parseInt(mChatPartner.getId()), Integer.parseInt(sessionData.get(Config.KEY_ID)));
        if(getIntent().getExtras() != null){
            if(getIntent().getExtras().containsKey("EXTRA_NEW_MESSAGES")){
                if(getIntent().getStringExtra("EXTRA_NEW_MESSAGES").equals("1")){
                    getLastMessages(mLastSavedMessage);
                }
            }
        }

        mMessageBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    scrollToBottom();
                }else{
                    scrollToBottom();
                }
            }
        });

        KeyboardVisibilityEvent.setEventListener(ChatRoomActivity.this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (isOpen && mMessageBox.hasFocus()) {
                    //scroll to last view
                    scrollToBottom();
                }
            }
        });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.CHAT_MESSAGE)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    String timestamp = intent.getStringExtra("timestamp");
                    int from = Integer.parseInt(intent.getStringExtra("from"));
                    int to = Integer.parseInt(sessionData.get(Config.KEY_ID));

                    // register into message list
                    if(from  == Integer.parseInt(mChatPartner.getId())){
                        mMessageList.add(new Message(
                                mMessageList.size()+ 1,
                                from,
                                to,
                                message,
                                timestamp
                        ));

                        db.insertMessage(from, to, message, timestamp);

                        mAdapter.notifyDataSetChanged();
                        scrollToBottom();
                    }
                }
            }
        };
    }

    private void getLastMessages(final Message lastMessage){
        startLoadingBar();
        String url = session.getAFCLink() + "/afc/chat/get_last_messages.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject j = new JSONObject(response);

                    JSONArray jsonArray = j.getJSONArray("message_list");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject intermediar = jsonArray.getJSONObject(i);

                        int from = Integer.parseInt(intermediar.getString("from"));
                        int to = Integer.parseInt(intermediar.getString("to"));
                        String message = intermediar.getString("text");
                        String timestamp = intermediar.getString("date");

                        //save to db
                        db.insertMessage(from, to, message, timestamp);

                        //insert for view
                        mMessageList.add(new Message(
                                Integer.parseInt(intermediar.getString("id")),
                                from,
                                to,
                                message,
                                timestamp
                        ));
                    }
                    mAdapter.notifyDataSetChanged();
                    scrollToBottom();
                    stopLoadingBar();
                } catch (JSONException e) {
                    e.printStackTrace();
                    alert(e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("from_id", lastMessage.getFrom()+""); //parametrii POST
                params.put("to_id", lastMessage.getTo()+"");
                params.put("text", lastMessage.getText());
                params.put("date", lastMessage.getDate());
                return params;
            }
        };
        mQueue.add(request);
    }

    public void sendMessage(View view){
        String message = mMessageBox.getText().toString();
        if(message.length()==0) return;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        String timestamp = formatter.format(ts);

        sendMessageToUser(mChatPartner.getChat_id(), message, timestamp);
        sendMessageToServer(mChatPartner.getId(), sessionData.get(Config.KEY_ID), message, timestamp);

        //clear message box
        notificationManagement.playMessageSound();
        mMessageBox.setText("");
    }

    private void sendMessageToServer(final String idTo, final String idFrom, final String text, final String date) {
        int mess_id = mMessageList.size();
        mMessageList.add(new Message(
                mess_id,
                Integer.parseInt(sessionData.get(Config.KEY_ID)),
                Integer.parseInt(mChatPartner.getId()),
                text,
                date
        ));

        mAdapter.notifyDataSetChanged();
        scrollToBottom();

        String url = session.getAFCLink() + "/afc/chat/message_register.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                switch(response){
                    case "success: data_registered":
                        //message registered to db
                        db.insertMessage(Integer.parseInt(idFrom), Integer.parseInt(idTo), text, date);
                        break;
                    case "error: data_missing":
                        alert(response); break;
                    default:
                        alert(response);break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("mess_id_to", idTo); //parametrii POST
                params.put("mess_id_from", idFrom);
                params.put("mess_date", date);
                params.put("mess_text", text);

                return params;
            }
        };
        mQueue.add(request);
    }

    private void sendMessageToUser(final String userChatId, final String text, final String timestamp) {
        String url = session.getAFCLink() + "/afc/chat/message_send.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                switch(response){
                    case "success: data_registered":
                        //alert("notificare trimisa");
                        break;
                    case "error: data_missing":
                        alert(response); break;
                    default:
                        alert(response);break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("mess_fcm_id",userChatId);
                params.put("mess_to_id",mChatPartner.getId());
                params.put("mess_from_id", sessionData.get(Config.KEY_ID));
                params.put("mess_from_name", sessionData.get(Config.KEY_LNAME) + " " +sessionData.get(Config.KEY_FNAME));
                params.put("mess_text", text);
                params.put("mess_date", timestamp);

                return params;
            }
        };
        mQueue.add(request);
    }

    private void scrollToBottom(){
        mChatRecyclerView.scrollToPosition(mMessageList.size()-1);
        mChatScrollView.scrollTo(0, mChatScrollView.getBottom());
    }

    public void onMessageBoxClick(View view){
        scrollToBottom();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_menu_chat_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.nav_chat_room_call:
                intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mChatPartner.getPhone()));
                startActivity(intent);
                return true;
            case R.id.nav_chat_room_profile:
                intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra("EXTRA_USER", (Serializable) mChatPartner);
                startActivity(intent);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_chat_room;
    }

    @Override
    public int getToolbarResource() {
        return R.id.top_menu_chat_room_layout;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.CHAT_MESSAGE));

        // clear the notification area when the app is opened
        NotificationManagement.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}
