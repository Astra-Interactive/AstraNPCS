package com.astrainteractive.astranpcs

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.FileManager
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.events.GlobalEventManager
import com.astrainteractive.astranpcs.api.NPCManager
import com.astrainteractive.astranpcs.api.PacketReader
import com.astrainteractive.astranpcs.commands.CommandManager
import com.astrainteractive.astranpcs.data.AstraNPCYaml
import com.astrainteractive.astranpcs.events.EventManager
import kotlinx.coroutines.*
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin


class AstraNPCS : JavaPlugin() {


    companion object {
        lateinit var instance: AstraNPCS
        lateinit var npcsConfig: FileManager
    }

    private lateinit var eventManager: EventManager

    override fun onEnable() {

        AstraLibs.rememberPlugin(this)
        Logger.prefix = "AstraNPCS"
        npcsConfig = FileManager("npcs.yml")
        AstraNPCYaml.create()
        instance = this
        NPCManager.onEnable()
        CommandManager()
        eventManager = EventManager()
        PacketReader.onEnable()
    }

    override fun onDisable() {
        runBlocking { AstraTaskTimer.cancelJobs() }
        eventManager.onDisable()
        GlobalEventManager.onDisable()
        HandlerList.unregisterAll(this)
        NPCManager.onDisable()
        PacketReader.onDisable()
    }

    fun reload() {
        onDisable()
        onEnable()
    }
}