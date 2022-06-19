package com.astrainteractive.astranpcs.events

import com.astrainteractive.astralibs.AstraLibs
import com.astrainteractive.astralibs.Logger
import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.astranpcs.api.ProtocolLibManager
import org.bukkit.Bukkit

class EventManager : com.astrainteractive.astralibs.events.EventManager {
    override val handlers: MutableList<EventListener> = mutableListOf()

    init {
//        Bukkit.getServer().pluginManager.getPlugin("ProtocolLib")?.let {
//            ProtocolLibManager().onEnable(this)
//        }?:run{
//            Logger.error("Missing ProtocolLib","AstraNPC")
//        }

        ClickNPC().onEnable(this)
    }
}