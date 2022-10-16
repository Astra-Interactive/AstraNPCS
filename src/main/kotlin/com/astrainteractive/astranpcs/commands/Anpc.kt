package com.astrainteractive.astranpcs.commands

import com.astrainteractive.astranpcs.AstraNPCS
import com.astrainteractive.astranpcs.api.NPCManager
import com.astrainteractive.astranpcs.utils.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.AstraLibs
import ru.astrainteractive.astralibs.Logger
import ru.astrainteractive.astralibs.utils.registerCommand
import ru.astrainteractive.astralibs.utils.registerTabCompleter
import ru.astrainteractive.astralibs.utils.withEntry

class Anpc {
    var anpc = AstraLibs.registerCommand("anpc") { sender, args ->
        if (args.firstOrNull().equals("reload", ignoreCase = true)) {
            AstraNPCS.instance.reload()
            Logger.log("Plugin reloaded")
        }
        if (sender !is Player)
            return@registerCommand

        if (!Permissions.CreateNPC.hasPermission(sender))
            return@registerCommand

        if (args.isNotEmpty() && args[0].equals("tp", ignoreCase = true))
            teleportToNpc(sender, args)

        if (args.isNotEmpty() && args[0].equals("move", ignoreCase = true))
            moveNpcToPlayer(sender, args)
        if (args.isNotEmpty() && args[0].equals("skin", ignoreCase = true))
            setNpcSkin(sender, args)


    }


    private fun setNpcSkin(sender: Player, args: Array<out String>) {
        if (args.size != 3)
            return
        val npc = NPCManager.npcByEmpireId(args[1]) ?: return
        npc.loadSkinByName(args[2])
    }

    private fun moveNpcToPlayer(sender: Player, args: Array<out String>) {
        if (args.size != 2)
            return
        val npc = NPCManager.npcByEmpireId(args[1])
//        npc?.setLocation(sender.location)
    }

    private fun teleportToNpc(sender: Player, args: Array<out String>) {

        if (args.size != 2)
            return
        val npc = NPCManager.npcByEmpireId(args[1])
        sender.teleport(npc?.empireNPC?.location ?: return)
    }


    var completer = AstraLibs.registerTabCompleter("anpc") { sender, args ->

        if (args.size == 1)
            return@registerTabCompleter listOf(
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

        if (args.size == 2 && listOf(
                "tp",
                "move",
                "skin",
                "delete",
                "line_remove",
                "line_add",
                "phrase_remove",
                "phrase_add",
                "name"
            ).contains(args[0])
        )
            return@registerTabCompleter NPCManager.registeredNPCs.map { it.empireNPC.id }.withEntry(args[1])

        return@registerTabCompleter listOf()
    }
}