package com.coreyhorn.mvpiframeworkexample

import android.util.Log
import com.coreyhorn.mvpiframework.architecture.MVIInteractor
import com.coreyhorn.mvpiframework.disposeWith
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class ExampleInteractor(events: Observable<ExampleEvent>): MVIInteractor<ExampleEvent, ExampleResult>(events) {

    init {
        Observable.interval(0, 3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("stuff", "new text from: " + this)
                    pushResult(ExampleResult.ChangedText(java.util.UUID.randomUUID().toString()))
                }
                .disposeWith(disposables)
    }

    override fun eventToResult(event: ExampleEvent): ExampleResult {
        return when (event) {
            is ExampleEvent.TestEvent -> ExampleResult.TestResult()
            is ExampleEvent.ChangeText -> ExampleResult.ChangedText(java.util.UUID.randomUUID().toString())
        }
    }
}