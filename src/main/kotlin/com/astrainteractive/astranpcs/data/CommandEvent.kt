package com.astrainteractive.empireprojekt.npc.data

import org.bukkit.configuration.ConfigurationSection

data class CommandEvent(
    val command: String,
    val asConsole: Boolean
) {
    companion object {
        fun new(section: ConfigurationSection?): List<CommandEvent> {
            val list = mutableListOf<CommandEvent>()
            section ?: return list
            for (keys in section.getKeys(false)) {
                list.add(
                    CommandEvent(section.getString("$keys.command") ?: continue, section.getBoolean("$keys.as_console", false))
                )
            }
            return list
        }
    }
}