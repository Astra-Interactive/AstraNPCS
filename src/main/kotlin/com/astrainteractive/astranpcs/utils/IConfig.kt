package com.astrainteractive.astranpcs.utils


val Config: IConfig
    get() = IConfig.instance

class IConfig(
    val distanceTrack: Long = 10L,
    val distanceHide: Long = 30L,
    val removeListTime: Long = 50L,
) {
    companion object {
        lateinit var instance: IConfig
        fun create(): IConfig {
            val c = Files.configFile.getConfig().getConfigurationSection("config") ?: return IConfig()
            instance = IConfig(
                c.getLong("distanceTrack", 10L),
                c.getLong("distanceHide", 30L),
                c.getLong("removeListTime", 50L)
            )
            return instance
        }
    }
}