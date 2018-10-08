package com.coreyhorn.mvpiframeworkexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.coreyhorn.mvpiframework.architecture.PresenterFactory
import com.coreyhorn.mvpiframework.architecture.PresenterFragment
import kotlinx.android.synthetic.main.fragment_example.view.*

class ExampleFragment: PresenterFragment<ExampleEvent, ExampleAction, ExampleResult, ExampleState>() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_example, container, false)
    }

    override fun loaderId() = 2

    override fun presenterFactory(): PresenterFactory<ExamplePresenter> = object: PresenterFactory<ExamplePresenter>() {
        override fun create() = ExamplePresenter()
    }

    override fun renderViewState(view: View, state: ExampleState) {
        view.fragmentText.text = state.whatever
    }

    override fun setupViewBindings(view: View) {
        view.changeText.setOnClickListener { events.onNext(ExampleEvent.TestEvent()) }
    }
}