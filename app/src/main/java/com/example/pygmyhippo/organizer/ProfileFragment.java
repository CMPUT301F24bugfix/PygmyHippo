package com.example.pygmyhippo.organizer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.OnRoleSelectedListener;
import com.example.pygmyhippo.databinding.OrganiserFragmentProfileBinding;

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
public class ProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    private OrganiserFragmentProfileBinding binding;

    // Listener interface to communicate the selected role
    private OnRoleSelectedListener roleSelectedListener;

    public void setOnRoleSelectedListener(OnRoleSelectedListener listener) {
        this.roleSelectedListener = listener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            // Ensure the activity implements the listener interface
            roleSelectedListener = (OnRoleSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnRoleSelectedListener");
        }
    }

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


        binding = OrganiserFragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Spinner role_dropdown = (Spinner) root.findViewById(R.id.organiser_E_P_role);
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

        role_dropdown.setOnItemSelectedListener(this);

        return root;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedRole = adapterView.getItemAtPosition(i).toString();
        if (roleSelectedListener != null) {
            roleSelectedListener.onRoleSelected(selectedRole);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}