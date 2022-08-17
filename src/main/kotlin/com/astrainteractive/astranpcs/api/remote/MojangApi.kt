package com.astrainteractive.astranpcs.api.remote

import com.astrainteractive.astralibs.utils.catching

class MojangApi(val api: IMojangApi) {
    suspend fun fetchProfile(name: String): Profile? = catching {
        api.fetchProfile(name).await()?.response
    }

    suspend fun fetchProfileSkin(name: String): ProfileSkin? = catching {
        api.fetchProfileSkin(name).await()?.response
    }
}