package com.astrainteractive.astranpcs.api

import com.astrainteractive.astranpcs.api.versioned.NPC_v1_19_R1
import com.astrainteractive.astranpcs.data.EmpireNPC

object NPCManager {
    val registeredNPCs: MutableSet<INPC> = HashSet<INPC>()
    private val empireNPCs: MutableSet<EmpireNPC> = HashSet<EmpireNPC>()
    fun npcByEmpireId(id: String) = registeredNPCs.firstOrNull { it.empireId == id }
    fun onEnable() {
        empireNPCs.apply {
            clear()
            addAll(EmpireNPC.getList())
            forEach {
                createNPC(it).apply {
                    spawn()
                }
            }
        }
    }

    private fun createNPC(empireNPC: EmpireNPC): INPC {
        val npc: INPC = NPC_v1_19_R1(empireNPC)
        registeredNPCs.add(npc)
        return npc
    }

    fun onDisable() {
        HashSet(registeredNPCs).forEach { npc ->
            npc.despawn()
            registeredNPCs.remove(npc)
        }
    }
}