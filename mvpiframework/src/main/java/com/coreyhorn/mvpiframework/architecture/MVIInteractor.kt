package com.coreyhorn.mvpiframework.architecture

import com.coreyhorn.mvpiframework.MVIEvent
import com.coreyhorn.mvpiframework.MVIResult
import com.coreyhorn.mvpiframework.disposeWith
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.ReplaySubject

abstract class MVIInteractor<E: MVIEvent, R: MVIResult>(private val events: Observable<E>): Interactor<E, R> {

    private val results: ReplaySubject<R> = ReplaySubject.create()

    val disposables = CompositeDisposable()

    open fun connect() {
        events.map { eventToResult(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { pushResult(it) }
                .disposeWith(disposables)
    }

    fun results(): Observable<R> = results

    open fun destroy() {
        disposables.clear()
    }

    protected fun pushResult(result: R) {
        results.onNext(result)
    }
}

private interface Interactor<E: MVIEvent, R: MVIResult> {
    fun eventToResult(event: E): R
}