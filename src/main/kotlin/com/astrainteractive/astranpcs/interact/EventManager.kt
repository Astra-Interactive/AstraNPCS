package com.astrainteractive.astranpcs.interact

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.astralibs.IAstraManager
import com.astrainteractive.astranpcs.api.ProtocolLibManager

class EventManager : IAstraManager {
    override val handlers: MutableList<IAstraListener> = mutableListOf()
    init {
        ProtocolLibManager().onEnable(this)
        ClickNPC().onEnable(this)
    }
}