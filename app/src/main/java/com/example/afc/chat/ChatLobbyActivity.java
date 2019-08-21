package com.example.afc.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.afc.R;
import com.example.afc.activities.BaseActivity;
import com.example.afc.app.Config;
import com.example.afc.app.NotificationManagement;
import com.example.afc.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatLobbyActivity extends BaseActivity {
    ArrayList<LobbyUser> mUserList;
    TextView mChatLobbyEmpty;

    RecyclerView mLobbyRecyclerView;
    RecyclerView.LayoutManager mLobbyLayoutManager;
    RecyclerView.Adapter mAdapter;

    public BroadcastReceiver mRegistrationBroadcastReceiver;
    public NotificationManagement notificationManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserList = new ArrayList<LobbyUser>();
        notificationManagement = new NotificationManagement(getApplicationContext());

        mChatLobbyEmpty = (TextView) findViewById(R.id.chat_lobby_empty);
        mLobbyRecyclerView = (RecyclerView) findViewById(R.id.chat_lobby_list);
        mLobbyRecyclerView.setHasFixedSize(true);

        setChatLobbyDetails();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.CHAT_MESSAGE)) {
                    // new push notification is received
                    jsonParseLobbyList();
                }
            }
        };
    }
    private void setChatLobbyDetails(){
        setTitle(getString(R.string.sidemenu_messages));

        mLobbyLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mLobbyRecyclerView.setLayoutManager(mLobbyLayoutManager);

        mAdapter = new RecyclerChatLobbyAdapter(getApplicationContext(), mUserList, Integer.parseInt(sessionData.get(Config.KEY_ID)));
        mLobbyRecyclerView.setAdapter(mAdapter);
    }

    private void jsonParseLobbyList() {
        String url = session.getAFCLink() + "/afc/chat/get_chat_list.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject j = new JSONObject(response);

                    JSONArray jsonArray = j.getJSONArray("user_list");
                    mUserList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject intermediar = jsonArray.getJSONObject(i);

                        mUserList.add(
                                new LobbyUser(
                                    new User(
                                        intermediar.getString("id"),
                                        intermediar.getString("type"),
                                        intermediar.getString("user"),
                                        intermediar.getString("email"),
                                        intermediar.getString("fname"),
                                        intermediar.getString("lname"),
                                        intermediar.getString("city"),
                                        intermediar.getString("phone"),
                                        intermediar.getString("year"),
                                        intermediar.getString("spec"),
                                        intermediar.getString("pic"),
                                        intermediar.getString("chat_id")
                                    ),
                                    Integer.parseInt(intermediar.getString("from")),
                                    Integer.parseInt(intermediar.getString("to")),
                                    intermediar.getString("text"),
                                    intermediar.getString("date")));
                    }

                    if(mUserList.size() == 0) {
                        mChatLobbyEmpty.setVisibility(View.VISIBLE);
                    } else {
                        mChatLobbyEmpty.setVisibility(View.GONE);
                    }

                    mAdapter.notifyDataSetChanged();
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
                params.put("user_id", sessionData.get(Config.KEY_ID)); //parametrii POST
                return params;
            }
        };
        mQueue.add(request);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_chat_lobby;
    }

    @Override
    public int getToolbarResource() {
        return R.id.top_menu_layout;
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

        jsonParseLobbyList();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}
