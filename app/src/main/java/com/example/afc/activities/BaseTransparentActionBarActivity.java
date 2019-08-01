package com.example.afc.activities;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import com.example.afc.R;

/* This is a Base Activity. Includes features used by multiple activies across the APP */

public abstract class BaseTransparentActionBarActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setup activity layout elements
        setContentView(getLayoutResource());
        configureToolbar();
        configureSideMenuHeader();
    }
    protected abstract int getLayoutResource();

    //Top menu intialization
    protected void configureToolbar() {
        toolbar = (Toolbar) findViewById(R.id.top_menu_layout_transparent);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorBackground), PorterDuff.Mode.SRC_ATOP);
        }
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
