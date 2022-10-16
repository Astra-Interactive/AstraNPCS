package com.astrainteractive.astranpcs

import com.astrainteractive.astranpcs.api.NPCManager
import com.astrainteractive.astranpcs.api.PacketReader
import com.astrainteractive.astranpcs.api.remote.IMojangApiBuilder
import com.astrainteractive.astranpcs.api.remote.MojangApi
import com.astrainteractive.astranpcs.commands.CommandManager
import com.astrainteractive.astranpcs.events.ClickNpcEvent
import com.astrainteractive.astranpcs.utils.IConfig
import com.astrainteractive.astranpcs.utils.IFiles
import kotlinx.coroutines.runBlocking
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.di.Injector.remember
import ru.astrainteractive.astralibs.events.GlobalEventManager


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
            CommandManager()
            IFiles()
            IConfig.create()
            remember(MojangApi(mojangApi))
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
        onDisable()
        onEnable()
    }
}