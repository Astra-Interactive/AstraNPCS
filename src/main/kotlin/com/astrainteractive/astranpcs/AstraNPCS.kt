package com.astrainteractive.astranpcs

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.FileManager
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astranpcs.commands.CommandManager
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

    override fun onEnable() {
        AstraLibs.create(this)
        Logger.init("AstraNPCS")
        npcs = FileManager("npcs.yml")
        instance = this
        npcManager = NPCManager()
        CommandManager()
    }

    override fun onDisable() {
        HandlerList.unregisterAll()

        npcManager?.onDisable()
    }
    fun reload(){
        onDisable()
        onEnable()
    }
}