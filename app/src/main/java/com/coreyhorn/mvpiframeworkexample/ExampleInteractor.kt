package com.coreyhorn.mvpiframeworkexample

import com.coreyhorn.mvpiframework.architecture.Interactor

class ExampleInteractor: Interactor<ExampleResult>() {

    init {
        results.onNext(ExampleResult.TestResult())
    }

}