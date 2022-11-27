package com.astrainteractive.astranpcs.remote_api

import com.astrainteractive.astranpcs.remote_api.IMojangApi
import com.astrainteractive.astranpcs.remote_api.Profile
import com.astrainteractive.astranpcs.remote_api.ProfileSkin
import ru.astrainteractive.astralibs.utils.catching


class MojangApi(val api: IMojangApi) {
    suspend fun fetchProfile(name: String): Profile? = catching(true) {
        api.fetchProfile(name).await()?.response
    }

    suspend fun fetchProfileSkin(uuid: String): ProfileSkin? = catching(true) {
        api.fetchProfileSkin(uuid).await()?.response
    }
}