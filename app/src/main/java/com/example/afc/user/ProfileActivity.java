package com.example.afc.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.afc.R;
import com.example.afc.activities.BaseTogglelessActivity;
import com.example.afc.app.User;
import com.example.afc.chat.ChatRoomActivity;

import java.io.Serializable;

public class ProfileActivity extends BaseTogglelessActivity {
    private AppCompatButton userAccPrf, userEmailPrf, userPhonePrf, userCityPrf, userGroupPrf;
    private TextView userNamePrf;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startLoadingBar();

        userAccPrf = (AppCompatButton) findViewById(R.id.profile_user);
        userNamePrf = (TextView) findViewById(R.id.profile_name);
        userEmailPrf = (AppCompatButton) findViewById(R.id.profile_email);
        userPhonePrf = (AppCompatButton) findViewById(R.id.profile_phone);
        userCityPrf = (AppCompatButton) findViewById(R.id.profile_city);
        userGroupPrf = (AppCompatButton) findViewById(R.id.profile_group);

        mUser  = (User) getIntent().getSerializableExtra("EXTRA_USER");

        setProfileDetails();
    }

    private void setProfileDetails() {
        String url = session.getAFCLink()+"/afc/assets/profile_pics/"+mUser.getPic();
        String name = mUser.getLname() + " " + mUser.getFname();
        String group = mUser.getSpec() + " " + mUser.getYear();

        final AppCompatImageView userPic = (AppCompatImageView) findViewById(R.id.profile_header_avatar);
        Glide.with(getApplicationContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(RequestOptions.circleCropTransform())
                .into(userPic);

        userNamePrf.setText(name);
        userAccPrf.setText(mUser.getUser());
        userPhonePrf.setText(mUser.getPhone());
        userEmailPrf.setText(mUser.getEmail());
        userCityPrf.setText(mUser.getCity());
        if(mUser.getType().equals("prof")){
            userGroupPrf.setText(getString(R.string.user_prof));
        } else {
            userGroupPrf.setText(group);
        }
        stopLoadingBar();
    }

    public void callUser(View view){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mUser.getPhone()));
        startActivity(intent);
    }

    public void sendMailUser(View view){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mUser.getEmail()});
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        startActivity(Intent.createChooser(intent, getString(R.string.app_intent_mail)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.nav_profile_message:
                Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                intent.putExtra("EXTRA_USER",(Serializable) mUser);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_profile;
    }
}
