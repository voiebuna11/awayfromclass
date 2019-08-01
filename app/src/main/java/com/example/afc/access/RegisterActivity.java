package com.example.afc.access;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.afc.R;
import com.example.afc.classes.SectionsPageAdapter;
import com.google.android.material.tabs.TabLayout;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAB = "Register";
    private ViewPager mViewPager;
    private SectionsPageAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mSectionsPagerAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new StudentFragment(), "Student");
        adapter.addFragment(new ProfessorFragment(), "Profesor");
        viewPager.setAdapter(adapter);
    }

    public void goToLogin(View view){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        // Closing all the Activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add new Flag to start new Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {}
}