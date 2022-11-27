package com.astrainteractive.astranpcs.api

import com.astrainteractive.astranpcs.api.versioned.NPCController_1_19
import com.astrainteractive.astranpcs.remote_api.MojangApi
import com.astrainteractive.astranpcs.models.EmpireNPC
import com.astrainteractive.astranpcs.models.Config

class NPCManager(
    private val config: Config,
    private val npcs: List<EmpireNPC>,
    private val mojangAPI: MojangApi
) {
    val registeredNPCs: MutableSet<AbstractNPC> = HashSet<AbstractNPC>()
    private val empireNPCs: MutableSet<EmpireNPC> = HashSet<EmpireNPC>()
    fun npcByEmpireId(id: String) = registeredNPCs.firstOrNull { it.empireNPC.id == id }
    fun onEnable() {
        empireNPCs.apply {
            clear()
            addAll(npcs)
            forEach {
                createNPC(it).apply {
                    spawn()
                }
            }
        }
    }

    private fun createNPC(empireNPC: EmpireNPC): AbstractNPC {
        val npc: AbstractNPC = object : AbstractNPC() {
            override val config: Config = this@NPCManager.config
            override val empireNPC: EmpireNPC
                get() = empireNPC
            override val mojangAPI: MojangApi = this@NPCManager.mojangAPI
            override val npcController: INpcController
                get() = NPCController_1_19

        }
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