package com.coreyhorn.mvpiframework.architecture

import android.content.Context
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.util.Log
import android.view.View
import com.coreyhorn.mvpiframework.LOGGING_TAG
import com.coreyhorn.mvpiframework.MVPISettings
import com.coreyhorn.mvpiframework.basemodels.Event
import com.coreyhorn.mvpiframework.basemodels.Result
import com.coreyhorn.mvpiframework.basemodels.State
import com.coreyhorn.mvpiframework.disposeWith
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.ReplaySubject

interface MVIView<E: Event, R: Result, S: State> {

    var events: ReplaySubject<E>
    var presenter: MVIPresenter<E, R, S>?
    var disposables: CompositeDisposable
    var rootView: View?
    var attached: Boolean

    fun getContext(): Context?
    fun loaderId(): Int
    fun initializeLoader(loaderCallbacks: LoaderManager.LoaderCallbacks<MVIPresenter<E, R, S>>)
    fun presenterProvider(initialState: S): MVIPresenter<E, R, S>

    fun renderState(view: View, state: S)
    fun setupViewBindings(view: View)

    /**
     * Should be called as soon as a LoaderManager and an initial State is ready.
     * This can be used to construct an initial state from savedInstanceState
     * to handle state restoration through process death.
     *
     * Whether you do that or not, this library will handle configuration changes
     * similar to ViewModel and ignore initialState if we already have one.
     */
    fun initializePresenter(loaderManager: LoaderManager, initialState: S) {
        @Suppress("UNCHECKED_CAST")
        val loader = loaderManager.getLoader<MVIPresenter<E, R, S>>(loaderId()) as? PresenterLoader<E, R, S>

        val callbacks = object: LoaderManager.LoaderCallbacks<MVIPresenter<E, R, S>> {
            override fun onCreateLoader(id: Int, args: Bundle?) = PresenterLoader(getContext()!!, ::presenterProvider, initialState)

            override fun onLoadFinished(loader: Loader<MVIPresenter<E, R, S>>?, data: MVIPresenter<E, R, S>?) {
                data?.preWarm()
                presenter = data
                attachIfReady()
            }

            override fun onLoaderReset(loader: Loader<MVIPresenter<E, R, S>>?) { presenter = null }
        }

        if (loader == null) {
            initializeLoader(callbacks)
        } else {
            loader.presenter?.let {
                presenter?.preWarm()
                presenter = it
                attachIfReady()
            }
        }
    }

    /**
     * Should be called as soon as the view has been inflated and can be modified / bound to.
     */
    fun viewReady(view: View) {
        rootView = view
        attachIfReady()
    }

    fun detachView() {
        presenter?.disconnectEvents()
        disposables.clear()
        attached = false
    }

    fun attachIfReady() {
        if (readyToAttach()) {
            rootView?.let { it.post { setupViewBindings(it) } }
            disposables = CompositeDisposable()

            presenter?.let {
                it.connectEvents(events)
                it.states().observeOn(AndroidSchedulers.mainThread())
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