package com.astrainteractive.astranpcs.api

import com.astrainteractive.astranpcs.api.versioned.NPC_v1_19_R1
import com.astrainteractive.astranpcs.data.AstraNPCYaml
import com.astrainteractive.astranpcs.data.empireNPCS
import com.astrainteractive.astranpcs.data.models
import com.ticxo.modelengine.api.ModelEngineAPI
import com.ticxo.modelengine.api.model.ModeledEntity
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Villager

data class ActiveModel(
    val l: Location,
    val npc: AstraNPCYaml.YamlNPC,
    val model: com.ticxo.modelengine.api.model.ActiveModel,
    val modeledEntity: ModeledEntity,
    val armorStand: Entity
)

object NPCManager {
    val registeredNPCs: MutableSet<INPC> = HashSet<INPC>()
    val registeredModels: MutableSet<ActiveModel> = HashSet<ActiveModel>()
    fun npcByEmpireId(id: String) = registeredNPCs.firstOrNull { it.empireId == id }
    fun npcByEntityId(id: Int) = registeredNPCs.firstOrNull { it.id == id }
    fun onEnable() {
        empireNPCS.forEach { (_, it) ->
            newNPC(it).spawn()
        }
        models.forEach { id, npc ->
            val l = npc.location.bukkitLocation
            val e =(l.world.spawnEntity(l, EntityType.VILLAGER) as Villager).apply{
                this.isInvulnerable = true
                this.isInvisible = true
                this.isPersistent = true
                this.isSilent = true
                this.customName = id
                this.isCustomNameVisible = false
                this.setAI(false)
            }
            val model = ModelEngineAPI.api.modelManager.createActiveModel(npc.modelID)
            val modeledEntity = ModelEngineAPI.api.modelManager.createModeledEntity(e)
            modeledEntity.addActiveModel(model)
            modeledEntity.detectPlayers()
            modeledEntity?.isInvisible = true
            NPCManager.registeredModels.add(ActiveModel(l, npc, model, modeledEntity, e))

        }
    }

    private fun newNPC(empireNPC: AstraNPCYaml.YamlNPC): INPC {
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
        registeredModels.forEach {
            it.modeledEntity.removeModel(it.npc.modelID)
            it.armorStand.remove()
        }
    }
}