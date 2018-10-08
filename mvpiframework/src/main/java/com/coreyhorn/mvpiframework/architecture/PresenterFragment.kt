package com.coreyhorn.mvpiframework.architecture

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.view.View
import android.view.ViewTreeObserver
import com.coreyhorn.mvpiframework.basemodels.Action
import com.coreyhorn.mvpiframework.basemodels.Event
import com.coreyhorn.mvpiframework.basemodels.Result
import com.coreyhorn.mvpiframework.basemodels.State
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.ReplaySubject

abstract class PresenterFragment<E : Event, A : Action, R : Result, S : State> : Fragment(), PresenterView<E, A, R, S> {

    override var events: ReplaySubject<E> = ReplaySubject.create()

    override var presenter: Presenter<E, A, R, S>? = null
    override var disposables = CompositeDisposable()
    override var attached = false
    override var rootView: View? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializePresenter(loaderManager)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                setView(view)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        attachStream()
    }

    override fun onResume() {
        super.onResume()
        attachStream()
    }

    override fun onStop() {
        detachStream()
        super.onStop()
    }

    override fun onDestroy() {
        rootView = null
        super.onDestroy()
    }

    override fun initializeLoader(loaderCallbacks: LoaderManager.LoaderCallbacks<Presenter<E, A, R, S>>) {
        loaderManager.initLoader(loaderId(), null, loaderCallbacks)
    }
}