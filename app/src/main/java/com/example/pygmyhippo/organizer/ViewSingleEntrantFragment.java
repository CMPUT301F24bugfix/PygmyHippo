package com.example.pygmyhippo.organizer;

/*
This Fragment is meant for the Organizer to view a single entrant from the list of entrants of their event
Will contain the functionality to filter the given list depending on the status of the entrant
Deals with user stories 02.06.04 currently
Author: Kori

Purposes:
    - Allow the organiser to view a single entrant
    - Let them see the entrant status and give them the option to cancel them if they are invited
    - Give them the option to draw a replacement winner on entrants with cancelled status
Issues:
    - No Image handling
    - Notifications haven't been dealt with yet
    - Replacement draw hasn't been dealt with yet
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pygmyhippo.R;

public class ViewSingleEntrantFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organiser_view_single_entrant, container, false);

        return view;
    }
}
