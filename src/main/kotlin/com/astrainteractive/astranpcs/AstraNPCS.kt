package com.astrainteractive.astranpcs

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.FileManager
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astranpcs.api.NPCManager
import com.astrainteractive.astranpcs.api.PacketReader
import com.astrainteractive.astranpcs.commands.CommandManager
import com.astrainteractive.astranpcs.events.MyEventManager
import com.astrainteractive.astranpcs.utils.Config
import kotlinx.coroutines.*
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin


class AstraNPCS : JavaPlugin() {


    companion object {
        lateinit var instance: AstraNPCS
        lateinit var npcsConfig: FileManager
    }

    private lateinit var eventManager: MyEventManager

    override fun onEnable() {

        AstraLibs.rememberPlugin(this)
        Logger.prefix = "AstraNPCS"
        npcsConfig = FileManager("npcs.yml")
        Config.load()
        instance = this
        NPCManager.onEnable()
        CommandManager()
        eventManager = MyEventManager()
        PacketReader.onEnable()
    }

    override fun onDisable() {
        runBlocking { AstraTaskTimer.cancelJobs() }
        eventManager.onDisable()
        HandlerList.unregisterAll(this)
        NPCManager.onDisable()
        PacketReader.onDisable()
    }

    fun reload() {
        onDisable()
        onEnable()
    }
}