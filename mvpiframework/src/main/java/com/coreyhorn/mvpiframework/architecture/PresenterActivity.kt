package com.coreyhorn.mvpiframework.architecture

import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v7.app.AppCompatActivity
import com.coreyhorn.mvpiframework.basemodels.Action
import com.coreyhorn.mvpiframework.basemodels.Event
import com.coreyhorn.mvpiframework.basemodels.Result
import com.coreyhorn.mvpiframework.basemodels.State
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.ReplaySubject

abstract class PresenterActivity<E : Event, A : Action, R : Result, S : State> : AppCompatActivity(), PresenterView<E, A, R, S> {

    override final val events: ReplaySubject<E> = ReplaySubject.create()

    override var presenter: Presenter<E, A, R, S>? = null
    override var disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializePresenter(supportLoaderManager)
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
        supportLoaderManager.initLoader(loaderId(), null, loaderCallbacks)
    }
}