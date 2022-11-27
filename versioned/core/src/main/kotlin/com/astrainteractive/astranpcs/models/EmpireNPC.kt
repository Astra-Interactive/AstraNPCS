package com.astrainteractive.astranpcs.models


import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection

data class EmpireNPC(
    val id: String,
    val name: String? = null,
    val lines: List<String>? = null,
    val phrases: List<String>? = null,
    val commands: List<Command> = listOf(),
    var skin: Skin? = null,
    var location: Location,
)

data class Command(
    val id: String,
    val command: String,
    val asConsole: Boolean,
) {
    companion object {
        fun getCommands(s: ConfigurationSection?): List<Command> =
            s?.getKeys(false)?.mapNotNull { key ->
                Command(
                    key,
                    s.getString("$key.command") ?: return@mapNotNull null,
                    s.getBoolean("$key.asConsole", false)
                )
            } ?: listOf()
    }
}

data class Skin(
    val value: String,
    val signature: String,
) {
    companion object {
        fun getSkin(s: ConfigurationSection?): Skin? {
            s ?: return null
            return Skin(
                s.getString("value") ?: return null,
                s.getString("signature") ?: return null
            )
        }
    }
}