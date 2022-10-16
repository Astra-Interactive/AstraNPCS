package com.astrainteractive.astranpcs.api.versioned

import com.astrainteractive.astranpcs.data.Skin
import com.mojang.authlib.properties.Property
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo
import net.minecraft.server.dedicated.DedicatedServer
import net.minecraft.server.level.EntityPlayer
import net.minecraft.server.level.WorldServer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.decoration.EntityArmorStand
import net.minecraft.world.entity.player.EntityHuman
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_19_R1.CraftServer
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftArmorStand
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_19_R1.util.CraftChatMessage
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.utils.HEX

object NPCController_1_19 : INpcController {
    override fun setLocation(ep: EntityPlayer, l: Location) {
        (ep as net.minecraft.world.entity.Entity).a(l.x, l.y, l.z, l.yaw, l.pitch)
    }

    override fun setListName(ep: EntityPlayer, name: String?) {
        ep.listName = CraftChatMessage.fromString(name?.HEX() ?: "").first()
    }

    override val dedicatedServer: DedicatedServer
        get() = (Bukkit.getServer() as CraftServer).server

    override fun worldServer(location: Location): WorldServer = (location.world as CraftWorld).handle

    override fun setSkin(ep: EntityPlayer, skin: Skin?) {
        (ep as EntityHuman).fy().properties.put(
            "textures",
            Property("textures", skin?.value, skin?.signature)
        )
    }

    override fun sendPacket(player: Player?, packet: Any?) {
        (player as CraftPlayer).handle.b?.a(packet as Packet<*>)
    }

    override fun removeFromTabPacket(entityPlayer: EntityPlayer): PacketPlayOutPlayerInfo {
        return PacketPlayOutPlayerInfo(
            PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e,
            entityPlayer
        )
    }

    override fun addToTabPacket(entityPlayer: EntityPlayer): PacketPlayOutPlayerInfo {
        return PacketPlayOutPlayerInfo(
            PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a,
            entityPlayer
        )
    }

    override fun entityMetadataPacket(armorStand: EntityArmorStand): PacketPlayOutEntityMetadata {
        return PacketPlayOutEntityMetadata(
            (armorStand as Entity).hashCode(),
            (armorStand as Entity).ai(),
            true
        )
    }

    override fun entityArmorStand(armorStand: ArmorStand): EntityArmorStand = (armorStand as CraftArmorStand).handle
}