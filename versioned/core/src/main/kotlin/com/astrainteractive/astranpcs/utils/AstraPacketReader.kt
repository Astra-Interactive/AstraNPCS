package com.astrainteractive.astranpcs.utils

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import net.minecraft.network.protocol.Packet
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import ru.astrainteractive.astralibs.utils.catching
import java.lang.reflect.Field
import java.util.*
import kotlin.collections.HashMap

abstract class AstraPacketReader<T : Packet<*>> {
    private val channels: MutableMap<UUID, Channel> = HashMap()
    fun onEnable() {
        onDisable()
        Bukkit.getOnlinePlayers().forEach { inject(it) }
    }

    fun onDisable() {
        channels.keys.forEach { deInject(it) }
        channels.clear()
    }
    abstract val clazz:Class<out T>

    fun decoder(player: Player) = object : MessageToMessageDecoder<T>(clazz) {
        override fun decode(ctx: ChannelHandlerContext?, packet: T?, arg: MutableList<Any>?) {
            packet ?: return
            arg ?: return
            ctx ?: return
            arg.add(packet as Any)
            readPacket(player, packet)
        }
    }

    fun inject(player: Player) {
        val craftPlayer = player as CraftPlayer
        val channel = craftPlayer.handle.b.b.m
        channels[player.uniqueId] = channel
        if (channel.pipeline().get("PacketInjector") != null) return
        channel.pipeline().addAfter("decoder", "PacketInjector", decoder(player))
    }

    fun deInject(uuid: UUID) {
        val channel = channels[uuid] ?: return
        catching { channel.pipeline().remove("PacketInjector") }
        channels.remove(uuid)
    }

    abstract fun readPacket(player: Player, packet: T)
    fun getClassFieldValue(instance: Any, name: String): Any? = catching(false) {
        val field: Field = instance.javaClass.getDeclaredField(name)
        field.isAccessible = true
        val result = field.get(instance)
        field.isAccessible = false
        result
    }
}