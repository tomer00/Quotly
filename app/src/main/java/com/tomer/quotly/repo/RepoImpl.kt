package com.tomer.quotly.repo

import com.tomer.quotly.retro.Api
import javax.inject.Inject

class RepoImpl @Inject constructor( private val ret: Api) : MainRepo {
    override fun getData() {

    }
}