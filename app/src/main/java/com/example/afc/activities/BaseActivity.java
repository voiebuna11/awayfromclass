package com.example.afc.activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.afc.R;
import com.example.afc.app.Config;
import com.example.afc.app.DBManagement;
import com.example.afc.app.SessionManagement;
import com.example.afc.chat.ChatLobbyActivity;
import com.example.afc.course.CourseListActivity;
import com.example.afc.main.MainActivity;
import com.example.afc.user.FilesActivity;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/* This is a Base Activity. Includes features used by multiple activies across the APP */

public abstract class BaseActivity extends AppCompatActivity {
    public SessionManagement session;
    public HashMap<String, String> sessionData;
    public RequestQueue mQueue;

    public Toolbar toolbar;
    public ActionBarDrawerToggle toggleBtn;
    public DrawerLayout drawerLayout;
    public NavigationView nv;

    public RelativeLayout progressCircle;
    public DBManagement db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = (Activity) this;

        //check session(if user is logged in)
        session = new SessionManagement(getApplicationContext());
        session.checkLogin();
        sessionData = session.getUserDetails();

        //setup activity layout elements
        setContentView(getLayoutResource());
        configureToolbar();
        configureSideMenu(activity);
        configureSideMenuHeader();

        startLoadingBar();

        db = new DBManagement(getApplicationContext());
        mQueue =  Volley.newRequestQueue(getApplicationContext());
    }
    protected abstract int getLayoutResource();

    protected abstract int getToolbarResource();

    //Top menu intialization
    protected void configureToolbar() {
        toolbar = (Toolbar) findViewById(getToolbarResource());
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setPadding(0,0,0,0);
        toolbar.setContentInsetsAbsolute(0,0);
    }

    //Side menu initialization
    private void configureSideMenu(final Activity activity){
        nv = (NavigationView) findViewById(R.id.side_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        toggleBtn = new ActionBarDrawerToggle(this, drawerLayout, R.string.sidemenu_open, R.string.sidemenu_close);
        drawerLayout.addDrawerListener(toggleBtn);
        toggleBtn.syncState();

        //setare butoane meniu lateral
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Intent intent = null;

                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case(R.id.nav_home): intent = new Intent(getApplicationContext(), MainActivity.class);
                        if(activity instanceof MainActivity) return true; break;
                    case(R.id.nav_messages): intent = new Intent(getApplicationContext(), ChatLobbyActivity.class);
                        if(activity instanceof ChatLobbyActivity) return true; break;
                    case(R.id.nav_courses): intent = new Intent(getApplicationContext(), CourseListActivity.class);
                        if(activity instanceof CourseListActivity) return true; break;
                    case(R.id.nav_my_files): intent = new Intent(getApplicationContext(), FilesActivity.class);
                        if(activity instanceof FilesActivity) return true; break;
                    case(R.id.nav_notifications): intent = new Intent(getApplicationContext(), EmptyActivity.class);
                        if(activity instanceof EmptyActivity) return true; break;
                    case(R.id.nav_settings): intent = new Intent(getApplicationContext(), EmptyActivity.class);
                        if(activity instanceof EmptyActivity) return true; break;
                    case(R.id.nav_logout): session.logoutUser(); return true;
                    default:
                        break;
                }
                startActivity(intent);
                return true;
            }
        });
    }

    public void configureSideMenuHeader(){
        final Context context = getApplicationContext();
        View header = nv.getHeaderView(0);

        final ImageView imageView = (ImageView) header.findViewById(R.id.menu_header_avatar);
        TextView mName = (TextView) header.findViewById(R.id.menu_header_name);
        TextView mEmail = (TextView) header.findViewById(R.id.menu_header_email);

        String url = session.getAFCLink()+"/afc/assets/profile_pics/"+sessionData.get("us_pic");
        String name = sessionData.get(Config.KEY_LNAME) + " " + sessionData.get(Config.KEY_FNAME);

        mName.setText(name);
        mEmail.setText(sessionData.get(Config.KEY_EMAIL));

        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);
    }

    public void startLoadingBar(){
        progressCircle = (RelativeLayout) findViewById(R.id.progress_rl);
        progressCircle.setVisibility(View.VISIBLE);
    }

    public void stopLoadingBar(){
        progressCircle.setVisibility(View.GONE);
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    public void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.PREF_NAME, 0);
        String regId = pref.getString(Config.KEY_CHAT_ID, null);

        if (!TextUtils.isEmpty(regId))
            alert(regId);
        else
            alert("Firebase Reg Id is not received yet!");
    }

    public void alert(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    public void popUp(final Context context, String title, String message, String button) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setPositiveButton(button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public int getRandom(int from, int to){
        Random r = new Random();
        return r.nextInt(to-from) + from;
    }

    public void copyToClipboard(String text, String label){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        alert(getString(R.string.file_list_copy_to_clipboard));
    }

    //get user files from server
    public void deleteFileFromServer(final String fileName,final RecyclerView.Adapter adapter) {
        String url = session.getAFCLink() + "/afc/users/remove_user_file.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("success: file_removed")){
                    alert(getString(R.string.file_list_removed));
                } else {
                    alert(response);
                }
                adapter.notifyDataSetChanged();
                stopLoadingBar();
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
                params.put("file_name", fileName);
                return params;
            }
        };
        mQueue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggleBtn.onOptionsItemSelected(item)){
            return true;
        }

        switch (item.getItemId()) {
            //case R.id.nav_mail: stopLoadingBar(); return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
