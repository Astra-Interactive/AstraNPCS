package com.astrainteractive.astranpcs.modules

import com.astrainteractive.astranpcs.models.Command
import com.astrainteractive.astranpcs.models.Config
import com.astrainteractive.astranpcs.models.EmpireNPC
import com.astrainteractive.astranpcs.models.Skin
import com.astrainteractive.astranpcs.remote_api.IMojangApi
import com.astrainteractive.astranpcs.remote_api.MojangApi
import com.astrainteractive.astranpcs.utils.Files
import com.astrainteractive.astranpcs.api.NPCManager
import com.astrainteractive.astranpcs.utils.PacketReader
import org.jetbrains.kotlin.com.google.gson.Gson
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.di.getValue
import ru.astrainteractive.astralibs.di.module
import ru.astrainteractive.astralibs.di.reloadable
import ru.astrainteractive.astralibs.rest.RestRequester
import ru.astrainteractive.astralibs.utils.HEX
import ru.astrainteractive.astralibs.utils.getHEXStringList

val mojangApiModule = module {
    val api = RestRequester {
        this.baseUrl = ""
        this.converterFactory = { json, clazz ->
            json?.let { Gson().fromJson(json, clazz) }
        }
        this.decoderFactory = Gson()::toJson
    }.create(IMojangApi::class.java)
    MojangApi(api)
}

val configModule = reloadable {
    val c = Files.configFile.fileConfiguration.getConfigurationSection("config") ?: return@reloadable Config()
    Config(
        c.getLong("distanceTrack", 10L),
        c.getLong("distanceHide", 30L),
        c.getLong("removeListTime", 50L)
    )
}

val npcsModule = reloadable {
    val config = Files.configFile.fileConfiguration.getConfigurationSection("npcs") ?: run {
        Logger.warn("Npc list is empty")
        return@reloadable emptyList()
    }
    return@reloadable config.getKeys(false).mapNotNull { id ->
        val c = config.getConfigurationSection(id) ?: run {
            Logger.warn("$id not exists")
            return@mapNotNull null
        }
        val name = c.getString("name")?.HEX()
        val lines = c.getHEXStringList("lines")
        val phrases = c.getHEXStringList("phrases")
        val commands = Command.getCommands(c.getConfigurationSection("commands"))
        val skin = Skin.getSkin(c.getConfigurationSection("skin"))
        val location = c.getLocation("location") ?: run {
            Logger.warn("Location is wrong: $id")
            return@mapNotNull null
        }
        EmpireNPC(
            id = id,
            name = name,
            lines = lines,
            phrases = phrases,
            commands = commands,
            skin = skin,
            location = location
        )
    }
}

val npcManagerModule = reloadable {
    val config by configModule
    val api by mojangApiModule
    val npcs by npcsModule
    NPCManager(config, npcs, api)
}

val packetReaderModule = reloadable {
    val npcManager by npcManagerModule
    PacketReader(npcManager)
}