package com.coreyhorn.mvpiframework.architecture

abstract class PresenterFactory<out P> {
    abstract fun create(): P
}