package com.coreyhorn.mvpiframeworkexample

import com.coreyhorn.mvpiframework.MVIEvent
import com.coreyhorn.mvpiframework.MVIResult
import com.coreyhorn.mvpiframework.MVIState

sealed class ExampleEvent: MVIEvent() {
    class TestEvent: ExampleEvent()
    class ChangeText: ExampleEvent()
}

sealed class ExampleResult: MVIResult() {
    class TestResult: ExampleResult()
    data class ChangedText(val text: String): ExampleResult()
}

data class ExampleState(val whatever: String): MVIState()