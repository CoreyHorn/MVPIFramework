package com.coreyhorn.mvpiframework.architecture

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.util.Log
import com.coreyhorn.mvpiframework.LOGGING_TAG
import com.coreyhorn.mvpiframework.MVPISettings
import com.coreyhorn.mvpiframework.basemodels.Action
import com.coreyhorn.mvpiframework.basemodels.Event
import com.coreyhorn.mvpiframework.basemodels.Result
import com.coreyhorn.mvpiframework.basemodels.State
import com.coreyhorn.mvpiframework.disposeWith
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

interface PresenterView<E : Event, A : Action, R : Result, S : State> {

    val events: PublishSubject<E>

    var presenter: Presenter<E, A, R, S>?
    var disposables: CompositeDisposable
    var attachAttempted: Boolean

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
        attachAttempted = true
        presenter?.let {
            it.attachEventStream(events
                    .doOnNext {
                        if (MVPISettings.loggingEnabled) {
                            Log.d(LOGGING_TAG, it.toString())}
                    })
            it.states()
                    .subscribe { renderViewStateOnMainThread(it) }
                    .disposeWith(disposables)
        }
    }

    fun detachStream() {
        attachAttempted = false
        disposables.clear()
        disposables = CompositeDisposable()
        presenter?.detachEventStream()
    }

    fun onPresenterAvailable(presenter: Presenter<E, A, R, S>) {
        if (attachAttempted) {
            attachStream()
        }
    }

    private fun renderViewStateOnMainThread(state: S) {
        val mainHandler = Handler(Looper.getMainLooper())
        val renderRunnable = { renderViewState(state) }
        mainHandler.post(renderRunnable)
    }

    fun initializeLoader(loaderCallbacks: LoaderManager.LoaderCallbacks<Presenter<E, A, R, S>>)
    fun getContext(): Context?
    fun loaderId(): Int
    fun presenterFactory(): PresenterFactory<Presenter<E, A, R, S>>
    fun renderViewState(state: S)
    fun setupViewBindings()
}