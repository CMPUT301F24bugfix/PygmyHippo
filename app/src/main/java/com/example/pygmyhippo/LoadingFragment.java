package com.example.pygmyhippo;

/*
Not actually implemented yet
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import androidx.fragment.app.Fragment;

/**
 * Loading page while MainActivity checks with Firestore on log in.
 */
public class LoadingFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.loading_fragment, container, false);
    }
}