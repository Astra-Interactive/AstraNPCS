package com.astrainteractive.astranpcs.events

import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.astralibs.events.EventManager
import com.astrainteractive.astranpcs.api.ProtocolLibManager

class MyEventManager : EventManager {
    override val handlers: MutableList<EventListener> = mutableListOf()
    init {
//        ProtocolLibManager().onEnable(this)
        ClickNPC().onEnable(this)
    }
}