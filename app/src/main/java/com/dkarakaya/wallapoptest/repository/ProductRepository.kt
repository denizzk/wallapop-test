package com.dkarakaya.wallapoptest.repository

import com.dkarakaya.wallapoptest.model.remote.ProductRemoteModel
import io.reactivex.Observable
import retrofit2.http.GET

interface ProductRepository {

    @GET("items.json")
    fun getProduct(): Observable<List<ProductRemoteModel>>

    companion object {
        const val BASE_URL =
            "https://raw.githubusercontent.com/Wallapop/Wallapop-Android-Test-Resources/master/"
    }
}
