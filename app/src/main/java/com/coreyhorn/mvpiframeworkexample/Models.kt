package com.coreyhorn.mvpiframeworkexample

import com.coreyhorn.mvpiframework.basemodels.Event
import com.coreyhorn.mvpiframework.basemodels.Result
import com.coreyhorn.mvpiframework.basemodels.State

sealed class ExampleEvent: Event() {
    class TestEvent: ExampleEvent()
    class ChangeText: ExampleEvent()
}

sealed class ExampleResult: Result() {
    class TestResult: ExampleResult()
    data class ChangedText(val text: String): ExampleResult()
}

data class ExampleState(val whatever: String): State()