package com.example.afc.main;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.afc.R;
import com.example.afc.activities.BaseActivity;
import com.example.afc.activities.EmptyActivity;
import com.example.afc.chat.ChatRoomActivity;
import com.example.afc.classes.CustomDrawerButton;
import com.example.afc.classes.CustomEditText;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    RequestQueue mQueue;
    InputMethodManager imm;

    ArrayList<String> searchSuggestions;
    RecyclerView.LayoutManager mSearchLayoutManager;
    RecyclerView.Adapter mSearchAdapter;

    CustomDrawerButton customToggleBtn;
    CustomEditText mSearchBox;
    CustomDrawerButton mBackSearchBtn;
    RecyclerView mRecyclerSuggestionList;

    ValueAnimator transformAnimation;
    ValueAnimator transformBackAnimation;
    Animation mRotateAnimation;
    Dialog searchDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mRotateAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotation);

        searchSuggestions = new ArrayList<String>();
        searchSuggestions = db.getSearchHistory("");


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
        view = inflater.inflate(R.layout.modal_search_layout, null, false);

        mSearchBox = (CustomEditText) view.findViewById(R.id.search_box);
        mBackSearchBtn = (CustomDrawerButton) view.findViewById(R.id.search_toggle_btn);
        mRecyclerSuggestionList = (RecyclerView) view.findViewById(R.id.suggestion_list);
        mRecyclerSuggestionList.setHasFixedSize(true);

        transformAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), R.drawable.ic_search_dialog_toggle, R.drawable.ic_search_dialog_back);
        transformAnimation.setDuration(600);
        transformAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mBackSearchBtn.setBackgroundResource((int) animator.getAnimatedValue());
                customToggleBtn.setBackgroundResource((int) animator.getAnimatedValue());
            }
        });
        mBackSearchBtn.setAnimation(mRotateAnimation);

        searchDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface arg0) {
                transformBackAnimation.start();
                customToggleBtn.startAnimation(mRotateAnimation);
            }
        });
        mSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String search = v.getText().toString();
                    if(search.length()==0) return true;

                    db.insertSearch(search);
                    hideSearchDialog();
                    v.setText("");

                    Log.e("SAVED INTO history: ", search);
                    return true;
                }
                return false;
            }
        });
        mSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) { }
            @Override
            public void beforeTextChanged(CharSequence s, int start,int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchSuggestions.clear();
                searchSuggestions = db.getSearchHistory(s.toString());

                refreshSuggestionList();
                mSearchAdapter.notifyDataSetChanged();

                if(searchSuggestions.size() > 0){
                    Log.e("RELOADED history: ", searchSuggestions.get(0));
                }
            }
        });

        searchDialog.setContentView(view);
    }
    //functia pt meniu principal
    public void showSearchDialog(View view){
        if(searchDialog.isShowing()) return;

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 0);

        Window window = searchDialog.getWindow();
        window.setBackgroundDrawable(inset);
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        refreshSuggestionList();

        customToggleBtn.setAnimation(mRotateAnimation);
        transformAnimation.start();
        mBackSearchBtn.startAnimation(mRotateAnimation);
        customToggleBtn.startAnimation(mRotateAnimation);

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

        transformBackAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), R.drawable.ic_search_dialog_back, R.drawable.ic_search_dialog_toggle);
        transformBackAnimation.setDuration(600);
        transformBackAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mBackSearchBtn.setBackgroundResource((int) animator.getAnimatedValue());
                customToggleBtn.setBackgroundResource((int) animator.getAnimatedValue());
            }
        });
    }

    private void refreshSuggestionList(){
        mSearchLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRecyclerSuggestionList.setLayoutManager(mSearchLayoutManager);
        mSearchAdapter = new RecyclerSuggestionListAdapter(getApplicationContext(), searchSuggestions);
        mRecyclerSuggestionList.setAdapter(mSearchAdapter);
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
        startActivity(intent);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_main;
    }
}