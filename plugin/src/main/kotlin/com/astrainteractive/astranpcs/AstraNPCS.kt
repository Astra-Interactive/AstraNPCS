package com.astrainteractive.astranpcs

import com.astrainteractive.astranpcs.commands.CommandManager
import com.astrainteractive.astranpcs.events.ClickNpcEvent
import com.astrainteractive.astranpcs.modules.*
import com.astrainteractive.astranpcs.utils.AstraTaskTimer
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
        ClickNpcEvent()

        reload()
    }

    override fun onDisable() {
        runBlocking { AstraTaskTimer.cancelJobs() }
        HandlerList.unregisterAll(this)
        GlobalEventManager.onDisable()
        npcManagerModule.value.onDisable()
        packetReaderModule.value.onDisable()
        runBlocking { AstraTaskTimer.cancelJobs() }
    }

    fun reload() {
        Files.configFile.reload()
        configModule.reload()
        mojangApiModule.value
        npcsModule.reload()

        npcManagerModule.value.onDisable()
        npcManagerModule.reload()
        npcManagerModule.value.onEnable()

        packetReaderModule.value.onDisable()
        packetReaderModule.reload()
        packetReaderModule.value.onEnable()
        
        runBlocking { AstraTaskTimer.cancelJobs() }

    }
}