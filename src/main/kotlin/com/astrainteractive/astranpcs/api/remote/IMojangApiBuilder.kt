package com.astrainteractive.astranpcs.api.remote

import org.jetbrains.kotlin.com.google.gson.Gson
import ru.astrainteractive.astralibs.rest.RestRequester

object IMojangApiBuilder {
    fun build() = RestRequester {
        this.baseUrl = ""
        this.converterFactory = { json, clazz ->
//            println("Got json: $json and clazz: $clazz")
            json?.let { Gson().fromJson(json, clazz) }
        }
        this.decoderFactory = Gson()::toJson
    }.create(IMojangApi::class.java)
}