package com.astrainteractive.astranpcs.utils

import com.astrainteractive.astranpcs.api.remote.IMojangApi
import com.astrainteractive.astranpcs.api.remote.MojangApi
import org.jetbrains.kotlin.com.google.gson.Gson
import ru.astrainteractive.astralibs.di.IModule
import ru.astrainteractive.astralibs.rest.RestRequester

object MojangAPIModule : IModule<MojangApi>() {
    override fun initializer(): MojangApi {
        val api =  RestRequester {
            this.baseUrl = ""
            this.converterFactory = { json, clazz ->
//            println("Got json: $json and clazz: $clazz")
                json?.let { Gson().fromJson(json, clazz) }
            }
            this.decoderFactory = Gson()::toJson
        }.create(IMojangApi::class.java)
        return MojangApi(api)
    }
}