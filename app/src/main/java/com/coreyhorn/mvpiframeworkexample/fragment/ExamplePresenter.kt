package com.coreyhorn.mvpiframeworkexample.fragment

import android.util.Log
import com.coreyhorn.mvpiframework.architecture.MVIInteractor
import com.coreyhorn.mvpiframework.architecture.MVIViewModel
import com.coreyhorn.mvpiframeworkexample.ExampleEvent
import com.coreyhorn.mvpiframeworkexample.ExampleResult
import com.coreyhorn.mvpiframeworkexample.ExampleState
import io.reactivex.Observable

class ExamplePresenter: MVIViewModel<ExampleEvent, ExampleResult, ExampleState>() {

    override fun provideInteractor(events: Observable<ExampleEvent>): MVIInteractor<ExampleEvent, ExampleResult> {
        return ExampleInteractor(events)
    }

    override fun resultToState(previousState: ExampleState, result: ExampleResult): ExampleState {
        return when (result) {
            is ExampleResult.TestResult -> previousState
            is ExampleResult.ChangedText -> {
                previousState.copy(whatever = result.text)
            }
        }
    }
}