package com.astrainteractive.astranpcs.commands

import com.astrainteractive.astralibs.withEntry
import com.astrainteractive.astranpcs.api.NPCManager
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class TabCompleter : TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String>? {
        if (args.size == 1)
            return listOf(
                "tp",
                "reload",
                "skin",
                "move",
                "delete",
                "line_add",
                "line_remove",
                "phrase_add",
                "phrase_remove",
                "name"
            ).withEntry(args[0])
        if (args.size == 2 && listOf("tp", "move", "skin", "delete", "line_remove","line_add", "phrase_remove","phrase_add","name").contains(args[0]))
            return NPCManager.registeredNPCs.map { it.empireId }.withEntry(args[1])

        return listOf()
    }
}