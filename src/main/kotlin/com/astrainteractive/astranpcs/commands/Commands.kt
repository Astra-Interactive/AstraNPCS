package com.astrainteractive.empireprojekt.npc.commands

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

        if (args.isNotEmpty() && args[0].equals("create", ignoreCase = true))
            createNPC(sender, args)


        if (args.getOrNull(0).equals("delete", ignoreCase = true))
            deleteNPC(sender, args)


        if (args.getOrNull(0).equals("line_add", ignoreCase = true))
            addLine(sender, args)
        if (args.getOrNull(0).equals("line_remove", ignoreCase = true))
            removeLine(sender, args)


        if (args.getOrNull(0).equals("phrase_add", ignoreCase = true))
            addPhrase(sender, args)
        if (args.getOrNull(0).equals("phrase_remove", ignoreCase = true))
            removePhrase(sender, args)


        if (args.getOrNull(0).equals("name", ignoreCase = true))
            changeName(sender, args)

        return true
    }

    private fun changeName(sender: Player, args: Array<out String>) {
        val npcId = args.elementAtOrNull(1)
        val name = args.drop(2).joinToString(" ")
        NPCManager.changeName(npcId,name)
    }
    private fun addPhrase(sender: Player, args: Array<out String>) {
        val npcId = args.elementAtOrNull(1)
        val line = args.drop(2).joinToString(" ")
        NPCManager.addNpcPhrase(npcId,line)
    }
    private fun removePhrase(sender: Player, args: Array<out String>) {
        val npcId = args.elementAtOrNull(1)
        val index = args.elementAtOrNull(2)
        NPCManager.removeNpcPhrase(npcId,index)
    }

    private fun addLine(sender: Player, args: Array<out String>) {
        val npcId = args.elementAtOrNull(1)
        val line = args.drop(2).joinToString(" ")
        NPCManager.addNpcName(npcId,line)
    }
    private fun removeLine(sender: Player, args: Array<out String>) {
        val npcId = args.elementAtOrNull(1)
        val index = args.elementAtOrNull(2)
        NPCManager.removeNpcName(npcId,index)
    }

    private fun deleteNPC(sender: Player, args: Array<out String>) {
        if (args.size != 2)
            return
        NPCManager.deleteNPC(args[1])
        sender.sendMessage("NPC ${args[1]} успешно удалён")
    }

    private fun createNPC(sender: Player, args: Array<out String>) {
        if (args.size != 2)
            return

        NPCManager.createNPC(args[1], sender.location.clone())
        sender.sendMessage("NPC ${args[1]} успешно создан")
    }

    private fun setNpcSkin(sender: Player, label: String, args: Array<out String>) {
        if (args.size != 3)
            return
        if (NPCManager.abstractNPCByName.containsKey(args[1]))
            NPCManager.abstractNPCByName[args[1]]!!.setSkinByName(args[2])
    }

    private fun moveNpcToPlayer(sender: Player, label: String, args: Array<out String>) {
        if (args.size != 2)
            return
        if (NPCManager.abstractNPCByName.containsKey(args[1]))
            NPCManager.abstractNPCByName[args[1]]!!.setLocation(sender.location)
    }

    private fun teleportToNpc(sender: Player, label: String, args: Array<out String>) {

        if (args.size != 2)
            return
        if (NPCManager.abstractNPCByName.containsKey(args[1]))
            sender.teleport(NPCManager.abstractNPCByName[args[1]]!!.location)
    }
}