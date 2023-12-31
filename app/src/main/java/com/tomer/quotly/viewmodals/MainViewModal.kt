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


    private val currentList = mutableListOf<QuoteItem>()
    private var currentPos = 0

    fun onCateClick(cate: String) {
        viewModelScope.launch {
            val l = repo.getCateQuotes(cate)
            if (l.isEmpty()) {
                _isConnected.value = true
                return@launch
            }
            _isConnected.value = false
            currentList.clear()
            currentList.addAll(l)
            currentPos = Random.nextInt(currentList.size)
            _currentQuote.value = currentList[currentPos]
            _currentCate.value = cate
            repo.saveLastCate(cate)
            repo.saveLast(currentQuote.value!!)
        }
    }

    fun onFavClick() {
        if (isOpen.value == true) {
            _isOpen.value = false
            viewModelScope.launch {
                currentList.clear()
                currentList.addAll(repo.getCateQuotes(_currentCate.value ?: "random"))
            }
        } else if (isOpen.value == false || isOpen.value == null) {
            _isOpen.value = true


            if (favList.value!!.isEmpty()) _favList.value = repo.getFavQuotes().toList()
            if (favList.value!!.isNotEmpty()) {
                currentList.clear()
                currentList.addAll(favList.value!!)
                _currentQuote.value = favList.value!![0]
                currentPos = 0
            }
        }
    }

    fun onFavAdd() {
        if (!repo.hasFav(_currentQuote.value!!._id)) {
            val newL = _favList.value!!.toMutableList()
            newL.add(_currentQuote.value!!)
            _favList.value = newL
            currentList.clear()
            currentList.addAll(newL)
            repo.saveFav(_currentQuote.value!!)
        }
    }

    fun onFavDel(id: String) {
        val newL = _favList.value!!.toMutableList()
        newL.remove(_currentQuote.value!!)
        _favList.value = newL
        repo.delFav(id)
        if (newL.isEmpty()) {
            viewModelScope.launch {
                currentList.clear()
                currentList.addAll(repo.getCateQuotes(_currentCate.value!!))
                if (currentList.isEmpty()) return@launch
                _currentQuote.value = currentList.first()
                currentPos = 0
            }
            return
        }
        currentList.clear()
        currentList.addAll(newL)
        if (_currentQuote.value!!._id == id) {
            _currentQuote.value = currentList.first()
        }
    }

    fun favSel(id: String) {
        for (i in _favList.value!!.indices) {
            if (_favList.value!![i]._id == id) {
                _currentQuote.value = _favList.value!![i]
                currentPos = i
                break
            }
        }
    }

    fun isConn() {
        _isConnected.value = false
    }

    fun swipe(isRight: Boolean) {
        if (isRight) currentPos++
        else currentPos--;
        if (currentList.isEmpty()) {
            onCateClick("random")
            currentList.add(currentQuote.value!!)
        }
        if (currentPos < 0) currentPos = currentList.size - 1
        _currentQuote.value = currentList[currentPos % currentList.size]
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
        viewModelScope.launch {
            currentList.clear()
            currentList.addAll(repo.getCateQuotes(_currentCate.value ?: "random"))
        }
    }


}