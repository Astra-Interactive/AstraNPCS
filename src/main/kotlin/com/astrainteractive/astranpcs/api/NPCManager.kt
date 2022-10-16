package com.astrainteractive.astranpcs.api

import com.astrainteractive.astranpcs.api.versioned.AbstractNPC
import com.astrainteractive.astranpcs.api.versioned.INpcController
import com.astrainteractive.astranpcs.api.versioned.NPCController_1_19
import com.astrainteractive.astranpcs.data.EmpireNPC
import com.astrainteractive.astranpcs.data.Config
import com.astrainteractive.astranpcs.utils.ConfigProvider
import com.astrainteractive.astranpcs.utils.EmpireNPCSProvider

object NPCManager {
    val registeredNPCs: MutableSet<AbstractNPC> = HashSet<AbstractNPC>()
    private val empireNPCs: MutableSet<EmpireNPC> = HashSet<EmpireNPC>()
    fun npcByEmpireId(id: String) = registeredNPCs.firstOrNull { it.empireNPC.id == id }
    fun onEnable() {
        empireNPCs.apply {
            clear()
            addAll(EmpireNPCSProvider.value)
            forEach {
                createNPC(it).apply {
                    spawn()
                }
            }
        }
    }

    private fun createNPC(empireNPC: EmpireNPC): AbstractNPC {
        val npc: AbstractNPC = object : AbstractNPC() {
            override val config: Config
                get() = ConfigProvider.value
            override val empireNPC: EmpireNPC
                get() = empireNPC
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