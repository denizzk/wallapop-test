package com.dkarakaya.core.util

import io.reactivex.observers.BaseTestConsumer
import io.reactivex.observers.TestObserver

fun <T> TestObserver<T>.await(atLeast: Int, timeout: Long = 500): TestObserver<T> {
    return this.awaitCount(atLeast, BaseTestConsumer.TestWaitStrategy.SPIN, timeout)
}
