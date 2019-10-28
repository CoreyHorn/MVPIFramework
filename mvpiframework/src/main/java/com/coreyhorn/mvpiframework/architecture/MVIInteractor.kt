package com.coreyhorn.mvpiframework.architecture

import com.coreyhorn.mvpiframework.MVIEvent
import com.coreyhorn.mvpiframework.MVIResult
import com.coreyhorn.mvpiframework.disposeWith
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.ReplaySubject

abstract class MVIInteractor<E: MVIEvent, R: MVIResult>(events: Observable<E>): Interactor<E, R> {

    private val results: ReplaySubject<R> = ReplaySubject.create()

    val disposables = CompositeDisposable()

    init {
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