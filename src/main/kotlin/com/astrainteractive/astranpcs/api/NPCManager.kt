package com.astrainteractive.astranpcs.api

import com.astrainteractive.astranpcs.api.versioned.NPC_v1_19_R1
import com.astrainteractive.astranpcs.data.EmpireNPC

object NPCManager {
    val registeredNPCs: MutableSet<INPC> = HashSet<INPC>()
    private val empireNPCs: MutableSet<EmpireNPC> = HashSet<EmpireNPC>()
    fun npcByEmpireId(id: String) = registeredNPCs.firstOrNull { it.empireId == id }
    fun npcByEntityId(id: Int) = registeredNPCs.firstOrNull { it.id == id }
    fun onEnable() {
        empireNPCs.clear()
        empireNPCs.addAll(EmpireNPC.getList())
        empireNPCs.forEach {
            newNPC(it).spawn()
        }
    }

    private fun newNPC(empireNPC: EmpireNPC): INPC {
        val npc: INPC = NPC_v1_19_R1(empireNPC)
        registeredNPCs.add(npc)
        return npc
    }

    fun findNPC(name: String) =
        registeredNPCs.stream().filter { it.empireId.equals(name, ignoreCase = true) }.findFirst()

    fun onDisable() {
        HashSet(registeredNPCs).forEach { npc ->
            npc.despawn()
            registeredNPCs.remove(npc)
        }
    }
}