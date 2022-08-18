package com.astrainteractive.astranpcs

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.events.GlobalEventManager
import com.astrainteractive.astralibs.utils.Injector.module
import com.astrainteractive.astralibs.utils.Injector.remember
import com.astrainteractive.astranpcs.api.NPCManager
import com.astrainteractive.astranpcs.api.PacketReader
import com.astrainteractive.astranpcs.api.remote.IMojangApiBuilder
import com.astrainteractive.astranpcs.api.remote.MojangApi
import com.astrainteractive.astranpcs.commands.CommandManager
import com.astrainteractive.astranpcs.events.ClickNpcEvent
import com.astrainteractive.astranpcs.utils.*
import kotlinx.coroutines.*
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin


class AstraNPCS : JavaPlugin() {


    companion object {
        lateinit var instance: AstraNPCS
    }

    private val mojangApi by lazy {
        IMojangApiBuilder.build()
    }
    init {
        instance = this
    }

    override fun onEnable() {

        AstraLibs.rememberPlugin(this)
        Logger.prefix = "AstraNPCS"
        module {
            CommandManager()
            IFiles()
            IConfig.create()
            NPCManager.onEnable()
            ClickNpcEvent()
            PacketReader.onEnable()
            remember(MojangApi(mojangApi))
        }
    }

    override fun onDisable() {
        runBlocking { AstraTaskTimer.cancelJobs() }
        HandlerList.unregisterAll(this)
        GlobalEventManager.onDisable()
        NPCManager.onDisable()
        PacketReader.onDisable()
    }

    fun reload() {
        onDisable()
        onEnable()
    }
}