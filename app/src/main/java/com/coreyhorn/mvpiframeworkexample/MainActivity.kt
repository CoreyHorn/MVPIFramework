package com.coreyhorn.mvpiframeworkexample

import android.content.Context
import android.os.Bundle
import com.coreyhorn.mvpiframework.architecture.PresenterActivity
import com.coreyhorn.mvpiframework.architecture.PresenterFactory

class MainActivity : PresenterActivity<ExampleEvent, ExampleAction, ExampleResult, ExampleState>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        events.onNext(ExampleEvent.TestEvent())
    }

    //Should be a unique id
    override fun loaderId() = 1

    override fun getContext(): Context? = baseContext

    override fun presenterFactory(): PresenterFactory<ExamplePresenter> = object: PresenterFactory<ExamplePresenter>() {
        override fun create() = ExamplePresenter()
    }

    override fun renderViewState(state: ExampleState) {

    }

    override fun setupViewBindings() {

    }
}
