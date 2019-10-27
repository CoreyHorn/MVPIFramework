package com.coreyhorn.mvpiframework.views

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import com.coreyhorn.mvpiframework.MVIEvent
import com.coreyhorn.mvpiframework.MVIResult
import com.coreyhorn.mvpiframework.MVIState
import com.coreyhorn.mvpiframework.architecture.MVIView
import com.coreyhorn.mvpiframework.viewmodel.MVIViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.ReplaySubject

abstract class MVIActivity<E : MVIEvent, R : MVIResult, S : MVIState> : AppCompatActivity(), MVIView<E, R, S> {

    override var events: ReplaySubject<E> = ReplaySubject.create()

    override var presenter: MVIViewModel<E, R, S>? = null
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
                viewReady(view, this@MVIActivity, presenterProvider())
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
        super.onDestroy()
    }
}
