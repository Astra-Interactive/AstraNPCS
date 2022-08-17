package com.astrainteractive.astranpcs.api.remote

import com.astrainteractive.astralibs.rest.*
import com.astrainteractive.astralibs.utils.catching
import org.jetbrains.kotlin.com.google.gson.Gson


interface IMojangApi {
    @Request("https://api.mojang.com/users/profiles/minecraft/{name}")
    fun fetchProfile(@Path("name") name: String): ProxyTask<Response<Profile>>

    @Request("https://sessionserver.mojang.com/session/minecraft/profile/{uuid}?unsigned=false")
    fun fetchProfileSkin(@Path("uuid") name: String): ProxyTask<Response<ProfileSkin>>
}




