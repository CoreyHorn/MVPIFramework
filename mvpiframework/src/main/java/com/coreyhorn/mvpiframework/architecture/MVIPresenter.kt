package com.coreyhorn.mvpiframework.architecture

import android.util.Log
import com.coreyhorn.mvpiframework.LOGGING_TAG
import com.coreyhorn.mvpiframework.MVPISettings
import com.coreyhorn.mvpiframework.basemodels.Event
import com.coreyhorn.mvpiframework.basemodels.Result
import com.coreyhorn.mvpiframework.basemodels.State
import com.coreyhorn.mvpiframework.disposeWith
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

abstract class MVIPresenter<E: Event, R: Result, S: State>(private val initialState: S): Presenter<E, R, S> {
    private val states: BehaviorSubject<S> = BehaviorSubject.create()
    private val events: BehaviorSubject<E> = BehaviorSubject.create()

    private var eventDisposables = CompositeDisposable()

    private var interactor: MVIInteractor<E, R>? = null

    /**
     * Can be called as early as you receive an MVIPresenter instance to
     * initialize the Interactor and therefore start any network requests, etc
     * before the view is ready. Benefits depend on how long the view takes to
     * layout.
     *
     * Otherwise the Interactor will be connected when connectEvents() is called.
     *
     * This is a side effect of relying on the subclass implementation of
     * provideInteractor(). Is there a better way?
     */
    fun preWarm() {
        connectInteractor()
    }

    fun connectEvents(events: Observable<E>) {
        connectInteractor()

        eventDisposables.clear()
        eventDisposables = CompositeDisposable()

        events
            .subscribe { this.events.onNext(it) }
            .disposeWith(eventDisposables)

    }

    /**
     * Called when you want to stop processing Events. This can vary depending on use case
     * but we have defaults in the MVIActivity and MVIFragment convenience classes.
     */
    fun disconnectEvents() {
        eventDisposables.clear()
    }

    fun states(): Observable<S> = states.doOnNext {
        if (MVPISettings.loggingEnabled) {
            Log.d(LOGGING_TAG, it?.toString())
        }
    }

    fun destroy() {
        disconnectEvents()
        interactor?.destroy()
        interactor = null
    }

    private fun connectInteractor() {
        if (interactor == null) {
            val interactor = provideInteractor(events)

            interactor.results()
                    .scan(initialState, this::resultToState)
                    .subscribe(states)

            this.interactor = interactor
        }
    }
}

private interface Presenter<E: Event, R: Result, S: State> {
    fun provideInteractor(events: Observable<E>): MVIInteractor<E, R>
    fun resultToState(previousState: S, result: R): S
}