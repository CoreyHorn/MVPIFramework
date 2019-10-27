package com.coreyhorn.mvpiframework.architecture

import android.content.Context
import android.support.v4.content.Loader
import android.util.Log

import com.coreyhorn.mvpiframework.basemodels.Event
import com.coreyhorn.mvpiframework.basemodels.Result
import com.coreyhorn.mvpiframework.basemodels.State

class PresenterLoader<E : Event, R : Result, S : State>(
        context: Context, val presenterFactory: (initialState: S) -> MVIPresenter<E, R, S>,
        private val initialState: S) : Loader<MVIPresenter<E, R, S>>(context) {

    var presenter: MVIPresenter<E, R, S>? = null

    override fun onStartLoading() {
        super.onStartLoading()

        Log.d("stuff", "onStartLoading: " + this)

        if (presenter != null) {
            deliverResult(presenter)
            return
        }

        forceLoad()
    }

    override fun forceLoad() {
        super.forceLoad()

        presenter = presenterFactory(initialState)
        deliverResult(presenter)
    }

    override fun onReset() {
        super.onReset()

        Log.d("stuff", "clearing presenter from loader")
        presenter?.destroy()
        presenter = null
    }
}