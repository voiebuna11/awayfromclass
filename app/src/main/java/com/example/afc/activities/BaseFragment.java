package com.example.afc.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.afc.R;
import com.example.afc.app.SessionManagement;

import java.util.HashMap;

public abstract class BaseFragment extends Fragment {
    private static final String TAG = "Base Fragment";

    public RequestQueue mQueue;
    public SessionManagement session;
    public HashMap<String, String> sessionData;
    public RelativeLayout progressCircle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResource(), container, false);

        progressCircle = (RelativeLayout) view.findViewById(R.id.progress_rl);
        startLoadingBar();

        mQueue =  Volley.newRequestQueue(getActivity());
        session = new SessionManagement(getActivity());
        sessionData = session.getUserDetails();
        return view;
    }

    protected abstract int getLayoutResource();

    public void startLoadingBar(){
        progressCircle.setVisibility(View.VISIBLE);
    }

    public void stopLoadingBar(){
        progressCircle.setVisibility(View.GONE);
    }

    public void alert(String text){
        Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
    }
}
