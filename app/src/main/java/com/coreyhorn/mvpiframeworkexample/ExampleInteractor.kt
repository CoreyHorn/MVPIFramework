package com.coreyhorn.mvpiframeworkexample

import com.coreyhorn.mvpiframework.architecture.Interactor
import io.reactivex.Observable

class ExampleInteractor(actions: Observable<ExampleAction>): Interactor<ExampleResult>() {

    init {
        actions.map { ExampleResult.TestResult() }.subscribe(results)
    }

}