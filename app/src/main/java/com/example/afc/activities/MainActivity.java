package com.example.afc.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.afc.R;
import com.example.afc.app.User;
import com.example.afc.chat.ChatRoomActivity;
import com.example.afc.classes.CustomDrawerButton;
import com.example.afc.classes.CustomEditText;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    RequestQueue mQueue;
    ArrayList<User> userArrayList = new ArrayList<>();

    CustomDrawerButton customToggleBtn;
    Dialog searchDialog;
    CustomEditText mSearchBox;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mQueue = Volley.newRequestQueue(getApplicationContext());

        configureSideMenu();
        configureToolbar();
        configureSearch();

        stopLoadingBar();
    }

    public void configureSearch(){
        searchDialog = new Dialog(this);
        searchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        searchDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View view;
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.search_layout, null, false);
        mSearchBox = (CustomEditText) view.findViewById(R.id.search_box);

        mSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideSearchDialog();
                    alert(v.getText().toString());
                    v.setText("");
                    return true;
                }
                return false;
            }
        });


        CustomDrawerButton mBtn = (CustomDrawerButton) view.findViewById(R.id.search_toggle_btn);
        Animation mRotateAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotation);

        mBtn.setAnimation(mRotateAnimation);
        mBtn.startAnimation(mRotateAnimation);

        searchDialog.setContentView(view);
    }
    //functia pt meniu principal
    public void showSearchDialog(View view){
        if(searchDialog.isShowing()) return;

        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int dp_8 = (int) (8 * scale + 0.5f);
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 0);

        Window window = searchDialog.getWindow();
        window.setBackgroundDrawable(inset);
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        searchDialog.show();
        mSearchBox.requestFocus();
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }

    protected void configureToolbar() {
        toolbar = (Toolbar) findViewById(R.id.top_menu_main_layout);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    protected void configureSideMenu() {
        customToggleBtn = (CustomDrawerButton) findViewById(R.id.main_toggle_btn);

        customToggleBtn.setDrawerLayout(drawerLayout);
        customToggleBtn.getDrawerLayout().addDrawerListener(customToggleBtn);
        customToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customToggleBtn.changeState();
            }
        });
    }

    private void hideSearchDialog(){
        imm.hideSoftInputFromWindow(mSearchBox.getWindowToken(), 0);
        searchDialog.dismiss();
    }

    public void goToEmpty(View view) {
        Intent intent = new Intent(getBaseContext(), EmptyActivity.class);
        startActivity(intent);
    }

    public void goToChatRoom(View view) {
        Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
        intent.putExtra("EXTRA_USER_LIST", (Serializable) userArrayList);
        intent.putExtra("EXTRA_USER", (Serializable) userArrayList.get(0));
        startActivity(intent);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_main;
    }
}