package com.astrainteractive.astranpcs.utils

import com.astrainteractive.astranpcs.data.Config
import ru.astrainteractive.astralibs.di.IReloadable

object ConfigProvider : IReloadable<Config>() {
    override fun initializer(): Config {
        val c = Files.configFile.fileConfiguration.getConfigurationSection("config") ?: return Config()
        return Config(
            c.getLong("distanceTrack", 10L),
            c.getLong("distanceHide", 30L),
            c.getLong("removeListTime", 50L)
        )
    }
}