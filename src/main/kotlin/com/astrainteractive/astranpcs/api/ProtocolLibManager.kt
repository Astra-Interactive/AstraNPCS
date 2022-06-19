package com.astrainteractive.astranpcs.api

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.astranpcs.AstraNPCS
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.events.PacketListener
import com.comphenix.protocol.wrappers.EnumWrappers
import kotlinx.coroutines.launch

class ProtocolLibManager : EventListener {

    private lateinit var protocolManager: ProtocolManager
    private lateinit var packetListener: PacketListener


    private fun initPackerListener() {
        packetListener = object : PacketAdapter(
            AstraNPCS.instance,
            ListenerPriority.NORMAL,
            PacketType.Play.Client.USE_ENTITY
        ) {
            override fun onPacketReceiving(event: PacketEvent) {
                val packet = event.packet
                val player = event.player
                val entityId = event.packet.integers.read(0)
                AsyncHelper.launch {
                    for (i in 0 until packet.enumEntityUseActions.size()) {
                        if (!packet.enumEntityUseActions.read(i).action.toString()
                                .equals("INTERACT", ignoreCase = true)
                        )
                            continue
                        if (packet.enumEntityUseActions.read(i).hand == EnumWrappers.Hand.OFF_HAND)
                            continue
                        val e = NPCInteractionEvent(player, NPCManager.npcByEntityId(entityId) ?: return@launch)
                        AsyncHelper.callSyncMethod {
                            AstraNPCS.instance.server.pluginManager.callEvent(e)
                        }
                    }
                }
            }

            override fun onPacketSending(event: PacketEvent) = Unit
        }
        protocolManager.addPacketListener(packetListener)
    }

    override fun onDisable() {
        protocolManager.removePacketListener(packetListener)
    }

    init {
        AstraNPCS.instance.server.pluginManager.getPlugin("protocollib")?.let {
            protocolManager = ProtocolLibrary.getProtocolManager()
            initPackerListener()
        }
    }
}