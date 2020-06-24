package com.dkarakaya.wallapoptest.repository

import com.dkarakaya.wallapoptest.model.Product
import io.reactivex.Observable
import retrofit2.http.GET

interface ProductRepository {

    @GET("items.json")
    fun getProduct(): Observable<List<Product>>

    companion object {
        const val BASE_URL =
            "https://raw.githubusercontent.com/Wallapop/Wallapop-Android-Test-Resources/master/"
    }
}
