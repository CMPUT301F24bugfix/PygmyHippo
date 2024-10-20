package com.example.pygmyhippo.ui.QR;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QRViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public QRViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is QR Scanner fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}