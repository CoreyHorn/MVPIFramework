package com.coreyhorn.mvpiframework.architecture

import android.util.Log
import com.coreyhorn.mvpiframework.LOGGING_TAG
import com.coreyhorn.mvpiframework.MVPISettings
import com.coreyhorn.mvpiframework.basemodels.Result
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

abstract class Interactor<R : Result> {

    protected val results: PublishSubject<R> = PublishSubject.create()

    fun results(): Observable<R> = results.doOnNext {
        if (MVPISettings.loggingEnabled) {
            Log.d(LOGGING_TAG, it.toString())
        }
    }

    /**
     * This function will be called from the Presenter after they are subscribed to the Results
     * This is where the Interactor should subscribe to all outside sources of data / begin to
     * push results onto the Subject.
     */
    abstract fun connected()
}