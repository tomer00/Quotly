package com.tomer.quotly.repo

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.tomer.quotly.modals.QuoteItem
import com.tomer.quotly.modals.QuotesResponse
import com.tomer.quotly.retro.Api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RepoImpl @Inject constructor(private val ret: Api, con: Application, private val gson: Gson) : MainRepo {


    private val defaultQuote by lazy {
        QuoteItem(
            "_cllvgW3qw9C", "Seneca the Younger", "Seneca-the-Younger",
            "\"Most powerful is he who has himself in his own power.\"", "2020-12-19",
            "2023-04-14", 53, arrayListOf()
        )
    }

    private val pref by lazy { con.getSharedPreferences("data", Context.MODE_PRIVATE) }
    private val favPref by lazy { con.getSharedPreferences("fav", Context.MODE_PRIVATE) }


    override fun saveLast(quoteItem: QuoteItem) {
        pref.edit().putString("last", gson.toJson(quoteItem)).apply()
    }

    override fun getLastQuote(): QuoteItem {
        return gson.fromJson(pref.getString("last", gson.toJson(defaultQuote)), QuoteItem::class.java)
    }


    override fun saveLastCate(cate: String) {
        pref.edit().putString("lastCate", cate).apply()
    }

    override fun getLastCate(): String {
        return pref.getString("lastCate", "random").toString()
    }


    override fun saveFav(quoteItem: QuoteItem) {
        favPref.edit().putString(quoteItem._id, gson.toJson(quoteItem)).apply()
    }

    override fun delFav(id: String) {
        favPref.edit().remove(id).apply()
    }

    override fun hasFav(id: String) = favPref.contains(id)

    override fun getFavQuotes(): Array<QuoteItem> {
        val l = mutableListOf<QuoteItem>()
        favPref.all.forEach {
            l.add(gson.fromJson(it.value as String,QuoteItem::class.java))
        }
        return l.toTypedArray()
    }


    private fun saveTagQuotes(tag: String, quotes: Array<QuoteItem>) {
        pref.edit().putString(tag, gson.toJson(quotes)).apply()
    }

    override suspend fun getCateQuotes(tag: String): Array<QuoteItem> {
        // checking in the shared pref
        if (pref.contains(tag)) {
            return gson.fromJson(pref.getString(tag, "[]"), Array<QuoteItem>::class.java)
        }
        return suspendCoroutine { continuation ->

            //if not found call over the network
            ret.getQuotes(tag).enqueue(object : Callback<QuotesResponse> {
                override fun onResponse(call: Call<QuotesResponse>, response: Response<QuotesResponse>) {
                    if (response.isSuccessful) {
                        saveTagQuotes(tag, response.body()!!.toTypedArray())
                        continuation.resume(
                            response.body()!!.toTypedArray()
                        )
                    } else continuation.resume(emptyArray())
                }

                override fun onFailure(call: Call<QuotesResponse>, t: Throwable) {
                    continuation.resume(emptyArray())
                }
            })
        }
    }


}