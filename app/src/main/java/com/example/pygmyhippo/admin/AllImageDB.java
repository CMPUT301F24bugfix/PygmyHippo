package com.example.pygmyhippo.admin;

import android.net.Uri;
import android.util.Log;

import com.example.pygmyhippo.common.Image;
import com.example.pygmyhippo.database.DBHandler;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AllImageDB extends DBHandler {
    public void getImageDownloadUrl(Image image, DBOnCompleteListener<Uri> listener) {
        StorageReference ref = storage.getReferenceFromUrl(image.getUrl());
        ref.getDownloadUrl().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("DB", String.format("Found image at %s", image.getUrl()));
                Uri uri = task.getResult();
                ArrayList<Uri> results = new ArrayList<>();
                results.add(uri);
                listener.OnComplete(results, 0, DBOnCompleteFlags.SUCCESS.value);
            } else {
                Log.d("DB", String.format("Image download failed for %s", image.getUrl()));
            }
        });
    }
}
