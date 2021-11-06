package com.astrainteractive.empireprojekt.npc.commands

import com.astrainteractive.astranpcs.AstraNPCS

class CommandManager {
    init {
        AstraNPCS.instance.getCommand("anpc")!!.tabCompleter = TabCompleter()
        AstraNPCS.instance.getCommand("anpc")!!.setExecutor(Commands())
    }
}