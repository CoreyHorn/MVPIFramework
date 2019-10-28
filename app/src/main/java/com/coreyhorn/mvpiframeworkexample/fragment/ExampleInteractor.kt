package com.coreyhorn.mvpiframeworkexample.fragment

import android.util.Log
import com.coreyhorn.mvpiframework.architecture.MVIInteractor
import com.coreyhorn.mvpiframework.disposeWith
import com.coreyhorn.mvpiframeworkexample.ExampleEvent
import com.coreyhorn.mvpiframeworkexample.ExampleResult
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import java.util.concurrent.TimeUnit

class ExampleInteractor(events: Observable<ExampleEvent>): MVIInteractor<ExampleEvent, ExampleResult>(events) {

    init {
        Observable.interval(0, 5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("stuff", "new text: " + this.toString())
                    pushResult(ExampleResult.ChangedText(UUID.randomUUID().toString()))
                }
                .disposeWith(disposables)

        connect()
    }

    override fun eventToResult(event: ExampleEvent): ExampleResult {
        return when (event) {
            is ExampleEvent.TestEvent -> ExampleResult.TestResult()
            is ExampleEvent.ChangeText -> ExampleResult.ChangedText(UUID.randomUUID().toString())
        }
    }
}