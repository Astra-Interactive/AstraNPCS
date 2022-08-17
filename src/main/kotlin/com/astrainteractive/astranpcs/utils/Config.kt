package com.astrainteractive.astranpcs.utils

class Config(
    val distanceTrack: Long = 10L,
    val distanceHide: Long = 30L,
    val removeListTime: Long = 50L,
) {
    companion object {
        fun create(): Config {
            val c = Files.configFile.getConfig().getConfigurationSection("config") ?: return Config()
            return Config(
                c.getLong("distanceTrack", 10L),
                c.getLong("distanceHide", 30L),
                c.getLong("removeListTime", 50L)
            )
        }
    }
}