package com.coreyhorn.mvpiframework.views

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.LoaderManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.coreyhorn.mvpiframework.basemodels.Event
import com.coreyhorn.mvpiframework.basemodels.Result
import com.coreyhorn.mvpiframework.basemodels.State
import com.coreyhorn.mvpiframework.architecture.MVIPresenter
import com.coreyhorn.mvpiframework.architecture.MVIView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.ReplaySubject

abstract class MVIActivity<E : Event, R : Result, S : State> : AppCompatActivity(), MVIView<E, R, S> {

    override var events: ReplaySubject<E> = ReplaySubject.create()

    override var presenter: MVIPresenter<E, R, S>? = null
    override var disposables = CompositeDisposable()
    override var attached = false
    override var rootView: View? = null

    override fun onStart() {
        super.onStart()
        attachIfReady()
        val view = findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        view.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                viewReady(view)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        attachIfReady()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        detachView()
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onStop() {
        detachView()
        super.onStop()
    }

    override fun onDestroy() {
        rootView = null
        presenter?.destroy()
        super.onDestroy()
    }

    override fun initializeLoader(loaderCallbacks: LoaderManager.LoaderCallbacks<MVIPresenter<E, R, S>>) {
        supportLoaderManager.initLoader(loaderId(), null, loaderCallbacks)
    }
}
