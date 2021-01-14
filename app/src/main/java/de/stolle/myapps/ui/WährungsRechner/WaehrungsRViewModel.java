package de.stolle.myapps.ui.WährungsRechner;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WaehrungsRViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public WaehrungsRViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}