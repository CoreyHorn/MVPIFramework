package com.coreyhorn.mvpiframeworkexample

import android.content.Context
import android.os.Bundle
import android.view.View
import com.coreyhorn.mvpiframework.MVPISettings
import com.coreyhorn.mvpiframework.architecture.MVIPresenter
import com.coreyhorn.mvpiframework.views.MVIActivity

class MainActivity : MVIActivity<ExampleEvent, ExampleResult, ExampleState>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MVPISettings.loggingEnabled = true
        setContentView(R.layout.activity_main)
        events.onNext(ExampleEvent.TestEvent())

        if (savedInstanceState == null) {

            val fragment = ExampleFragment()

            supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, fragment)
                    .commit()
        }
    }

    //Should be a unique id
    override fun loaderId() = 1

    override fun getContext(): Context? = baseContext

    override fun presenterProvider(initialState: ExampleState): MVIPresenter<ExampleEvent, ExampleResult, ExampleState> {
        return ExamplePresenter(initialState)
    }

    override fun renderState(view: View, state: ExampleState) {

    }

    override fun setupViewBindings(view: View) {

    }
}
