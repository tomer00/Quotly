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
            val l = repo.getCateQuotes(cate)
            if (l.isEmpty()) {
                _isConnected.value = true
                return@launch
            }
            _isConnected.value = false
            currentList = l
            _currentQuote.value = currentList[Random.nextInt(currentList.size)]
            _currentCate.value = cate
            repo.saveLastCate(cate)
            repo.saveLast(currentQuote.value!!)
        }
    }

    fun onFavClick() {
        if (isOpen.value == true)
            _isOpen.value = false
        else if (isOpen.value == false || isOpen.value == null) {
            _isOpen.value = true


            if (favList.value!!.isEmpty()) _favList.value = repo.getFavQuotes().toList()
            if (favList.value!!.isNotEmpty()) {
                currentList = favList.value!!.toTypedArray()
                _currentQuote.value = favList.value!![0]
            }
        }
    }

    fun onFavAdd() {
        if (!repo.hasFav(_currentQuote.value!!._id)) {
            val newL = _favList.value!!.toMutableList()
            newL.add(_currentQuote.value!!)
            _favList.value = newL
            currentList = newL.toTypedArray()
            repo.saveFav(_currentQuote.value!!)
        }
    }

    fun onFavDel(id: String) {
        val newL = _favList.value!!.toMutableList()
        newL.remove(_currentQuote.value!!)
        _favList.value = newL
        repo.delFav(id)
        if (_favList.value!!.isEmpty()){
            viewModelScope.launch {
                currentList = repo.getCateQuotes(_currentCate.value!!)
                _currentQuote.value = currentList.first()
            }
            return
        }
        currentList = newL.toTypedArray()
        if (_currentQuote.value!!._id == id) {
            _currentQuote.value = currentList.first()
        }
    }

    fun favSel(id: String) {
        for (i in _favList.value!!) {
            if (i._id == id) {
                _currentQuote.value = i
                break
            }
        }
    }

    fun isConn(){
        _isConnected.value  = false
    }

    //region ::CurrentQuote

    private val _currentQuote = MutableLiveData<QuoteItem>()
    val currentQuote: LiveData<QuoteItem> = _currentQuote

    //endregion ::CurrentQuote

    // region ::FavList

    private val _favList = MutableLiveData<List<QuoteItem>>(emptyList())
    val favList: LiveData<List<QuoteItem>> = _favList

    //endregion ::FavList

    // region ::CurrentCate

    private val _currentCate = MutableLiveData<String>()
    val currentCate: LiveData<String> = _currentCate

    //endregion ::CurrentCate

    //region ::FAV VIEW POSITION
    private val _isOpen = MutableLiveData<Boolean>()
    val isOpen: LiveData<Boolean> = _isOpen
    //endregion ::FAV VIEW POSITION

    // region ::No Internet Visibility
    private val _isConnected = MutableLiveData<Boolean>()
    val isConn: LiveData<Boolean> = _isConnected
    //endregion ::No Internet Visibility


    init {
        _currentQuote.value = repo.getLastQuote()
        _currentCate.value = repo.getLastCate()
    }


}