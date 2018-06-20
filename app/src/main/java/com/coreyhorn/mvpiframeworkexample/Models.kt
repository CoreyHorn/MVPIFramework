package com.coreyhorn.mvpiframeworkexample

import com.coreyhorn.mvpiframework.basemodels.Action
import com.coreyhorn.mvpiframework.basemodels.Event
import com.coreyhorn.mvpiframework.basemodels.Result
import com.coreyhorn.mvpiframework.basemodels.State

sealed class ExampleEvent: Event() {
    class TestEvent: ExampleEvent()
}

sealed class ExampleAction: Action() {
    class TestAction: ExampleAction()
}

sealed class ExampleResult: Result() {
    class TestResult: ExampleResult()
}

data class ExampleState(val whatever: String): State()