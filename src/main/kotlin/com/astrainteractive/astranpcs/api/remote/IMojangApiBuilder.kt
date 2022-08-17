package com.astrainteractive.astranpcs.api.remote

import com.astrainteractive.astralibs.rest.RestRequester
import org.jetbrains.kotlin.com.google.gson.Gson

object IMojangApiBuilder {
    fun build() = RestRequester {
        this.baseUrl = ""
        this.converterFactory = { json, clazz ->
            json?.let { Gson().fromJson(json, clazz) }
        }
        this.decoderFactory = Gson()::toJson
    }.create(IMojangApi::class.java)
}