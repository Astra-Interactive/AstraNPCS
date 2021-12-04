package com.astrainteractive.astranpcs.commands

import com.astrainteractive.astranpcs.AstraNPCS
import com.astrainteractive.astranpcs.NPCManager
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
//        if (NPCManager.abstractNPCByEmpireId.containsKey(args[1]))
//            NPCManager.abstractNPCByEmpireId[args[1]]!!.setSkinByName(args[2])
    }

    private fun moveNpcToPlayer(sender: Player, label: String, args: Array<out String>) {
        if (args.size != 2)
            return
//        if (NPCManager.abstractNPCByEmpireId.containsKey(args[1]))
//            NPCManager.abstractNPCByEmpireId[args[1]]!!.setLocation(sender.location)
    }

    private fun teleportToNpc(sender: Player, label: String, args: Array<out String>) {

        if (args.size != 2)
            return
//        if (NPCManager.abstractNPCByEmpireId.containsKey(args[1]))
//            sender.teleport(NPCManager.abstractNPCByEmpireId[args[1]]!!.location)
    }
}