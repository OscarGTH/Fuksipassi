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
 * Created by Osku on 11.4.2018.
 */

public class CompletedFragment extends Fragment {
    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.completed_fragment, container, false);

        recyclerView = view.findViewById(R.id.completed_frag);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setRecyclerView();
        return view;
    }

    public void setRecyclerView() {
        TempDataReader reader = new TempDataReader(getActivity());
        List<List<String>> super_list = reader.readFile("completed",false);
        adapter = new RecyclerViewAdapter(super_list, "completed");
        recyclerView.setAdapter(adapter);
    }
}
