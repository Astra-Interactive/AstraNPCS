package com.astrainteractive.astranpcs.utils

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

sealed class Permissions(override val key: String):IPermission {
    object CreateNPC : Permissions("astra_npcs.create_npc")
}

