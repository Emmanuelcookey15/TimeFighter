package com.bolaji.todolistproject;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class NoteViewModelFactory implements ViewModelProvider.Factory{

    private Application mApplication;
    private String mParam;

    public NoteViewModelFactory(Application application, String param) {
        mApplication = application;
        mParam = param;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return null;
    }
}
