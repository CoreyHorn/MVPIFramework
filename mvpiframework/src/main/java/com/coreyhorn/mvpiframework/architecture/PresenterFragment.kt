package com.coreyhorn.mvpiframework.architecture

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.util.Log
import com.coreyhorn.mvpiframework.LOGGING_TAG
import com.coreyhorn.mvpiframework.MVPISettings
import com.coreyhorn.mvpiframework.basemodels.Action
import com.coreyhorn.mvpiframework.basemodels.Event
import com.coreyhorn.mvpiframework.basemodels.Result
import com.coreyhorn.mvpiframework.basemodels.State
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.ReplaySubject

abstract class PresenterFragment<E : Event, A : Action, R : Result, S : State> : Fragment(), PresenterView<E, A, R, S> {

    override final val events: ReplaySubject<E> = ReplaySubject.create()

    override var presenter: Presenter<E, A, R, S>? = null
    override var disposables = CompositeDisposable()
    override var attachAttempted = false

    init {
        events.doOnNext {
            if (MVPISettings.loggingEnabled) {
                Log.d(LOGGING_TAG, it.toString())
            }
        }
        .subscribe()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializePresenter(loaderManager)
    }

    override fun onResume() {
        super.onResume()
        attachStream()
        setupViewBindings()
    }

    override fun onPause() {
        detachStream()
        super.onPause()
    }

    override fun initializeLoader(loaderCallbacks: LoaderManager.LoaderCallbacks<Presenter<E, A, R, S>>) {
        loaderManager.initLoader(loaderId(), null, loaderCallbacks)
    }
}