package com.coreyhorn.mvpiframework.architecture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.coreyhorn.mvpiframework.MVIEvent
import com.coreyhorn.mvpiframework.MVIResult
import com.coreyhorn.mvpiframework.MVIState
import com.coreyhorn.mvpiframework.disposeWith
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

abstract class MVIViewModel<E: MVIEvent, R: MVIResult, S: MVIState>: ViewModel(), Presenter<E, R, S> {

    private val states: MutableLiveData<S> = MutableLiveData()
    private val events: PublishSubject<E> = PublishSubject.create()

    private var eventDisposables = CompositeDisposable()
    private var interactorDisposables = CompositeDisposable()

    private var isInteractorConnected = false
    private var isViewConnected = false

    private var interactor: MVIInteractor<E, R>? = null

    override fun onCleared() {
        eventDisposables.dispose()
        interactorDisposables.dispose()
        interactor?.destroy()
        interactor = null
        isInteractorConnected = false
        isViewConnected = false

        super.onCleared()
    }

    fun states(): LiveData<S> = states

    // Called before streams are connected to initialize interactor.
    // This will allow any asynchronous tasks to be started early.
    fun conditionallyInitializeInteractor(): MVIInteractor<E, R> {
        if (interactor == null) {
            with (provideInteractor(events)) {
                interactor = this
                return this
            }
        } else {
            return interactor!!
        }
    }

    fun attachEvents(events: Observable<E>, state: S) {
        if (!isInteractorConnected) {
            isInteractorConnected = true
            with (conditionallyInitializeInteractor()) {
                /* Uses the current state value as seed unless we don't have one.
                *  Else seed with the state provided by this function.
                *  Allows passing in data from savedInstanceState that
                *  we can use if the ViewModel has been recreated */
                this.results().scan(states.value?: state, ::resultToState)
                        .subscribe { states.value = it }
                        .disposeWith(interactorDisposables)
            }
        }

        if (!isViewConnected) {
            isViewConnected = true
            eventDisposables.clear()

            events.subscribe { this.events.onNext(it) }
                    .disposeWith(eventDisposables)
        }
    }

    fun detachView() {
        isViewConnected = false
        eventDisposables.clear()
    }

}

private interface Presenter<E: MVIEvent, R: MVIResult, S: MVIState> {
    fun provideInteractor(events: Observable<E>): MVIInteractor<E, R>
    fun resultToState(previousState: S, result: R): S
}