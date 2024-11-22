package com.example.pygmyhippo.organizer;

/*
The purpose of this class is to take the data from the entrants in the given filtered entrant list and display that
on the app listview
Author: Kori Kozicki

Outstanding Issues: None
 */

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.database.AccountDB;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.database.ImageStorage;
import com.example.pygmyhippo.database.StorageOnCompleteListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * This Array Adapter is customized for the viewing entrants list
 * @author Kori
 */
public class EntrantArrayAdapter extends ArrayAdapter<Entrant> {
    private AccountDB dbHandler;
    private ImageStorage imageHandler;
    private Account account;

    public EntrantArrayAdapter(Context context, ArrayList<Entrant> entrantListData) {
        super(context, 0, entrantListData);
    }

    /**
     * This Method will customize the entrant list depending on their profile values
     * @param position position in the array
     * @param convertView
     * @param parent
     * @return the view to display one entrant
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        /*
        This method overrides the getView method so that we can customize it to display multiple textViews
         */
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.o_view_entrants_content, parent, false);
        } else {
            view = convertView;
        }

        // Initialize handlers
        dbHandler = new AccountDB();
        imageHandler = new ImageStorage();

        Entrant entrant = getItem(position);
        Log.d("DB", String.format("Finding %s Account", entrant.getAccountID()));

        // Get all the views from the entrants_content in our xml file
        TextView accountName = view.findViewById(R.id.o_entrant_name_view);
        TextView accountPhone = view.findViewById(R.id.o_entrant_phone_view);
        TextView accountEmail = view.findViewById(R.id.o_entrant_email_view);
        TextView accountStatus = view.findViewById(R.id.o_entrant_status_txt_view);
        TextView accountLocation = view.findViewById(R.id.o_entrant_location_txt_view);
        ImageView profileImage = view.findViewById(R.id.o_entrant_image_view);

        // Set placeholders while we wait for the data to be retrieved
        accountName.setText("Loading...");
        accountPhone.setText("Loading...");
        accountEmail.setText("Loading...");
        accountLocation.setText("Loading...");
        accountStatus.setText("Status: " + entrant.getEntrantStatus().value);

        // From the database, get the entrant's account info
        dbHandler.getAccountByID(entrant.getAccountID(), new DBOnCompleteListener<Account>() {
            @Override
            public void OnCompleteDB(@NonNull ArrayList<Account> docs, int queryID, int flags) {
                switch (queryID) {
                    case 0: // getAccount()
                        if (flags == DBOnCompleteFlags.SINGLE_DOCUMENT.value) {
                            // Get the account for this entrant
                            account = docs.get(0);

                            // Replace the placeholder texts with the real values from this current Entrant account
                            accountName.setText(account.getName());
                            accountPhone.setText(account.getPhoneNumber());
                            accountEmail.setText(account.getEmailAddress());
                            accountLocation.setText(account.getLocation());
                            Log.d("DB", String.format("Account name:  (%s)", account.getName()));
                            Log.d("DB", String.format("Account phone:  (%s)", account.getPhoneNumber()));
                            Log.d("DB", String.format("Account email:  (%s)", account.getEmailAddress()));

                            // Check if the account has uploaded an image
                            if (!account.getProfilePicture().isEmpty()) {
                                // Has an image so load that in from the database
                                imageHandler.getImageDownloadUrl(account.getProfilePicture(), new StorageOnCompleteListener<Uri>() {
                                    @Override
                                    public void OnCompleteStorage(@NonNull ArrayList<Uri> docs, int queryID, int flags) {
                                        // Author of this code segment is James
                                        if (flags == DBOnCompleteFlags.SUCCESS.value) {
                                            // Get the image and format it
                                            Uri downloadUri = docs.get(0);
                                            int imageSideLength = profileImage.getWidth() / 2;
                                            Picasso.get()
                                                    .load(downloadUri)
                                                    .resize(imageSideLength, imageSideLength)
                                                    .centerCrop()
                                                    .into(profileImage);
                                        }
                                    }
                                });
                            } else {
                                // The account doesn't have an image, so generate their avatar
                                // Author of this code is Jen
                                String name = account.getName();
                                if (name.isEmpty()) name = "null";
                                String url = "https://api.multiavatar.com/";
                                Uri avatarURI = Uri.parse(url+name+".png");

                                // Retrieve the generated profile picture
                                Picasso.get()
                                        .load(avatarURI)
                                        .into(profileImage);
                            }
                        } else {
                            // Should only ever expect 1 document, otherwise there must be an error
                            handleDBError();
                        }
                        break;
                    default:
                        Log.i("DB", String.format("Unknown query ID (%d)", queryID));
                        handleDBError();
                }
                }
        });
        return view;
    }

    /**
     * This method displays if there was an error in the database
     */
    private void handleDBError() {
        Toast toast = Toast.makeText(getContext(), "DB Error!", Toast.LENGTH_SHORT);
        toast.show();
    }
}
