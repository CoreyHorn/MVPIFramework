package com.coreyhorn.mvpiframework.architecture

import android.util.Log
import com.coreyhorn.mvpiframework.LOGGING_TAG
import com.coreyhorn.mvpiframework.MVPISettings
import com.coreyhorn.mvpiframework.basemodels.Result
import io.reactivex.Observable
import io.reactivex.subjects.ReplaySubject

abstract class Interactor<R : Result> {

    protected val results: ReplaySubject<R> = ReplaySubject.create()

    fun results(): Observable<R> =
            results.doOnNext {
                if (MVPISettings.loggingEnabled) {
                    Log.d(LOGGING_TAG, it.toString())
                }
            }
}