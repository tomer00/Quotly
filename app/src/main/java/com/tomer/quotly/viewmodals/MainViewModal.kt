package com.tomer.quotly.viewmodals

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomer.quotly.modals.QuoteItem
import com.tomer.quotly.repo.RepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random


@HiltViewModel
class MainViewModal @Inject constructor(private val repo: RepoImpl) : ViewModel() {


    private var currentList = arrayOf<QuoteItem>()

    fun onCateClick(cate: String) {
        viewModelScope.launch {
            currentList = repo.getCateQuotes(cate)
            _currentQuote.value = currentList[Random.nextInt(currentList.size)]
            _currentCate.value = cate
            repo.saveLastCate(cate)
            repo.saveLast(currentQuote.value!!)
        }
    }


    //region ::CurrentQuote

    private val _currentQuote = MutableLiveData<QuoteItem>()
    val currentQuote: LiveData<QuoteItem> = _currentQuote

    //endregion ::CurrentQuote

    // region ::CurrentCate

    private val _currentCate = MutableLiveData<String>()
    val currentCate: LiveData<String> = _currentCate

    //endregion ::CurrentCate

    init {
        _currentQuote.value = repo.getLastQuote()
        _currentCate.value = repo.getLastCate()
    }


}