package com.astrainteractive.astranpcs.utils

import com.astrainteractive.astranpcs.data.Command
import com.astrainteractive.astranpcs.data.EmpireNPC
import com.astrainteractive.astranpcs.data.Skin
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.di.IReloadable
import ru.astrainteractive.astralibs.utils.HEX
import ru.astrainteractive.astralibs.utils.getHEXStringList

object EmpireNPCSProvider: IReloadable<List<EmpireNPC>>() {
    override fun initializer(): List<EmpireNPC> {
        val config = Files.configFile.fileConfiguration.getConfigurationSection("npcs") ?: run {
            Logger.warn("Npc list is empty")
            return emptyList()
        }
        return config.getKeys(false).mapNotNull { id ->
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
}