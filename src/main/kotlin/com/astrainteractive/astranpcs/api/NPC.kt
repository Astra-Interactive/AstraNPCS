package com.astrainteractive.astranpcs.api

import com.astrainteractive.astranpcs.data.EmpireNPC
import org.bukkit.Location
import org.bukkit.entity.Player

interface NPC {
    val empireNPC: EmpireNPC
    val empireId: String
        get() = empireNPC.id
    val location: Location
        get() = empireNPC.location
    val id: Int
    fun showTo(player: Player?)
    fun hideFrom(player: Player?)
    fun delete()
    fun setLocation(l:Location)
    fun setSkinByName(name:String)
}
