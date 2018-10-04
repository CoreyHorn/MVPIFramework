package com.coreyhorn.mvpiframeworkexample

import com.coreyhorn.mvpiframework.architecture.Presenter
import io.reactivex.Observable
import java.util.*

class ExamplePresenter: Presenter<ExampleEvent, ExampleAction, ExampleResult, ExampleState>() {

    init {
        attachResultStream(ExampleInteractor(actions).results())
    }

    override fun attachResultStream(results: Observable<ExampleResult>) {
        results.scan(ExampleState("hmm"), this::accumulator)
                .subscribe(states)
    }

    override fun attachEventStream(events: Observable<ExampleEvent>) {
        super.attachEventStream(events)

        events.map { ExampleAction.TestAction() }.subscribe(actions)
    }

    private fun accumulator(previousState: ExampleState, result: ExampleResult): ExampleState {
        return ExampleState(Date().time.toString())
    }
}