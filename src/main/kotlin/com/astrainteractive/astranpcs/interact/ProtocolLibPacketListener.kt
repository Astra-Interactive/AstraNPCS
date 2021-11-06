package com.astrainteractive.empireprojekt.npc.interact

import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.astralibs.runAsyncTask
import com.astrainteractive.astranpcs.AstraNPCS
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.*
import com.comphenix.protocol.wrappers.EnumWrappers
import com.astrainteractive.astranpcs.NPCManager
import org.bukkit.Bukkit

class ProtocolLibPacketListener: IAstraListener {

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

                runAsyncTask{
                    for (i in 0 until packet.enumEntityUseActions.size()) {
                        if (!packet.enumEntityUseActions.read(i).action.toString().equals("INTERACT", ignoreCase = true))
                            continue
                        if (packet.enumEntityUseActions.read(i).hand == EnumWrappers.Hand.OFF_HAND)
                            continue

                        var npcID: Int? = null
                        for (j in 0 until packet.integers.size())
                            if (!packet.integers.getField(j).name.equals("a"))
                                continue
                            else
                                npcID = packet.integers.read(j)
                        npcID ?: continue
                        Bukkit.getScheduler().callSyncMethod(AstraNPCS.instance) {
                            AstraNPCS.instance.server.pluginManager.callEvent(
                                RightClickNPC(
                                    player,
                                    NPCManager.abstractNPCByID[npcID] ?: return@callSyncMethod
                                )
                            )
                        }

                    }
                }




            }

            override fun onPacketSending(event: PacketEvent) {
                //println("Packet Sending: " + event.packet.getType().name());
            }
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