package com.astrainteractive.astranpcs.api

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.events.DSLEvent
import net.minecraft.network.protocol.game.PacketPlayInUseEntity
import net.minecraft.world.EnumHand
import net.minecraft.world.phys.Vec3D
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent


object PacketReader : AstraPacketReader<PacketPlayInUseEntity>() {

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

        val npc = NPCManager.registeredNPCs.firstOrNull { it.id == id } ?: return
        println("ID: ${id}; ${NPCManager.registeredNPCs.map { it.id }}")
        AsyncHelper.callSyncMethod {
            Bukkit.getPluginManager().callEvent(NPCInteractionEvent(player, npc))
        }
    }

    val joinEvent = DSLEvent.event(PlayerJoinEvent::class.java) {
        inject(it.player)
    }
    val respawnEvent = DSLEvent.event(PlayerRespawnEvent::class.java) {
        inject(it.player)
    }
    val quitEvent = DSLEvent.event(PlayerQuitEvent::class.java) {
        deInject(it.player.uniqueId)
    }
    val deathEvent = DSLEvent.event(PlayerDeathEvent::class.java) {
        deInject(it.player.uniqueId)
    }

}