package com.example.osku.fuksipassi;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Osku on 10.4.2018.
 */

public class ChallengeFragment extends Fragment {
    public Fragment newInstance() {
        return new ChallengeFragment();
    }
    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.challenge_fragment, container, false);
        recyclerView = view.findViewById(R.id.challenge_frag);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final TempDataReader reader = new TempDataReader(getActivity());
        List<List<String>> super_list = reader.readFile("challenges",false);
        adapter = new RecyclerViewAdapter(super_list, "challenges", new Runnable() {
            @Override
            public void run() {
                List<List<String>> super_list = reader.readFile("challenges",false);
                adapter = new RecyclerViewAdapter(super_list, "challenges");
                recyclerView.setAdapter(adapter);
            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }
}

