package com.example.afc.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.afc.app.Config;
import com.example.afc.app.NotificationManagement;
import com.example.afc.course.CourseListActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

import static com.example.afc.app.NotificationManagement.getTimeSec;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationManagement notificationManagement;

    @Override
    public void onNewToken(String mToken) {
        super.onNewToken(mToken);
        Log.e("TOKEN",mToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            //Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage);
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            //Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (!NotificationManagement.isAppIsInBackground(getApplicationContext())) {
            String message = notification.getBody();
            String title = notification.getTitle();
            String timestamp = new Timestamp(System.currentTimeMillis()).toString();
            String imageUrl = notification.getImageUrl().toString();

            Log.e(TAG, imageUrl);
            // app is in foreground, broadcast the push message
            Intent resultIntent = new Intent(getApplicationContext(), CourseListActivity.class);
            resultIntent.putExtra("message", message);

            showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
            LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);

            // play notification sound
            NotificationManagement notificationManagement = new NotificationManagement(getApplicationContext());
            notificationManagement.playNotificationSound();


        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        //Log.e(TAG, "push json: " + json.toString());
        try {
            JSONObject data = json.getJSONObject("data");
            String title = data.getString("title");
            String idFrom = data.getString("from");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject extra = data.getJSONObject("extra");

            //display logs
            //Log.e(TAG, "title: " + title);
            //Log.e(TAG, "from: " + idFrom);
            //Log.e(TAG, "message: " + message);
            //Log.e(TAG, "isBackground: " + isBackground);
            //
            //Log.e(TAG, "imageUrl: " + imageUrl);
            //Log.e(TAG, "timestamp: " + timestamp);

            Log.e(TAG, "extra: " + extra.getString("type"));

            if (!NotificationManagement.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent chatMessage = new Intent(Config.CHAT_MESSAGE);
                chatMessage.putExtra("message", message);
                chatMessage.putExtra("timestamp", getTimeSec(timestamp));
                chatMessage.putExtra("from", idFrom);

                LocalBroadcastManager.getInstance(this).sendBroadcast(chatMessage);

                Intent resultIntent = new Intent(getApplicationContext(), CourseListActivity.class);
                resultIntent.putExtra("message", message);

                // play notification sound
                NotificationManagement notificationManagement = new NotificationManagement(getApplicationContext());
                notificationManagement.playMessageSound();
                showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
            } else {
                // app is in background
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationManagement = new NotificationManagement(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationManagement.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
