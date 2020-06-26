package com.dkarakaya.core.util

import io.reactivex.observers.BaseTestConsumer
import io.reactivex.observers.TestObserver

fun <T> TestObserver<T>.await(atLeast: Int): TestObserver<T> {
    return this.awaitCount(atLeast, BaseTestConsumer.TestWaitStrategy.SPIN, 500)
}
