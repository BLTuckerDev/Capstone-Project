package dev.bltucker.nanodegreecapstone.readlater;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dev.bltucker.nanodegreecapstone.R;

public class ReadLaterListFragment extends Fragment {

    public ReadLaterListFragment() {
        // Required empty public constructor
    }

    public static ReadLaterListFragment newInstance() {
        return new ReadLaterListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_read_later_list, container, false);
    }

}
