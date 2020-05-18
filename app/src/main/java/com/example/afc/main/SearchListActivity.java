package com.example.afc.main;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.viewpager.widget.ViewPager;

import com.example.afc.R;
import com.example.afc.activities.BaseTogglelessActivity;
import com.example.afc.classes.SectionsPageAdapter;
import com.google.android.material.tabs.TabLayout;

public class SearchListActivity extends BaseTogglelessActivity {
    AppCompatTextView mSearchBox;

    String mSearchTerm;

    private ViewPager mViewPager;
    private SectionsPageAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.sidemenu_courses));

        mSearchBox = findViewById(R.id.search_box);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mTabLayout = (TabLayout) findViewById(R.id.search_tabs);

        mSectionsPagerAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mSearchTerm = getIntent().getStringExtra("EXTRA_SEARCH");

        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //do stuff here
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tab.getIcon().setTint(getResources().getColor(R.color.colorPrimaryDark));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tab.getIcon().setTint(getResources().getColor(R.color.colorDefaultMenuGray));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tab.getIcon().setTint(getResources().getColor(R.color.colorPrimaryDark));
                }
            }
        });

        mTabLayout.getTabAt(0).setIcon(R.drawable.tab_ic_users);
        mTabLayout.getTabAt(1).setIcon(R.drawable.tab_ic_courses);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) mTabLayout.getTabAt(1).getIcon().setTint(getResources().getColor(R.color.colorDefaultMenuGray));

        mSearchBox.setText(mSearchTerm);
    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new UserListFragment(mSearchTerm), getString(R.string.search_user_title));
        adapter.addFragment(new CourseListFragment(mSearchTerm), getString(R.string.search_courses_title));

        viewPager.setAdapter(adapter);
    }

    public void goBack(View view){
        finish();
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_search_list;
    }
}
