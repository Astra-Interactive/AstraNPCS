package com.astrainteractive.astranpcs.api.versioned

import com.astrainteractive.astralibs.utils.HEX
import net.minecraft.server.level.WorldServer
import net.minecraft.server.network.PlayerConnection
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_19_R1.CraftServer
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftArmorStand
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_19_R1.util.CraftChatMessage
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player

object NMSUtil {

    fun Player.connection(): PlayerConnection {
        return (this as CraftPlayer).handle.b
    }

    fun ArmorStand.asEntityArmorStand() = (this as CraftArmorStand).handle
    val minecraftServer = (Bukkit.getServer() as CraftServer).server
    val Location.worldServer: WorldServer
        get() = (world as CraftWorld).handle
    val String.toIChatBaseComponent
        get() = CraftChatMessage.fromString(this?.HEX() ?: "").first()
}