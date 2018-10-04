package com.coreyhorn.mvpiframework.architecture

import android.content.Context
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.util.Log
import android.view.View
import com.coreyhorn.mvpiframework.LOGGING_TAG
import com.coreyhorn.mvpiframework.MVPISettings
import com.coreyhorn.mvpiframework.basemodels.Action
import com.coreyhorn.mvpiframework.basemodels.Event
import com.coreyhorn.mvpiframework.basemodels.Result
import com.coreyhorn.mvpiframework.basemodels.State
import com.coreyhorn.mvpiframework.disposeWith
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.ReplaySubject

interface PresenterView<E : Event, A : Action, R : Result, S : State> {

    var events: ReplaySubject<E>

    var presenter: Presenter<E, A, R, S>?
    var disposables: CompositeDisposable

    var attached: Boolean
    var rootView: View?

    val loaderCallbacks: LoaderManager.LoaderCallbacks<Presenter<E, A, R, S>>
        get() = object : LoaderManager.LoaderCallbacks<Presenter<E, A, R, S>> {
            override fun onLoaderReset(loader: Loader<Presenter<E, A, R, S>>?) {
                presenter = null
            }

            override fun onLoadFinished(loader: Loader<Presenter<E, A, R, S>>?, data: Presenter<E, A, R, S>) {
                presenter = data
                onPresenterAvailable(data)
            }

            override fun onCreateLoader(id: Int, args: Bundle?): Loader<Presenter<E, A, R, S>> = PresenterLoader(getContext()!!, presenterFactory())
        }

    fun initializePresenter(loaderManager: LoaderManager) {
        @Suppress("UNCHECKED_CAST")
        val loader = loaderManager.getLoader<Presenter<E, A, R, S>>(loaderId()) as? PresenterLoader<E, A, R, S>

        if (loader == null) {
            initializeLoader(loaderCallbacks)
        } else {
            loader.presenter?.let {
                presenter = it
                onPresenterAvailable(it)
            }
        }
    }

    fun attachStream() {
        attachIfReady()
    }

    fun detachStream() {
        disposables.clear()
        disposables = CompositeDisposable()
        presenter?.detachEventStream()
        events = ReplaySubject.create()
        attached = false
    }

    fun onPresenterAvailable(presenter: Presenter<E, A, R, S>) {
        this.presenter = presenter
        attachIfReady()
    }

    fun initializeLoader(loaderCallbacks: LoaderManager.LoaderCallbacks<Presenter<E, A, R, S>>)
    fun getContext(): Context?
    fun loaderId(): Int
    fun presenterFactory(): PresenterFactory<Presenter<E, A, R, S>>
    fun renderViewState(view: View, state: S)
    fun setupViewBindings(view: View)

    fun setView(view: View) {
        rootView = view
        attachIfReady()
    }

    private fun attachIfReady() {
        if (readyToAttach()) {
            disposables = CompositeDisposable()
            presenter?.let {
                it.attachEventStream(events)
                it.states().observeOn(AndroidSchedulers.mainThread())
                        .subscribe { state -> rootView?.let { view -> view.post { renderViewState(view, state) } } }
                        .disposeWith(disposables)

                events.doOnNext { event ->
                    if (MVPISettings.loggingEnabled) {
                        Log.d(LOGGING_TAG, event.toString())
                    }}.subscribe().disposeWith(disposables)
            }
            attached = true
        }
    }

    private fun readyToAttach(): Boolean = !attached && presenter != null && rootView != null
}