package com.coreyhorn.mvpiframework.architecture

import android.util.Log
import com.coreyhorn.mvpiframework.LOGGING_TAG
import com.coreyhorn.mvpiframework.MVPISettings
import com.coreyhorn.mvpiframework.basemodels.Action
import com.coreyhorn.mvpiframework.basemodels.Event
import com.coreyhorn.mvpiframework.basemodels.Result
import com.coreyhorn.mvpiframework.basemodels.State
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

abstract class Presenter<E : Event, A : Action, R : Result, S : State> {

    protected val actions: PublishSubject<A> = PublishSubject.create()
    protected val states: BehaviorSubject<S> = BehaviorSubject.create()

    protected var eventDisposables = CompositeDisposable()

    fun actions(): Observable<A> = actions.doOnNext {
        if (MVPISettings.loggingEnabled) {
            Log.d(LOGGING_TAG, it.toString())
        }
    }

    fun states(): Observable<S> = states.doOnNext {
        if (MVPISettings.loggingEnabled) {
            Log.d(LOGGING_TAG, it.toString())
        }
    }

    open fun attachEventStream(events: Observable<E>) {
        eventDisposables.clear()
        eventDisposables = CompositeDisposable()
    }

    fun detachEventStream() {
        eventDisposables.clear()
        eventDisposables = CompositeDisposable()
    }

    abstract fun attachResultStream(results: Observable<R>)

}



