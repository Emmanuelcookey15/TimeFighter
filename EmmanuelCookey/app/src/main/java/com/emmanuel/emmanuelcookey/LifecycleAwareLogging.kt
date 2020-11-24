package com.emmanuel.emmanuelcookey

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class LifecycleAwareLogging: LifecycleObserver {

    private val LOG_TAG = "LifecycleAwareLogging"

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun listenToCreate(){
        Log.d(LOG_TAG, "onCreate()")
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun listenToDestroy(){
        Log.d(LOG_TAG, "onDestroy()")
    }


}