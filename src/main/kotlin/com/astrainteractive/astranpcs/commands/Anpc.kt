package com.astrainteractive.astranpcs.commands

import com.astrainteractive.astralibs.*
import com.astrainteractive.astralibs.utils.registerCommand
import com.astrainteractive.astralibs.utils.registerTabCompleter
import com.astrainteractive.astralibs.utils.withEntry
import com.astrainteractive.astranpcs.AstraNPCS
import com.astrainteractive.astranpcs.api.NPCManager
import com.astrainteractive.astranpcs.data.EmpireNPC
import com.astrainteractive.astranpcs.utils.Permissions
import org.bukkit.ChatColor
import org.bukkit.entity.Player

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

        if (args.firstOrNull().equals("create", ignoreCase = true))
            createNpc(sender, args)


        if (args.isNotEmpty() && args[0].equals("tp", ignoreCase = true))
            teleportToNpc(sender, args)

        if (args.isNotEmpty() && args[0].equals("move", ignoreCase = true))
            moveNpcToPlayer(sender, args)
        if (args.isNotEmpty() && args[0].equals("skin", ignoreCase = true))
            setNpcSkin(sender, args)


    }

    private fun createNpc(player: Player, args: Array<out String>) {
        val location = player.location.clone()
        val id = args.getOrNull(1)
        if (id == null) {
            player.sendMessage("${ChatColor.YELLOW}Вы не ввели ID")
            return
        }
        val npc = EmpireNPC(
            id = id,
            location = location
        )
        npc.save()
        AstraNPCS.instance.reload()

    }

    private fun setNpcSkin(sender: Player, args: Array<out String>) {
        if (args.size != 3)
            return
        val npc = NPCManager.npcByEmpireId(args[1]) ?: return
        npc.setSkinByName(args[2] ?: return)
    }

    private fun moveNpcToPlayer(sender: Player, args: Array<out String>) {
        if (args.size != 2)
            return
        val npc = NPCManager.npcByEmpireId(args[1])
        npc?.setLocation(sender.location)
    }

    private fun teleportToNpc(sender: Player, args: Array<out String>) {

        if (args.size != 2)
            return
        val npc = NPCManager.npcByEmpireId(args[1])
        sender.teleport(npc?.location ?: return)
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
            return@registerTabCompleter NPCManager.registeredNPCs.map { it.empireId }.withEntry(args[1])

        return@registerTabCompleter listOf()
    }
}