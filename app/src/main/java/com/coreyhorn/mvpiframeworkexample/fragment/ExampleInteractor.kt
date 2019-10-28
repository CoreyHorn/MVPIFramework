package com.coreyhorn.mvpiframeworkexample.fragment

import android.util.Log
import com.coreyhorn.mvpiframework.architecture.MVIInteractor
import com.coreyhorn.mvpiframework.disposeWith
import com.coreyhorn.mvpiframeworkexample.ExampleEvent
import com.coreyhorn.mvpiframeworkexample.ExampleResult
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*
import java.util.concurrent.TimeUnit

class ExampleInteractor(events: Observable<ExampleEvent>): MVIInteractor<ExampleEvent, ExampleResult>(events) {

    init {
        Observable.interval(0, 10, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val text = UUID.randomUUID().toString()
                    Log.d("stuff", "new text: " + text)
                    pushResult(ExampleResult.ChangedText(text))
                }
                .disposeWith(disposables)

        connect()
    }

    override fun eventToResult(event: ExampleEvent): ExampleResult {
        return when (event) {
            is ExampleEvent.TestEvent -> ExampleResult.TestResult()
            is ExampleEvent.ChangeText -> {
                val text = UUID.randomUUID().toString()
                Log.d("stuff", "event received " + text)
                ExampleResult.ChangedText(text)
            }
        }
    }
}