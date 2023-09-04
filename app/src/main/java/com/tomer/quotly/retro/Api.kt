package com.tomer.quotly.retro

import com.tomer.quotly.modals.QuotesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET("random")
   fun getQuotes(
        @Query("tag") tag: String,
        @Query("limit") lim: Int = 10
    ): Call<QuotesResponse>

}