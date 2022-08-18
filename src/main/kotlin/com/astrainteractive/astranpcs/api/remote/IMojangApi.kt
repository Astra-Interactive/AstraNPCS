package com.astrainteractive.astranpcs.api.remote

import com.astrainteractive.astralibs.rest.*


interface IMojangApi {
    @Request("https://api.mojang.com/users/profiles/minecraft/{name}")
    fun fetchProfile(@Path("name") name: String): ProxyTask<Response<Profile>>

    @Request("https://sessionserver.mojang.com/session/minecraft/profile/{id}?unsigned=false")
    fun fetchProfileSkin(@Path("id") id: String): ProxyTask<Response<ProfileSkin>>
}




