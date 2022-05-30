package com.astrainteractive.astranpcs.api

import com.astrainteractive.astranpcs.data.EmpireNPC
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

interface INPC {
    /**
     * Ссылка на Data-объект
     */
    val empireNPC: EmpireNPC

    /**
     * ID Npc в yml файле
     */
    val empireId: String
        get() = empireNPC.id

    /**
     * @return Location of NPC
     */
    val location: Location
        get() = empireNPC.location

    /**
     * @return unique id of minecraft entity
     */
    val id: Int

    /**
     * Show npc to all online players
     */
    fun showToAll() = Bukkit.getOnlinePlayers().forEach { showTo(it) }

    /**
     * Show npc to online player
     */
    fun showTo(player: Player?)

    /**
     * Hide npc from all online players
     */
    fun hideFromAll() = Bukkit.getOnlinePlayers().forEach { showTo(it) }

    /**
     * Hide npc from player
     */
    fun hideFrom(player: Player?)

    /**
     * Delete npc from world
     */
    fun despawn()

    /**
     * Spawn NPC in world
     */
    fun spawn()

    /**
     * Relocate NPC
     */
    fun setLocation(l: Location)
    /**
     * Look at player
     */
    fun lookAtPlayer(player: Player)

    /**
     * Change NPC skin by minecraft user name
     */
    fun setSkinByName(name: String)
}
