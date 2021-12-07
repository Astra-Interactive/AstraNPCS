package com.astrainteractive.astranpcs.api

import com.astrainteractive.astranpcs.api.versioned.NPC_v1_18_R1
import com.astrainteractive.astranpcs.data.EmpireNPC

class NPCManager {
    companion object {
        val registeredNPCs: MutableSet<NPC> = HashSet<NPC>()
        private val empireNPCs:MutableSet<EmpireNPC> = HashSet<EmpireNPC>()
        fun npcByEmpireId(id:String) = registeredNPCs.filter { it.empireId==id }.firstOrNull()
        fun npcByEntityId(id:Int) = registeredNPCs.filter { it.id==id }.firstOrNull()
    }
    init {
        empireNPCs.addAll(EmpireNPC.getList())
        empireNPCs.forEach(this::newNPC)
    }

    private fun newNPC(empireNPC: EmpireNPC): NPC {
        val npc: NPC = NPC_v1_18_R1(empireNPC)

        registeredNPCs.add(npc)
        return npc
    }

    fun findNPC(name: String) = registeredNPCs.stream().filter { it.empireId.equals(name, ignoreCase = true) }.findFirst()
    fun deleteNPC(npc: NPC) {
        npc.delete()
        registeredNPCs.remove(npc)
    }

    fun deleteAllNPCs() =
        HashSet(registeredNPCs).forEach(this::deleteNPC)

    fun onDisable(){
        deleteAllNPCs()
    }
}