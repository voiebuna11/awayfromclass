package com.example.afc.main;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.afc.R;
import com.example.afc.activities.BaseActivity;
import com.example.afc.app.Config;
import com.example.afc.app.User;
import com.example.afc.classes.CustomDrawerButton;
import com.example.afc.classes.CustomEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {
    RequestQueue mQueue;
    InputMethodManager imm;

    ArrayList<String> searchSuggestions;
    ArrayList<User> mLastUsersList;

    RecyclerView.LayoutManager mSearchLayoutManager;
    RecyclerView.Adapter mSearchAdapter;
    RecyclerView.Adapter mLastAdapter;

    CustomDrawerButton customToggleBtn;
    CustomEditText mSearchBox;
    CustomDrawerButton mBackSearchBtn;
    RecyclerView mSuggestionsView;
    RecyclerView mLastUsersView;

    ValueAnimator transformAnimation;
    ValueAnimator transformBackAnimation;
    Animation mRotateAnimation;
    Dialog searchDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mLastUsersView = (RecyclerView) findViewById(R.id.last_users_list);

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mQueue = Volley.newRequestQueue(getApplicationContext());
        mRotateAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotation);

        searchSuggestions = new ArrayList<String>();
        mLastUsersList = new ArrayList<User>();

        searchSuggestions = db.getSearchHistory("");
        jsonParseLastUsers();
        configureSideMenu();
        configureSearch();
    }

    private void jsonParseLastUsers() {
        startLoadingBar();
        String url = session.getAFCLink() + "/afc/user/get_last_registered_users.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject j = new JSONObject(response);
                    JSONArray jsonArray = j.getJSONArray("user_list");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject intermediar = jsonArray.getJSONObject(i);

                        mLastUsersList.add(new User(
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
                        ));
                    }
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
                    mLastUsersView.setLayoutManager(layoutManager);

                    mLastAdapter = new RecyclerLastUserListAdapter(getApplicationContext(), mLastUsersList, MainActivity.this);
                    mLastUsersView.setAdapter(mLastAdapter);

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
                params.put("request", sessionData.get(Config.KEY_ID)); //parametrii POST
                return params;
            }
        };
        mQueue.add(request);
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
        mSuggestionsView = (RecyclerView) view.findViewById(R.id.suggestion_list);
        mSuggestionsView.setHasFixedSize(true);

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

                    //Log.e("SAVED INTO history: ", search);
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

                setRecyclerList(mSuggestionsView, mSearchLayoutManager, mSearchAdapter, searchSuggestions);

                if(searchSuggestions.size() > 0){
                    //Log.e("RELOADED history: ", searchSuggestions.get(0));
                }
            }
        });

        searchDialog.setContentView(view);
    }

    public void showSearchDialog(View view){
        if(searchDialog.isShowing()) return;

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 0);

        Window window = searchDialog.getWindow();
        window.setBackgroundDrawable(inset);
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        setRecyclerList(mSuggestionsView, mSearchLayoutManager, mSearchAdapter, searchSuggestions);

        customToggleBtn.setAnimation(mRotateAnimation);
        transformAnimation.start();
        mBackSearchBtn.startAnimation(mRotateAnimation);
        customToggleBtn.startAnimation(mRotateAnimation);

        searchDialog.show();
        mSearchBox.requestFocus();
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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

    private void setRecyclerList(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager, RecyclerView.Adapter adapter, ArrayList arrayList){
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerSuggestionListAdapter(getApplicationContext(), arrayList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void hideSearchDialog(){
        imm.hideSoftInputFromWindow(mSearchBox.getWindowToken(), 0);
        searchDialog.dismiss();
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    public int getToolbarResource() {
        return R.id.top_menu_main_layout;
    }
}