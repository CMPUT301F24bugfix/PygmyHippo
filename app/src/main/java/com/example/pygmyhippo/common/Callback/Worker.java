package com.example.pygmyhippo.common.Callback;

/**
 * Base class for creating Callback
 * Based on code by EboMike on StackOverflow
 * Code written Aug 3, 2010 [Accessed October 31]
 * https://stackoverflow.com/questions/3398363/how-to-define-callbacks-in-android
 * @Author: EboMike, added by Jennifer
 * @version: 1.0
 * */
public class Worker {
    public MyCallback callback;

    public void onEvent() throws Exception {
        callback.callbackCall();
    }

}
