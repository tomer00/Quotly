package com.tomer.quotly.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tomer.quotly.retro.Api
import com.tomer.quotly.utils.Utils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class HiltModules {

    @Provides
    @Singleton
    fun provideRetroApiCompiler(): Api {
        return Retrofit.Builder().baseUrl(Utils.COMPILER_LINK)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
            .build()
            .create(Api::class.java)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

}