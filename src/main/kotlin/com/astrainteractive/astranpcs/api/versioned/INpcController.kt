package com.astrainteractive.astranpcs.api.versioned

import com.astrainteractive.astranpcs.data.Skin
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo
import net.minecraft.server.dedicated.DedicatedServer
import net.minecraft.server.level.EntityPlayer
import net.minecraft.server.level.WorldServer
import net.minecraft.world.entity.decoration.EntityArmorStand
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player

interface INpcController {
    fun setLocation(ep: EntityPlayer, l: Location)
    fun setListName(ep: EntityPlayer, name: String?)
    val dedicatedServer: DedicatedServer
    fun worldServer(location: Location): WorldServer
    fun setSkin(ep: EntityPlayer, skin: Skin?)
    fun sendPacket(player: Player?, packet: Any?)
    fun removeFromTabPacket(entityPlayer: EntityPlayer): PacketPlayOutPlayerInfo
    fun addToTabPacket(entityPlayer: EntityPlayer): PacketPlayOutPlayerInfo
    fun entityMetadataPacket(armorStand: EntityArmorStand): PacketPlayOutEntityMetadata
    fun entityArmorStand(armorStand: ArmorStand): EntityArmorStand
}