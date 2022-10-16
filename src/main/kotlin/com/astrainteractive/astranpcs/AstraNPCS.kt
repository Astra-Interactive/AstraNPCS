package com.astrainteractive.astranpcs

import com.astrainteractive.astranpcs.api.NPCManager
import com.astrainteractive.astranpcs.api.PacketReader
import com.astrainteractive.astranpcs.commands.CommandManager
import com.astrainteractive.astranpcs.events.ClickNpcEvent
import com.astrainteractive.astranpcs.utils.ConfigProvider
import com.astrainteractive.astranpcs.utils.EmpireNPCSProvider
import com.astrainteractive.astranpcs.utils.Files
import kotlinx.coroutines.runBlocking
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.events.GlobalEventManager


class AstraNPCS : JavaPlugin() {


    companion object {
        lateinit var instance: AstraNPCS
    }

    init {
        instance = this
    }

    override fun onEnable() {
        AstraLibs.rememberPlugin(this)
        Logger.prefix = "AstraNPCS"
        CommandManager()
        reload()
        NPCManager.onEnable()
        ClickNpcEvent()
        PacketReader.onEnable()
    }

    override fun onDisable() {
        runBlocking { AstraTaskTimer.cancelJobs() }
        HandlerList.unregisterAll(this)
        GlobalEventManager.onDisable()
        NPCManager.onDisable()
        PacketReader.onDisable()
    }

    fun reload() {
        Files.configFile.reload()
        ConfigProvider.reload()
        EmpireNPCSProvider.reload()
        // NPCManager
        NPCManager.onDisable()
        NPCManager.onEnable()
        // PacketReader
        PacketReader.onDisable()
        PacketReader.onEnable()
        runBlocking { AstraTaskTimer.cancelJobs() }

    }
}