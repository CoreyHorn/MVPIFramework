package com.coreyhorn.mvpiframeworkexample

import com.coreyhorn.mvpiframework.architecture.MVIInteractor
import com.coreyhorn.mvpiframework.architecture.MVIPresenter
import io.reactivex.Observable

class ExamplePresenter(initialState: ExampleState): MVIPresenter<ExampleEvent, ExampleResult, ExampleState>(initialState) {

    override fun provideInteractor(events: Observable<ExampleEvent>): MVIInteractor<ExampleEvent, ExampleResult> {
        return ExampleInteractor(events)
    }

    override fun resultToState(previousState: ExampleState, result: ExampleResult): ExampleState {
        return when (result) {
            is ExampleResult.TestResult -> previousState
            is ExampleResult.ChangedText -> previousState.copy(whatever = result.text)
        }
    }
}