package com.example.afc.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afc.R;
import com.example.afc.activities.BaseFragment;
import com.example.afc.app.Config;
import com.example.afc.app.User;

import java.util.ArrayList;

public class UserListFragment extends BaseFragment {
    private static final String TAG = "Continut";
    private String mSearch;
    ArrayList<User> mUserList;

    RecyclerView mUsersRecyclerView;
    RecyclerView.LayoutManager mUsersLayoutManager;
    RecyclerView.Adapter mAdapter;

    public UserListFragment(String search){
        this.mSearch = search;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mUserList = new ArrayList<User>();

        for(int i = 0; i < 10; i++){
            mUserList.add(new User(
                    "1",
                    "std",
                    "test",
                    "ciprian@gmail.com",
                    "Prenume",
                    "Nume",
                    "Baia Mare",
                    "0762190393",
                    "4",
                    "CAL",
                    sessionData.get(Config.KEY_EMAIL),
                    "12312312313123"
            ));
        }
        mUsersRecyclerView = (RecyclerView) view.findViewById(R.id.search_user_list);
        mUsersRecyclerView.setHasFixedSize(true);

        mUsersLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mUsersRecyclerView.setLayoutManager(mUsersLayoutManager);
        mAdapter = new RecyclerUserSearchAdapter(getActivity(), mUserList);
        mUsersRecyclerView.setAdapter(mAdapter);

        //jsonParseCourseList();

        stopLoadingBar();
        alert(mSearch);
        return view;
    }

    @Override
    protected int getLayoutResource() { return R.layout.search_list_user_fragment; }
}
