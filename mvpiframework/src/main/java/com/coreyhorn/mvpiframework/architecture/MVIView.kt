package com.coreyhorn.mvpiframework.architecture

import android.util.Log
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.coreyhorn.mvpiframework.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.ReplaySubject

interface MVIView<E: MVIEvent, R: MVIResult, S: MVIState> {

    var events: ReplaySubject<E>
    var presenter: MVIViewModel<E, R, S>?
    var disposables: CompositeDisposable

    var rootView: View?

    var attached: Boolean

    var lifecycleOwner: LifecycleOwner

    fun presenterProvider(): MVIViewModel<E, R, S>

    fun renderState(view: View, state: S)
    fun setupViewBindings(view: View)
    fun initialState(): S

    // Called when the view is inflated and we have our initial / saved state provided by the Activity / Fragment
    fun viewReady(view: View, lifecycleOwner: LifecycleOwner, presenter: MVIViewModel<E, R, S>) {
        this.presenter = presenter
        this.lifecycleOwner = lifecycleOwner
        this.rootView = view

        attachIfReady()
    }

    fun detachView() {
        presenter?.states?.removeObservers(lifecycleOwner)
        presenter?.detachEvents()
        disposables.clear()
        events = ReplaySubject.create()
        attached = false
    }

    fun attachIfReady() {
        if (readyToAttach()) {
            attached = true
            disposables.clear()
            disposables = CompositeDisposable()

            rootView?.let { it.post { setupViewBindings(it) } }
            presenter?.let {
                it.states.removeObservers(lifecycleOwner)
                Log.d("stuff", "attaching events and states")
                it.attachEvents(events, initialState())
                it.states
                        .observe(lifecycleOwner, object: Observer<S> {
                            override fun onChanged(state: S) {
                                rootView?.let { view ->
                                    view.post {
                                        if (attached) {
                                            renderState(view, state)
                                        }
                                    }
                                }
                            }
                        })

                events.doOnNext { event ->
                    if (MVPISettings.loggingEnabled) {
                        Log.d(LOGGING_TAG, event?.toString())
                    }}.subscribe().disposeWith(disposables)
            }
        }
    }

    private fun readyToAttach(): Boolean = !attached && presenter != null && rootView != null
}