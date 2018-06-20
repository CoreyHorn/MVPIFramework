package com.coreyhorn.mvpiframeworkexample

import com.coreyhorn.mvpiframework.architecture.Presenter
import io.reactivex.Observable

class ExamplePresenter: Presenter<ExampleEvent, ExampleAction, ExampleResult, ExampleState>() {

    override fun attachResultStream(results: Observable<ExampleResult>) {

    }

    override fun attachEventStream(events: Observable<ExampleEvent>) {
        super.attachEventStream(events)
    }
}