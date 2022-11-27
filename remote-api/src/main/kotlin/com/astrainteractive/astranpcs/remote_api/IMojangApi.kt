package com.astrainteractive.astranpcs.remote_api

import ru.astrainteractive.astralibs.rest.Path
import ru.astrainteractive.astralibs.rest.ProxyTask
import ru.astrainteractive.astralibs.rest.Request
import ru.astrainteractive.astralibs.rest.Response


interface IMojangApi {
    @Request("https://api.mojang.com/users/profiles/minecraft/{name}")
    fun fetchProfile(@Path("name") name: String): ProxyTask<Response<Profile>>

    @Request("https://sessionserver.mojang.com/session/minecraft/profile/{id}?unsigned=false")
    fun fetchProfileSkin(@Path("id") id: String): ProxyTask<Response<ProfileSkin>>
}




