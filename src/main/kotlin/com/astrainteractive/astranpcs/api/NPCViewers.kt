package com.astrainteractive.astranpcs.api

import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

class NPCViewers {

    /**
     * Список людей, которые находятся вблизи NPC
     */
    private val _viewers: MutableMap<UUID, Long> = HashMap()
    val viewers: Map<UUID, Long>
        get() = _viewers

    fun addViewer(player: Player, time: Long = System.currentTimeMillis()) {
        _viewers[player.uniqueId] = time
    }

    fun getLastViewTime(player: Player): Long? = _viewers[player.uniqueId]
    fun has(player: Player) = _viewers.contains(player.uniqueId)
    fun remove(player: Player) = _viewers.remove(player.uniqueId)
}