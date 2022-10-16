package com.astrainteractive.astranpcs.api

import io.netty.channel.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.minecraft.network.protocol.game.PacketPlayInUseEntity
import net.minecraft.world.EnumHand
import net.minecraft.world.phys.Vec3D
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.async.BukkitMain
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.utils.AstraPacketReader

/**
 * Handling interactions on custom NPCS
 */
object PacketReader : AstraPacketReader<PacketPlayInUseEntity>() {

    override val Player.provideChannel: Channel
        get() = (this as CraftPlayer).handle.b.b.m

    override val clazz: Class<PacketPlayInUseEntity>
        get() = PacketPlayInUseEntity::class.java

    override fun readPacket(player: Player, packet: PacketPlayInUseEntity) {
        val idFieldName = packet.javaClass.declaredFields.getOrNull(0)?.name ?: return
        val id = getClassFieldValue(packet, idFieldName)

        val typeFieldName = packet.javaClass.declaredFields.getOrNull(1)?.name ?: return
        val typeField = getClassFieldValue(packet, typeFieldName)!!
        val enumHand: EnumHand =
            getClassFieldValue(typeField, typeField.javaClass.declaredFields.getOrNull(0)?.name ?: return) as EnumHand
        val vec3D: Vec3D =
            typeField.javaClass.declaredFields.getOrNull(1)?.let { getClassFieldValue(typeField, it.name) as? Vec3D }
                ?: return
        if (enumHand != EnumHand.a) return

        val npc = NPCManager.registeredNPCs.firstOrNull { it.entityID == id } ?: return
        PluginScope.launch(Dispatchers.BukkitMain) {
            Bukkit.getPluginManager().callEvent(NPCInteractionEvent(player, npc))
        }
    }




}