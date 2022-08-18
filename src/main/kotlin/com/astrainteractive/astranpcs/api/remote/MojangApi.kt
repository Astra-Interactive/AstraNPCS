package com.astrainteractive.astranpcs.api.remote

import com.astrainteractive.astralibs.utils.catching

class MojangApi(val api: IMojangApi) {
    suspend fun fetchProfile(name: String): Profile? = catching(true) {
        api.fetchProfile(name).await()?.response
    }

    suspend fun fetchProfileSkin(uuid: String): ProfileSkin? = catching(true) {
        api.fetchProfileSkin(uuid).await()?.response
    }
}