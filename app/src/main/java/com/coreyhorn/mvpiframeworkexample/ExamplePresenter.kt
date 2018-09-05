package com.coreyhorn.mvpiframeworkexample

import com.coreyhorn.mvpiframework.architecture.Presenter
import io.reactivex.Observable

class ExamplePresenter: Presenter<ExampleEvent, ExampleAction, ExampleResult, ExampleState>() {

    init {
        attachResultStream(ExampleInteractor().results())
    }

    override fun attachResultStream(results: Observable<ExampleResult>) {
        results.scan(ExampleState("hmm"), this::accumulator)
                .subscribe(states)
    }

    override fun attachEventStream(events: Observable<ExampleEvent>) {
        super.attachEventStream(events)
    }

    private fun accumulator(previousState: ExampleState, result: ExampleResult): ExampleState {
        return ExampleState("hmm")
    }
}