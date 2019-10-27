package com.coreyhorn.mvpiframework.architecture

import android.content.Context
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.coreyhorn.mvpiframework.*
import com.coreyhorn.mvpiframework.viewmodel.MVIViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.ReplaySubject

interface MVIView<E: MVIEvent, R: MVIResult, S: MVIState> {

    var events: ReplaySubject<E>
    var presenter: MVIViewModel<E, R, S>?
    var disposables: CompositeDisposable

    var rootView: View?

    var attached: Boolean





    fun getContext(): Context?
//    fun presenterProvider(initialState: S): MVIPresenter<E, R, S>

    fun renderState(view: View, state: S)
    fun setupViewBindings(view: View)
    fun initialState(): S

    // Called when the view is inflated and we have our initial / saved state provided by the Activity / Fragment
    fun ready(view: View, state: S) {
        rootView = view


    }



    /**
     * Should be called as soon as the view has been inflated and can be modified / bound to.
     */
    fun viewReady(view: View) {
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
                it.attachEvents(events)
                it.states
                        .subscribe { state ->
                            rootView?.let { view ->
                                view.post { if (attached) renderState(view, state) } }
                        }
                        .disposeWith(disposables)

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