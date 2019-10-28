package com.coreyhorn.mvpiframeworkexample.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import com.coreyhorn.mvpiframework.architecture.MVIViewModel
import com.coreyhorn.mvpiframework.views.MVIFragment
import com.coreyhorn.mvpiframeworkexample.*
import kotlinx.android.synthetic.main.fragment_example.view.*

class ExampleFragment: MVIFragment<ExampleEvent, ExampleResult, ExampleState>() {

    override var lifecycleOwner: LifecycleOwner = this

    override fun initialState(): ExampleState {
        return ExampleState("initial string")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            Log.d("stuff", "sending event")
            events.onNext(ExampleEvent.ChangeText())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_example, container, false)
    }

    override fun presenterProvider(): MVIViewModel<ExampleEvent, ExampleResult, ExampleState> {
        return ViewModelProviders.of(this@ExampleFragment).get(ExamplePresenter::class.java)
    }

    override fun renderState(view: View, state: ExampleState) {
        view.fragmentText.text = state.whatever
    }

    override fun setupViewBindings(view: View) {
        view.changeText.setOnClickListener {
            events.onNext(ExampleEvent.ChangeText())
        }
    }
}