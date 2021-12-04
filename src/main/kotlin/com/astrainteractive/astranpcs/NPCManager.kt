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
            runAsyncTask {
                for (npc in realNPCList)
                    npc.hideNPCForPlayer(player)
                playerSpawnedNpcs[player]?.clear() ?: return@runAsyncTask
            }
        }

        private fun Player.hideNPC(npc: RealNPC) {
            playerSpawnedNpcs[this]?.remove(npc)
            npc.hideNPCForPlayer(this)
        }

        private fun Player.showNPC(npc: RealNPC) {
            if (playerSpawnedNpcs[this]!!.contains(npc))
                return
            playerSpawnedNpcs[this]?.add(npc)
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
                for (npc in realNPCList) {
                    if (p.location.world != npc.location.world)
                        continue
                    val dist = p.location.distance(npc.location)
                    synchronized(this) {
                        if (!realNPCList.contains(npc))
                            return@synchronized
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

        fun playerJoinEvent(player: Player) {
            runAsyncTask {
                for (npc in realNPCList)
                    npc.showNPCToPlayer(player)
                playerSpawnedNpcs[player] = mutableSetOf()
            }
        }


        /**
         * File with config and NPCS
         */
        lateinit var fileManager: FileManager


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
        val playerSpawnedNpcs: MutableMap<Player, MutableSet<RealNPC>> = mutableMapOf()

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

    private lateinit var eventManager: EventManager

    private fun onEnable() {
        fileManager = FileManager("npcs.yml")

        empireNPCList = EmpireNPC.getList().toMutableSet()
        realNPCList = empireNPCList.map {
            RealNPC(it).apply { spawnNPC() }
        }.toMutableList()
        eventManager = EventManager()


    }

    init {
        if (AstraNPCS.instance.server.pluginManager.getPlugin("ProtocolLib") != null)
            onEnable()

        for (player in Bukkit.getOnlinePlayers()) {
            playerSpawnedNpcs[player] = mutableSetOf()
        }
    }

    fun onDisable() {
        if (AstraNPCS.instance.server.pluginManager.getPlugin("ProtocolLib") == null)
            return
        for (npc in realNPCList)
            npc.onDisable()
        eventManager.onDisable()
        clearListAndMap()
    }
}