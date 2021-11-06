package com.astrainteractive.empireprojekt.npc.commands

import com.astrainteractive.astralibs.withEntry
import com.astrainteractive.astranpcs.NPCManager
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
                "create",
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
            return NPCManager.abstractNPCByName.keys.toList().withEntry(args[1])

        if (args.size == 3 && args.getOrNull(0).equals("changeLine"))
            return listOf(NPCManager.abstractNPCByName[args.getOrNull(1) ?: ""]?.npc?.lines?.size?.toString() ?: "0")

        if (args.size == 3 && args.getOrNull(0).equals("changePhrase"))
            return listOf(NPCManager.abstractNPCByName[args.getOrNull(1) ?: ""]?.npc?.phrases?.size?.toString() ?: "0")
        return listOf()
    }
}