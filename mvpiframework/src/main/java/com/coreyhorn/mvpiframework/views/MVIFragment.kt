package com.coreyhorn.mvpiframework.views

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.view.View
import android.view.ViewTreeObserver
import com.coreyhorn.mvpiframework.architecture.MVIPresenter
import com.coreyhorn.mvpiframework.architecture.MVIView
import com.coreyhorn.mvpiframework.basemodels.Event
import com.coreyhorn.mvpiframework.basemodels.Result
import com.coreyhorn.mvpiframework.basemodels.State
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.ReplaySubject

abstract class MVIFragment<E : Event, R : Result, S : State> : Fragment(), MVIView<E, R, S> {

    override var events: ReplaySubject<E> = ReplaySubject.create()

    override var presenter: MVIPresenter<E, R, S>? = null
    override var disposables = CompositeDisposable()
    override var attached = false
    override var rootView: View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                viewReady(view)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        attachIfReady()
    }

    override fun onResume() {
        super.onResume()
        attachIfReady()
    }

    override fun onStop() {
        detachView()
        super.onStop()
    }

    override fun onDestroy() {
        rootView = null
        super.onDestroy()
    }

    override fun initializeLoader(loaderCallbacks: LoaderManager.LoaderCallbacks<MVIPresenter<E, R, S>>) {
        loaderManager.initLoader(loaderId(), null, loaderCallbacks)
    }
}