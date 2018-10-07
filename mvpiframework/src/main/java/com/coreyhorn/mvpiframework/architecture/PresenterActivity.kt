package com.coreyhorn.mvpiframework.architecture

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.LoaderManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.coreyhorn.mvpiframework.basemodels.Action
import com.coreyhorn.mvpiframework.basemodels.Event
import com.coreyhorn.mvpiframework.basemodels.Result
import com.coreyhorn.mvpiframework.basemodels.State
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.ReplaySubject

abstract class PresenterActivity<E : Event, A : Action, R : Result, S : State> : AppCompatActivity(), PresenterView<E, A, R, S> {

    override var events: ReplaySubject<E> = ReplaySubject.create()

    override var presenter: Presenter<E, A, R, S>? = null
    override var disposables = CompositeDisposable()
    override var attached = false
    override var rootView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializePresenter(supportLoaderManager)
    }

    override fun onStart() {
        super.onStart()
        attachStream()
        val view = findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        view.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                setView(view)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        attachStream()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        detachStream()
        super.onSaveInstanceState(outState, outPersistentState)
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
        supportLoaderManager.initLoader(loaderId(), null, loaderCallbacks)
    }
}