package com.astrainteractive.astranpcs

import com.astrainteractive.astralibs.FileManager
import com.astrainteractive.astralibs.runAsyncTask
import com.astrainteractive.astranpcs.data.EmpireNPC
import com.astrainteractive.empireprojekt.npc.interact.EventManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class NPCManager {
    companion object {

        fun playerQuitEvent(player: Player) {
            playerSpawnedNpcs[player.name]?.clear() ?: return
            isThreadActive.remove(player.name)
        }


        fun playerJoinEvent(player: Player) {
            playerSpawnedNpcs[player.name] = mutableSetOf()
        }

        private fun Player.hideNPC(npc: RealNPC) {
            playerSpawnedNpcs[this.name]?.remove(npc)
            npc.hideNPCForPlayer(this)
        }

        private fun Player.showNPC(npc: RealNPC) {
            if (playerSpawnedNpcs[this.name]!!.contains(npc))
                return
            playerSpawnedNpcs[this.name]?.add(npc)
            npc.showNPCToPlayer(this)
        }

        private fun Player.trackNPC(npc: RealNPC) {
                npc.trackPlayer(this)
        }


        fun playerMoveEvent(p: Player) {
            if (isThreadActive.contains(p.name))
                return
            isThreadActive.add(p.name)

            runAsyncTask {
                realNPCList.forEach { npc ->
                    if (p.location.world != npc.location.world)
                        return@forEach
                    val dist = p.location.distance(npc.location)
                    synchronized(this) {
                        when {
                            dist > 30 -> p.hideNPC(npc)
                            dist > 10 -> p.showNPC(npc)
                            else -> {
                                p.showNPC(npc)
                                p.trackNPC(npc)
                            }
                        }
                    }
                }
                synchronized(this) {
                    isThreadActive.remove(p.name)
                }
            }
        }




        /**
         * List of all NPCS in config
         */
        var empireNPCList: MutableSet<EmpireNPC> = mutableSetOf()

        /**
         * List of Minecraft NPCS
         */
        var realNPCList: MutableList<RealNPC> = mutableListOf()

        /**
         * List of Minecraft NPCS by ID
         */
        val realNpcByEntityId: Map<Int, RealNPC>
        get() = realNPCList.associateBy { it.id }

        /**
         * List of Minecraft NPCS by their Names
         */
        val realNpcByEmpireId: Map<String, RealNPC>
        get() = realNPCList.associateBy { it.npc.id }

        /**
         * List of spawned NPCS, which in range of player
         */
        val playerSpawnedNpcs: MutableMap<String, MutableSet<RealNPC>> = mutableMapOf()

        /**
         * List of active npc track threads for player
         */
        val isThreadActive: MutableList<String> = mutableListOf()

    }

    private fun clearListAndMap() {
        empireNPCList.clear()
        realNPCList.clear()
        playerSpawnedNpcs.clear()
        isThreadActive.clear()
    }


    private fun onEnable() {
        empireNPCList = EmpireNPC.getList().toMutableSet()
        realNPCList = empireNPCList.map {
            RealNPC(it).apply { spawnNPC() }
        }.toMutableList()


    }

    init {
        if (AstraNPCS.instance.server.pluginManager.getPlugin("ProtocolLib") != null)
            onEnable()

        Bukkit.getOnlinePlayers().forEach { player ->
            playerSpawnedNpcs[player.name] = mutableSetOf()
        }
    }

    fun onDisable() {
        if (AstraNPCS.instance.server.pluginManager.getPlugin("ProtocolLib") == null)
            return
        for (npc in realNPCList)
            npc.onDisable()
        clearListAndMap()
    }
}