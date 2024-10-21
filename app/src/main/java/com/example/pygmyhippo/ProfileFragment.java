package com.example.pygmyhippo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pygmyhippo.databinding.FragmentProfileBinding;
import com.google.android.material.navigation.NavigationBarView;

/**
 * This fragment holds most of the information about a user which is returned from a call to the database
 * To be Implemented: the edit button will change the interface and allow the user to edit all fields and send it to
 * the database.
 * Currently just a static page.
 * Allows the user to edit or view their current provided information.
 * @author Jennifer
 * @version 1.0
 * No returns and no parameters
 */
public class ProfileFragment extends Fragment {


    private FragmentProfileBinding binding;

    /**
     * Creates the view
     * @author Jennifer
     * @param inflater not sure
     * @param container not sure
     * @param savedInstanceState not sure
     * @return root not sure
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Spinner role_dropdown = (Spinner) root.findViewById(R.id.E_P_role);
// Create an ArrayAdapter using the string array and a default role_dropdown layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                root.getContext(),
                R.array.role,
                R.layout.e_p_role_dropdown
        );

// Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(R.layout.e_p_role_dropdown);

// Apply the adapter to the role_dropdown.
        role_dropdown.setAdapter(adapter);

        //FIXME: Setting up rough navigation to the work done for organizer role
        role_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // This was learned from https://stackoverflow.com/questions/12108893/set-onclicklistener-for-spinner-item#:~:text=You%20SHOULD%20NOT%20call%20OnItemClickListener%20on%20a%20spinner.,Exception.%20Check%20this.%20You%20can%20apply%20OnItemSelectedListener%20instead.
            // On Oct 20th, 2024
            public void onItemSelected(AdapterView<?> adapter, View view, int i,long id) {
                String role = adapter.getItemAtPosition(i).toString();
                if (role.equals("Organizer")) {
                    // Learned navigation on Oct 20th, 2024
                    // From: https://learntodroid.com/how-to-move-between-fragments-using-the-navigation-component/
                    Navigation.findNavController(view).navigate(R.id.action_navigation_notifications_to_navigation_o_view_entrants);
                }
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}