package com.example.pygmyhippo.common;

import static android.app.PendingIntent.getActivity;

import android.app.DownloadManager;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.example.pygmyhippo.MainActivity;

import java.io.File;
import java.net.URISyntaxException;

/**
 * This class takes care of passing the user's name to a website the generates avatars based on name
 * It then saves the avatar to shared memory and then generates the Uri for the image
 @author Jennifer Mckay, citations (methods have names of authors)
 @Version 1.0
 TODO: make this run async
 */
public class GenerateAvatar extends MainActivity {
    private Uri imagePath;
    private Context context;
    /**
     Original method from Pasan Bhanu on github,
     Code from 2019, [Accessed October 29, 2024]
     https://gist.github.com/PasanBhanu/46f08845ee3b7acf93de81acf8048fa2
     @author (edited by) Jennifer Mckay based on code Pasan Bhanu
     @param filename the name to give the picture being saved
     @param imagePath The url to the image
     @return the path to where the picture is stored
     */
    public void downloadImageNew(String filename, Uri imagePath){

        try{
            File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(imagePath);
            request.setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("image/png") // Your file type. You can use this code to download other file types also.
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, File.separator + filename + ".png");
            dm.enqueue(request);
            System.out.println(directory.toString() + "/avatar.png");


        }catch (Exception e){
            System.out.println(e.toString());
            // return "error";
        }
    }

    /**
     Method from Chatgpt, implemented by Jennifer:
     Purpose is to get the Uri of the image that was saved to the users device, everything else attempted has failed until this
     Queries the media store for the file
     First prompt: "to upload a picture from the emulator to firebase in android studio which is stored in the Environment.DIRECTORY_PICTURES
     what path should I use"
     part of it's response was: "However, it’s recommended to switch to MediaStore APIs for accessing images."
     Second prompt: "how do I use media store concisely please"
     [Accessed October 30, 2024]
     @author Chatgpt implemented by Jennifer
     @param imageName the name to give the picture being accessed
     @return Uri of the image
     */
    private Uri getImageUri(String imageName) {
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME};
        String selection = MediaStore.Images.Media.DISPLAY_NAME + "=?";
        String[] selectionArgs = new String[]{imageName};

        /* Idea to use fragment context to get rid of null pointer error
        * https://stackoverflow.com/questions/59643049/how-to-use-getcontentresolver-inside-non-activity-class
        * from reading comment of deluxan from January 8 2020, implemented October 30, 2024
        * */
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, selection, selectionArgs, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            long imageId = cursor.getLong(idColumn);

            Uri imageUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId
            );
            cursor.close();
            return imageUri;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    /**
     * generates an Avatar if the user doesn't upload one
     * @author Jennifer
     * @param name the name the user has entered
     * @return the Uri of the image in memory
     * @version 1.0
     */
    public Uri generateAvatar (Context context, String name) throws URISyntaxException {
        String imageName = "avatar.png";
        this.context = context;
        if (name.isEmpty()) name = "null";
        String url = "https://api.multiavatar.com/";
        this.imagePath = Uri.parse(url+name+".png");
        downloadImageNew("avatar", this.imagePath);
        this.imagePath = getImageUri(imageName);
        return this.imagePath;
    }
}
