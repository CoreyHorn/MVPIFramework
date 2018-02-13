package com.coreyhorn.mvpiframework

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

const val LOGGING_TAG = "MVPI"

fun Disposable.disposeWith(compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(this)
}