package com.astrainteractive.astranpcs.commands

import com.astrainteractive.astranpcs.AstraNPCS
import com.astrainteractive.astranpcs.api.NPCManager
import com.astrainteractive.astranpcs.utils.Permissions
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Commands : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player)
            return true

        if (!sender.hasPermission(Permissions.CREATE_NPC))
            return true
        if (args.firstOrNull().equals("reload",ignoreCase = true))
            AstraNPCS.instance.reload()

        if (args.isNotEmpty() && args[0].equals("tp", ignoreCase = true))
            teleportToNpc(sender, label, args)

        if (args.isNotEmpty() && args[0].equals("move", ignoreCase = true))
            moveNpcToPlayer(sender, label, args)
        if (args.isNotEmpty() && args[0].equals("skin", ignoreCase = true))
            setNpcSkin(sender, label, args)

        return true
    }

    private fun setNpcSkin(sender: Player, label: String, args: Array<out String>) {
        if (args.size != 3)
            return
        val npc = NPCManager.npcByEmpireId(args[1])?:return
        npc.setSkinByName(args[2]?:return)
    }

    private fun moveNpcToPlayer(sender: Player, label: String, args: Array<out String>) {
        if (args.size != 2)
            return
        val npc = NPCManager.npcByEmpireId(args[1])
        npc?.setLocation(sender.location)
    }

    private fun teleportToNpc(sender: Player, label: String, args: Array<out String>) {

        if (args.size != 2)
            return
        val npc = NPCManager.npcByEmpireId(args[1])
        sender.teleport(npc?.location?:return)
    }
}