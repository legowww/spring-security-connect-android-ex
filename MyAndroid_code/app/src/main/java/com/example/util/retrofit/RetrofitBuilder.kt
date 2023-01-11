package com.example.util.retrofit

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {
    var api : JsonApi
    init {
        val gson: Gson = GsonBuilder() // https://kmight0518.tistory.com/87
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        api = retrofit.create(JsonApi::class.java)
    }
}