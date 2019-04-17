package com.coreyhorn.mvpiframeworkexample

import com.coreyhorn.mvpiframework.architecture.MVIInteractor
import io.reactivex.Observable

class ExampleInteractor(events: Observable<ExampleEvent>): MVIInteractor<ExampleEvent, ExampleResult>(events) {

    override fun eventToResult(event: ExampleEvent): ExampleResult {
        return when (event) {
            is ExampleEvent.TestEvent -> ExampleResult.TestResult()
            is ExampleEvent.ChangeText -> ExampleResult.ChangedText(java.util.UUID.randomUUID().toString())
        }
    }
}