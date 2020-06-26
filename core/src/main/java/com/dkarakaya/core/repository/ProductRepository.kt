package com.dkarakaya.core.repository

import com.dkarakaya.core.model.ProductRemoteModel
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
