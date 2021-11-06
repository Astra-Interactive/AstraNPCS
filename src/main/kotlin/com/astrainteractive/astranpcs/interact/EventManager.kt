package com.astrainteractive.empireprojekt.npc.interact

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.astralibs.IAstraManager

class EventManager : IAstraManager {
    override val handlers: MutableList<IAstraListener> = mutableListOf()
    init {
        ProtocolLibPacketListener().onEnable(this)
        ClickNPC().onEnable(this)
    }
}