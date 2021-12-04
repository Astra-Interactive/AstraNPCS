package com.astrainteractive.astranpcs

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.FileManager
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astranpcs.commands.CommandManager
import com.astrainteractive.empireprojekt.npc.interact.EventManager
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin

class AstraNPCS: JavaPlugin() {


    companion object{
        lateinit var instance:AstraNPCS
        lateinit var npcs:FileManager
    }
    /**
     *Npc manager instance
     */
    var npcManager: NPCManager? = null
        private set
    private lateinit var eventManager: EventManager

    override fun onEnable() {
        AstraLibs.create(this)
        Logger.init("AstraNPCS")
        npcs = FileManager("npcs.yml")
        instance = this
        npcManager = NPCManager()
        CommandManager()
        eventManager = EventManager()
    }

    override fun onDisable() {
        eventManager.onDisable()
        HandlerList.unregisterAll()

        npcManager?.onDisable()
    }
    fun reload(){
        onDisable()
        onEnable()
    }
}