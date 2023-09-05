package com.tomer.quotly.repo

import com.tomer.quotly.modals.QuoteItem

interface MainRepo {
    fun saveLast(quoteItem: QuoteItem)
    fun getLastQuote() :QuoteItem
    fun saveLastCate(cate:String)
    fun getLastCate() : String
    fun saveFav(quoteItem: QuoteItem)
    fun delFav(id: String)
    fun hasFav(id: String) : Boolean
    fun getFavQuotes() : Array<QuoteItem>
   suspend fun getCateQuotes(tag:String) : Array<QuoteItem>
}