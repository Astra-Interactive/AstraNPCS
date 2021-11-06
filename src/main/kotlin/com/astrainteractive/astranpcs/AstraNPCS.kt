package com.astrainteractive.astranpcs

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.empireprojekt.npc.commands.CommandManager
import org.bukkit.plugin.java.JavaPlugin

class AstraNPCS: JavaPlugin() {


    companion object{
        lateinit var instance:AstraNPCS
    }
    /**
     *Npc manager instance
     */
    var npcManager: NPCManager? = null
        private set

    override fun onEnable() {
        AstraLibs.create(this)
        Logger.init("AstraNPCS")
        instance = this
        npcManager = NPCManager()
        CommandManager()
    }

    override fun onDisable() {
        npcManager?.onDisable()
    }
    fun reload(){
        onDisable()
        onEnable()
    }
}