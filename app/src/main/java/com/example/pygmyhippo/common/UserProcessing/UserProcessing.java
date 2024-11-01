package com.example.pygmyhippo.common.UserProcessing;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import org.chromium.net.CronetEngine;
import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
/**
 * Allows Download of avatar from online generator
 * Source Code from Googles Cronet Docs
 * https://chromium.googlesource.com/chromium/src/+/refs/heads/main/components/cronet/getting_started.md
 * Accessed November 1, 2024
 * Supplemented holes in the code with the Kotlin CodeLab from android
 * https://developer.android.com/codelabs/cronet#7
 * and the Android developer docs
 * https://developer.android.com/develop/connectivity/cronet/start#java
 @author Google, implemented by Jennifer Mckay
 @Version 1.0
 TODO: make this run async
 */
public class UserProcessing {
    private String imagePath2 = "https://avatar.iran.liara.run/username?username=Jennifer";
    private Uri imagePath;
    Context context;
    byte[] bodyBytes;

    class CronetCallback2 extends UrlRequest.Callback {
        private ByteArrayOutputStream bytesReceived = new ByteArrayOutputStream();
        private WritableByteChannel receiveChannel = Channels.newChannel(bytesReceived);
        @Override
        public void onRedirectReceived(UrlRequest request,
                                       UrlResponseInfo responseInfo, String newLocationUrl) {
            System.out.println("Redirect");
            request.cancel();
        }

        @Override
        public void onResponseStarted(UrlRequest request,
                                      UrlResponseInfo responseInfo) {
            try {
                // Now we have response headers!
                int httpStatusCode = responseInfo.getHttpStatusCode();
                System.out.println("Response Info: " + responseInfo.getHttpStatusCode() + " " + responseInfo.getHttpStatusText());
                System.out.println("Content-Type: " + responseInfo.getAllHeaders().get("Content-Type"));

                System.out.println("ONRS");
                if (httpStatusCode == 200) {
                    // Success! Let's tell Cronet to read the response body.
                    request.read(ByteBuffer.allocateDirect(102400));
                    System.out.println("Code 200");
                } else if (httpStatusCode == 503) {
                    System.out.println("Code 503");
                    // Do something. Note that 4XX and 5XX are not considered
                    // errors from Cronet's perspective since the response is
                    // successfully read.
                } else {
                    System.out.print(httpStatusCode);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            //  mResponseHeaders = responseInfo.getAllHeaders();
        }

        @Override
        public void onReadCompleted(UrlRequest request,
                                    UrlResponseInfo responseInfo, ByteBuffer byteBuffer) throws IOException {
            // Response body is available.
            byteBuffer.flip();
            receiveChannel.write(byteBuffer);
            // Let's tell Cronet to continue reading the response body or
            // inform us that the response is complete!
            System.out.println("On Read Running");
            byteBuffer.clear();
            request.read(byteBuffer);
        }

        @Override
        public void onSucceeded(UrlRequest request,
                                UrlResponseInfo responseInfo) {

            // Request has completed successfully!
          /*  bodyBytes = bytesReceived.toByteArray();
            System.out.println("Success!!!!");
            byteArrayToBitmap(); */

            if (bytesReceived.size() > 0) {
                bodyBytes = bytesReceived.toByteArray();
                System.out.println("Success!!!!");
                byteArrayToBitmap();
            } else {
                System.err.println("Received empty byte array.");
            }
        }

        @Override
        public void onFailed(UrlRequest request,
                             UrlResponseInfo responseInfo, CronetException error) {
            // Request has failed. responseInfo might be null.
            Log.e("MyCallback", "Request failed. " + error.getMessage());
            System.out.println("Request failed. " + error.getMessage());
            // Maybe handle error here. Typical errors include hostname
            // not resolved, connection to server refused, etc.
        }
    }

    public void startUp (Activity activity) {
        System.out.println("Running");
        this.context = activity.getApplicationContext();
        CronetEngine.Builder engineBuilder = new CronetEngine.Builder(context);
        CronetEngine engine = engineBuilder.build();
        Executor executor = Executors.newSingleThreadExecutor();
        CronetCallback2 crCallback;
        crCallback = new CronetCallback2();

        UrlRequest.Builder requestBuilder = engine.newUrlRequestBuilder(
                imagePath2, crCallback, executor);
        UrlRequest request = requestBuilder.build();
        try {

            request.start();

        } catch (Exception e) {
            Log.e("Cronet", "Error starting request: " + e.getMessage());
            System.out.println(e.getMessage());
        }


    }

    public  void byteArrayToBitmap (){
        // Decode the byte array into a Bitmap
        Bitmap pic = BitmapFactory.decodeByteArray(bodyBytes, 0, bodyBytes.length);
        System.out.println(pic != null ? "The bitmap is viable." : "The bitmap is not viable.");

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
        return this.imagePath;
    }

}
