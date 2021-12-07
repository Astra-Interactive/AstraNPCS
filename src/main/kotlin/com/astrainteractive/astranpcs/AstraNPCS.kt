package com.astrainteractive.astranpcs

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.FileManager
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astranpcs.api.NPCManager
import com.astrainteractive.astranpcs.commands.CommandManager
import com.astrainteractive.astranpcs.interact.EventManager
import com.astrainteractive.astranpcs.utils.Config
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin

class AstraNPCS: JavaPlugin() {


    companion object{
        lateinit var instance:AstraNPCS
        lateinit var npcsConfig:FileManager
    }
    /**
     *Npc manager instance
     */
    lateinit var npcManager: NPCManager
        private set
    private lateinit var eventManager: EventManager

    override fun onEnable() {
        AstraLibs.create(this)
        Logger.init("AstraNPCS")
        npcsConfig = FileManager("npcs.yml")
        Config.load()
        instance = this
        npcManager = NPCManager()
        CommandManager()
        eventManager = EventManager()
    }

    override fun onDisable() {
        eventManager.onDisable()
        HandlerList.unregisterAll()
        npcManager.onDisable()
    }
    fun reload(){
        onDisable()
        onEnable()
    }
}