package com.example.afc.course;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import com.example.afc.R;
import com.example.afc.activities.BaseActivity;

public class AddCourseActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.course_list_add));
        toolbar.getNavigationIcon().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);

        stopLoadingBar();
    }


    @Override
    public int getLayoutResource() {
        return R.layout.activity_add_course;
    }

    @Override
    public int getToolbarResource() { return R.id.top_menu_layout; }
}
