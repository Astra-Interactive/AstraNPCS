package com.astrainteractive.astranpcs.api

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.catching
import com.astrainteractive.astralibs.events.DSLEvent
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.PacketPlayInUseEntity
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import java.lang.reflect.Field
import java.util.UUID


object PacketReader : AstraPacketReader<PacketPlayInUseEntity>() {

    override fun readPacket(player: Player, packet: PacketPlayInUseEntity) {
        if (!packet.javaClass.simpleName.equals("PacketPlayInUseEntity", ignoreCase = true)) return
        if (!getClassFieldValue(getClassFieldValue(packet, "b")!!, "a").toString()
                .equals("MAIN_HAND", ignoreCase = true)
        ) return
        getClassFieldValue(getClassFieldValue(packet, "b")!!, "b") ?: return
        val id = getClassFieldValue(packet, "a") as? Int
        val npc = NPCManager.registeredNPCs.firstOrNull { it.hash == id } ?: return
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