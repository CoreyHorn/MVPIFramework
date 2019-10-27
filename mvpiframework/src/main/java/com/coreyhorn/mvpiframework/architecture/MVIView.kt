package com.coreyhorn.mvpiframework.architecture

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.coreyhorn.mvpiframework.*
import com.coreyhorn.mvpiframework.viewmodel.MVIViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.ReplaySubject

interface MVIView<E: MVIEvent, R: MVIResult, S: MVIState> {

    var events: ReplaySubject<E>
    var presenter: MVIViewModel<E, R, S>?
    var disposables: CompositeDisposable

    var rootView: View?

    var attached: Boolean

    var lifecycleOwner: LifecycleOwner

    fun getContext(): Context?
    fun presenterProvider(): MVIViewModel<E, R, S>

    fun renderState(view: View, state: S)
    fun setupViewBindings(view: View)
    fun initialState(): S

    // Called when the view is inflated and we have our initial / saved state provided by the Activity / Fragment

    /**
     * Should be called as soon as the view has been inflated and can be modified / bound to.
     */
    fun viewReady(view: View, lifecycleOwner: LifecycleOwner, presenter: MVIViewModel<E, R, S>) {
        this.presenter = presenter
        this.lifecycleOwner = lifecycleOwner
        rootView = view
        attachIfReady()
    }

    fun detachView() {
        presenter?.detachEvents()
        disposables.clear()
        events = ReplaySubject.create()
        attached = false
    }

    fun attachIfReady() {
        if (readyToAttach()) {
            disposables.clear()
            disposables = CompositeDisposable()

            rootView?.let { it.post { setupViewBindings(it) } }
            presenter?.let {
                it.attachEvents(events, initialState())
                it.states
                        .observe(lifecycleOwner, object: Observer<S> {
                            override fun onChanged(state: S) {
                                rootView?.let { view ->
                                    view.post { if (attached) renderState(view, state) }
                                }
                            }
                        })

                events.doOnNext { event ->
                    if (MVPISettings.loggingEnabled) {
                        Log.d(LOGGING_TAG, event?.toString())
                    }}.subscribe().disposeWith(disposables)
            }
            attached = true
        }
    }

    private fun readyToAttach(): Boolean = !attached && presenter != null && rootView != null
}