package com.coreyhorn.mvpiframeworkexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.coreyhorn.mvpiframework.architecture.MVIPresenter
import com.coreyhorn.mvpiframework.views.MVIFragment
import kotlinx.android.synthetic.main.fragment_example.view.*

class ExampleFragment: MVIFragment<ExampleEvent, ExampleResult, ExampleState>() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_example, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializePresenter(loaderManager, ExampleState("initial string"))
    }

    override fun loaderId() = 2

    override fun presenterProvider(initialState: ExampleState): MVIPresenter<ExampleEvent, ExampleResult, ExampleState> {
        return ExamplePresenter(initialState)
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