package com.astrainteractive.astranpcs.api

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class NPCInteractionEvent(val player:Player,val clicked: NPC):Event(),Cancellable {
    private var isCancelled = false
    override fun isCancelled(): Boolean {
        return isCancelled
    }
    override fun setCancelled(cancel: Boolean) {
        isCancelled = cancel
    }
    override fun getHandlers(): HandlerList {
        return HANDLERS
    }
    companion object {
        val HANDLERS = HandlerList()
        @JvmStatic
        fun getHandlerList(): HandlerList = HANDLERS
    }
}



