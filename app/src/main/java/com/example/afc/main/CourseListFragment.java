package com.example.afc.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.afc.R;
import com.example.afc.activities.BaseFragment;

public class CourseListFragment extends BaseFragment {
    private static final String TAG = "Continut";
    private String mSearch;

    public CourseListFragment(String search){
        this.mSearch = search;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        stopLoadingBar();
        alert(mSearch);
        return view;
    }

    @Override
    protected int getLayoutResource() { return R.layout.course_contents_fragment; }
}
