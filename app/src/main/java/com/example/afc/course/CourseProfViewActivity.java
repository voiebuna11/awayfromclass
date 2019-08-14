package com.example.afc.course;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.ViewPager;

import com.example.afc.R;
import com.example.afc.activities.BaseActivity;
import com.example.afc.classes.SectionsPageAdapter;
import com.google.android.material.tabs.TabLayout;

public class CourseProfViewActivity extends BaseActivity {
    private static final String TAB = "Course View";
    private ViewPager mViewPager;
    private SectionsPageAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;

    String mCourseId;
    String mCourseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCourseId = getIntent().getStringExtra("EXTRA_COURSE_ID");
        mCourseName = getIntent().getStringExtra("EXTRA_COURSE_NAME");

        setTitle(mCourseName);
        mSectionsPagerAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mTabLayout = (TabLayout) findViewById(R.id.course_prof_view_tabs);

        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);

        //MARGINS BETWEEN TABS
        for(int i=0; i < mTabLayout.getTabCount(); i++) {
            int spacing_left = getResources().getDimensionPixelSize(R.dimen.course_view_tab_spacing);
            int spacing_right = getResources().getDimensionPixelSize(R.dimen.course_view_tab_spacing);

            //Margin left x2 for first item
            if(i==0) spacing_left = spacing_left*2;

            //Margin right x2 for last item
            if(i == mTabLayout.getTabCount()-1) spacing_right = spacing_right*2;

            View tab = ((ViewGroup) mTabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(spacing_left, 0, spacing_right, 0);
            tab.requestLayout();
        }

        stopLoadingBar();
    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new ContentsFragment(mCourseId), getString(R.string.course_view_content_title));
        adapter.addFragment(new NewsFragment(mCourseId), getString(R.string.course_view_news_title));
        adapter.addFragment(new MembersFragment(mCourseId), getString(R.string.course_view_participants_title));
        adapter.addFragment(new EnrollmentRequestsFragment(mCourseId), getString(R.string.course_view_enroll_requests_title));

        viewPager.setAdapter(adapter);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_course_prof_view;
    }

    @Override
    public int getToolbarResource() { return R.id.top_menu_layout; }
}