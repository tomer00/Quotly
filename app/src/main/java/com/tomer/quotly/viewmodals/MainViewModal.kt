package com.tomer.quotly.viewmodals

import androidx.lifecycle.ViewModel
import com.tomer.quotly.repo.RepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModal @Inject constructor(private  val repo:RepoImpl) : ViewModel (){

}