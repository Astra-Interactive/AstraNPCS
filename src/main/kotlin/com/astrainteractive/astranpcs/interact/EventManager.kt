package com.astrainteractive.astranpcs.interact

import com.astrainteractive.astralibs.EventListener
import com.astrainteractive.astralibs.EventManager
import com.astrainteractive.astranpcs.api.ProtocolLibManager

class EventManager : EventManager {
    override val handlers: MutableList<EventListener> = mutableListOf()
    init {
        ProtocolLibManager().onEnable(this)
        ClickNPC().onEnable(this)
    }
}