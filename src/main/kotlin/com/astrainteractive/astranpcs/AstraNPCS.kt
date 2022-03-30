package com.astrainteractive.astranpcs

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.FileManager
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.async.AsyncTask
import com.astrainteractive.astranpcs.commands.CommandManager
import com.astrainteractive.astranpcs.interact.EventManager
import com.astrainteractive.astranpcs.utils.Config
import kotlinx.coroutines.*
import org.bukkit.ChatColor
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import kotlin.concurrent.timer




class AstraNPCS : JavaPlugin() {


    companion object {
        lateinit var instance: AstraNPCS
        lateinit var npcsConfig: FileManager
    }

    private lateinit var eventManager: EventManager

    override fun onEnable() {

        AstraLibs.create(this)
        Logger.init("AstraNPCS")
        npcsConfig = FileManager("npcs.yml")
        Config.load()
        instance = this
        NPCManager.onEnable()
        CommandManager()
        eventManager = EventManager()
    }

    override fun onDisable() {
        runBlocking { AstraTaskTimer.cancelJobs() }
        eventManager.onDisable()
        HandlerList.unregisterAll(this)
        NPCManager.onDisable()
    }

    fun reload() {
        onDisable()
        onEnable()
    }
}