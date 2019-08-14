package com.example.afc.course;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.afc.R;
import com.example.afc.activities.BaseFragment;

public class EnrollmentRequestsFragment extends BaseFragment {
    private static final String TAG = "Cereri de inscriere";

    private String mCourseId, mCourseName;

    public EnrollmentRequestsFragment(String courseId){
        this.mCourseId = courseId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        stopLoadingBar();
        alert(mCourseId);
        return view;
    }

    @Override
    protected int getLayoutResource() { return R.layout.course_enrollment_requests_fragment; }
}
