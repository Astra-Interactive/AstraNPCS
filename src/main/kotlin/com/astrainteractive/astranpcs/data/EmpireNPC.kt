package com.astrainteractive.astranpcs.data


import com.astrainteractive.astranpcs.AstraNPCS
import com.astrainteractive.astranpcs.utils.Files
import com.google.gson.JsonParser
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.utils.HEX
import ru.astrainteractive.astralibs.utils.catching
import ru.astrainteractive.astralibs.utils.getHEXStringList
import java.io.InputStreamReader
import java.net.URL

data class EmpireNPC(
    val id: String,
    val name: String? = null,
    val lines: List<String>? = null,
    val phrases: List<String>? = null,
    val commands: List<Command> = listOf(),
    var skin: Skin? = null,
    var location: Location,
) {
    fun save() {
        val c = Files.configFile.fileConfiguration
        c.set("npcs.$id.name", name)
        c.set("npcs.$id.lines", lines)
        c.set("npcs.$id.phrases", phrases)
        commands.forEach { cmd ->
            c.set("npcs.$id.commands.${cmd.id}.command", cmd.command)
            c.set("npcs.$id.commands.${cmd.id}.asConsole", cmd.asConsole)
        }
        c.set("npcs.$id.skin.value", skin?.value)
        c.set("npcs.$id.skin.signature", skin?.signature)
        c.set("npcs.$id.location", location)
        Files.configFile.save()
    }
}

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