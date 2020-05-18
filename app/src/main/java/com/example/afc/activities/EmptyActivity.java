package com.example.afc.activities;

import android.os.Bundle;
import android.view.View;

import com.example.afc.R;
import com.example.afc.app.Config;
import com.example.afc.app.DBManagement;

public class EmptyActivity extends BaseActivity {
    DBManagement db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DBManagement(this);
        stopLoadingBar();
    }

    public void clearTable(View view){
        db.clearTable(Config.DB_CHAT_TABLE);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_empty;
    }

    @Override
    public int getToolbarResource() { return R.id.top_menu_layout; }
}
