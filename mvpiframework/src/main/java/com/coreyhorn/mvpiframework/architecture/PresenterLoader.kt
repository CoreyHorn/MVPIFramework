package com.coreyhorn.mvpiframework.architecture

import android.content.Context
import android.support.v4.content.Loader
import com.coreyhorn.mvpiframework.basemodels.Action
import com.coreyhorn.mvpiframework.basemodels.Event
import com.coreyhorn.mvpiframework.basemodels.Result
import com.coreyhorn.mvpiframework.basemodels.State

class PresenterLoader<E : Event, A : Action, R : Result, S : State>(
        context: Context, private val factory: PresenterFactory<Presenter<E, A, R, S>>) : Loader<Presenter<E, A, R, S>>(context) {

    var presenter: Presenter<E, A, R, S>? = null

    override fun onStartLoading() {
        super.onStartLoading()

        if (presenter != null) {
            deliverResult(presenter)
            return
        }

        forceLoad()
    }

    override fun forceLoad() {
        super.forceLoad()

        presenter = factory.create()
        deliverResult(presenter)
    }

    override fun onReset() {
        super.onReset()

        presenter = null
    }
}