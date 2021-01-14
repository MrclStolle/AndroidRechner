package de.stolle.myapps.ui.WechselgeldRechner;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WechselGViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public WechselGViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }
    public LiveData<String> getText() {
        return mText;
    }
}