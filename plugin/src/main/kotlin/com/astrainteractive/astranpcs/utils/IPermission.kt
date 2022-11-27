package com.astrainteractive.astranpcs.utils

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

interface IPermission {
    val key: String
    fun hasPermission(player: CommandSender) = player.hasPermission(key)

    fun permissionSize(player: Player) = player.effectivePermissions
        .firstOrNull { it.permission.startsWith(key) }
        ?.permission
        ?.replace("$key.", "")
        ?.toIntOrNull()
}