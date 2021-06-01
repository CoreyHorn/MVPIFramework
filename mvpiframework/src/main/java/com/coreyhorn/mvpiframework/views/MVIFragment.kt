package com.coreyhorn.mvpiframework.views

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import com.coreyhorn.mvpiframework.MVIEvent
import com.coreyhorn.mvpiframework.MVIResult
import com.coreyhorn.mvpiframework.MVIState
import com.coreyhorn.mvpiframework.architecture.MVIView
import com.coreyhorn.mvpiframework.architecture.MVIViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.ReplaySubject

abstract class MVIFragment<E: MVIEvent, R: MVIResult, S: MVIState>: Fragment(), MVIView<E, R, S> {

    override var events: ReplaySubject<E> = ReplaySubject.create()
    override var presenter: MVIViewModel<E, R, S>? = null
    override var disposables: CompositeDisposable = CompositeDisposable()
    override var rootView: View? = null
    override var attached: Boolean = false

    /**
     * In subclass, make sure initialState() function will return the proper value before
     * calling super.onViewCreated()
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        view.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                viewReady(view, this@MVIFragment)
            }
        })
    }

    override fun onStart() {
        super.onStart()
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
}