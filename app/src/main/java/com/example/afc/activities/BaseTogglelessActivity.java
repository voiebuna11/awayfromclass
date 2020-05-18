package com.example.afc.activities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import com.example.afc.R;

/* This is a Base Activity. Includes features used by multiple activies across the APP */

public abstract class BaseTogglelessActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setup activity layout elements
        setContentView(getLayoutResource());
        configureToolbar();
    }
    protected abstract int getLayoutResource();

    //Top menu intialization
    protected void configureToolbar() {
        toolbar = (Toolbar) findViewById(getToolbarResource());
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.getNavigationIcon().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);
            toolbar.setPadding(0,0,0,0);
            toolbar.setContentInsetsAbsolute(0,0);
        }
    }

    @Override
    public int getToolbarResource() {
        return R.id.top_menu_layout_transparent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
