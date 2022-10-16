package com.astrainteractive.astranpcs.utils

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

sealed class Permissions(val value: String) {
    object CreateNPC : Permissions("astra_npcs.create_npc")
    fun hasPermission(player: CommandSender) = player.hasPermission(value)

    /**
     * If has astra_template.damage.2 => returns 2
     */
    fun permissionSize(player: Player) = player.effectivePermissions
        .firstOrNull { it.permission.startsWith(value) }
        ?.permission
        ?.replace("$value.", "")
        ?.toIntOrNull()
}