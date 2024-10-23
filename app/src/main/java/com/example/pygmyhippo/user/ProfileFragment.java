package com.example.pygmyhippo.user;

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
import com.example.pygmyhippo.databinding.UserFragmentProfileBinding;
import com.example.pygmyhippo.common.OnRoleSelectedListener;

/**
 * This fragment holds most of the information about a user which is returned from a call to the database
 * To be Implemented: the edit button will change the interface and allow the user to edit all fields and send it to
 * the database.
 *
 * To be fixed:
 * - The framework for the communication between this can the main activity needs to be more robust
 *   (right now when this fragment is open the drop down is triggered sending roleSelectedListener,
 *   which would change the role to user since its he first role in the drop down)
 *
 * Currently just a static page.
 * Allows the user to edit or view their current provided information.
 * @author Jennifer, Griffin
 * @version 1.1
 * No returns and no parameters
 */
public class ProfileFragment extends Fragment  implements AdapterView.OnItemSelectedListener{


    private UserFragmentProfileBinding binding;

    // Listener interface to communicate with the main activity
    private OnRoleSelectedListener roleSelectedListener;

    // initialized the listener
    public void setOnRoleSelectedListener(OnRoleSelectedListener listener) {
        this.roleSelectedListener = listener;
    }

    /**
     * This is called when the fragment is created, this ensure a connection the onrole listener to the parent
     * @author Griffin
     * @param context not sure
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
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


        binding = UserFragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Spinner role_dropdown = (Spinner) root.findViewById(R.id.user_E_P_role);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                root.getContext(),
                R.array.role,
                R.layout.e_p_role_dropdown
        );

        adapter.setDropDownViewResource(R.layout.e_p_role_dropdown);
        role_dropdown.setAdapter(adapter);

        // need to do this so the listener is connected
        role_dropdown.setOnItemSelectedListener(this);

        return root;
    }

    /**
     * since this implements the OnTimeSelectedLister we need to override these two methods to get
     * the communication working
     * @author Griffin
     * @param adapterView: The adapter view of the selectable options
     * @param view: not sure
     * @param i: position of item clicked
     * @param l: not sure
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedRole = adapterView.getItemAtPosition(i).toString();
        if (roleSelectedListener != null) {
            roleSelectedListener.onRoleSelected(selectedRole);
        }
    }

    /**
     * This is for the case when nothing is selected
     * @author Griffin
     * @param adapterView: The adapter view of the selectable options
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}