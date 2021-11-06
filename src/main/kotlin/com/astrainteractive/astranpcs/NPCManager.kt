package com.astrainteractive.astranpcs

import com.astrainteractive.astralibs.FileManager
import com.astrainteractive.astralibs.HEX
import com.astrainteractive.astralibs.runAsyncTask
import com.astrainteractive.empireprojekt.npc.data.EmpireNPC
import com.astrainteractive.empireprojekt.npc.data.NPCConfig
import com.astrainteractive.empireprojekt.npc.interact.EventManager
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

class NPCManager {
    companion object {

        fun playerQuitEvent(player: Player) {
            runAsyncTask {
                for (npc in abstractNPCList)
                    npc.hideNPCForPlayer(player)
                playerSpawnedNpcs[player]?.clear() ?: return@runAsyncTask
            }
        }

        private fun Player.hideNPC(npc: AbstractNPC) {
            playerSpawnedNpcs[this]?.remove(npc)
            npc.hideNPCForPlayer(this)
        }

        private fun Player.showNPC(npc: AbstractNPC) {
            if (playerSpawnedNpcs[this]!!.contains(npc))
                return
            playerSpawnedNpcs[this]?.add(npc)
            npc.showNPCToPlayer(this)
        }

        private fun Player.trackNPC(npc: AbstractNPC) {
                npc.trackPlayer(this)
        }


        fun playerMoveEvent(p: Player) {
            if (isThreadActive.contains(p.name))
                return
            isThreadActive.add(p.name)

            runAsyncTask {
                for (npc in abstractNPCList) {
                    if (p.location.world != npc.location.world)
                        continue
                    val dist = p.location.distance(npc.location)
                    synchronized(this) {
                        if (!abstractNPCList.contains(npc))
                            return@synchronized
                        when {
                            dist > npcConfig.radiusHide -> p.hideNPC(npc)
                            dist > npcConfig.radiusTrack -> p.showNPC(npc)
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
                for (npc in abstractNPCList)
                    npc.showNPCToPlayer(player)
                playerSpawnedNpcs[player] = mutableSetOf()
            }
        }

        fun createNPC(id: String, location: Location) {
            val npc = EmpireNPC(id = id, location = location)
            val abstractNPC = AbstractNPC(npc)
            abstractNPC.spawnNPC()
            empireNPCList.add(npc)
            abstractNPCList.add(abstractNPC)
            abstractNPCByID[abstractNPC.id] = abstractNPC
            abstractNPCByName[id] = abstractNPC
            saveNPC(npc)
        }

        fun deleteNPC(id: String) {
            val npc = abstractNPCByName[id] ?: return
            fileManager.getConfig().set("npcs.$id", null)
            fileManager.saveConfig()
            updateNPC(npc)

        }


        fun removeNpcPhrase(npcId: String?, index: String?) {
            val oldNpc = abstractNPCByName[npcId] ?: return
            val empireNpc = oldNpc.npc
            val _index = index?.toIntOrNull() ?: empireNpc.phrases?.size ?: 0
            println(npcId)
            println(_index)
            if (_index >= empireNpc.phrases?.size ?: 0 && empireNpc.phrases?.isNotEmpty() == true)
                empireNpc.phrases.removeLast()
            else if (_index >= 0 && empireNpc.phrases?.isNotEmpty() == true)
                empireNpc.phrases.removeAt(_index)
            val newNpc = AbstractNPC(empireNpc)
            updateNPC(oldNpc, newNpc)
            saveNPC(empireNpc)
        }

        fun addNpcPhrase(npcId: String?, line: String?) {
            val oldNpc = abstractNPCByName[npcId] ?: return
            val empireNpc = oldNpc.npc
            if (line?.isEmpty() == true)
                return
            empireNpc.phrases?.add(line?.HEX() ?: return)
            val newNpc = AbstractNPC(empireNpc)
            updateNPC(oldNpc, newNpc)
            saveNPC(empireNpc)
        }


        fun addNpcName(npcId: String?, line: String?) {
            val oldNpc = abstractNPCByName[npcId] ?: return
            val empireNpc = oldNpc.npc
            if (line?.isEmpty() == true)
                return
            empireNpc.lines?.add(line?.HEX() ?: return)
            val newNpc = AbstractNPC(empireNpc)
            updateNPC(oldNpc, newNpc)
            saveNPC(empireNpc)
        }

        fun removeNpcName(npcId: String?, index: String?) {
            val oldNpc = abstractNPCByName[npcId] ?: return
            val empireNpc = oldNpc.npc
            val _index = index?.toIntOrNull() ?: empireNpc.lines?.size ?: 0
            println(npcId)
            println(_index)
            if (_index >= empireNpc.lines?.size ?: 0 && empireNpc.lines?.isNotEmpty() == true)
                empireNpc.lines.removeLast()
            else if (_index >= 0 && empireNpc.lines?.isNotEmpty() == true)
                empireNpc.lines.removeAt(_index)
            val newNpc = AbstractNPC(empireNpc)
            updateNPC(oldNpc, newNpc)
            saveNPC(empireNpc)
        }

        fun changeName(npcId: String?, name: String) {

            val oldNpc = abstractNPCByName[npcId] ?: return
            val empireNpc = oldNpc.npc
            empireNpc.name = name.HEX()

            val newNpc = AbstractNPC(empireNpc)
            updateNPC(oldNpc, newNpc)
            saveNPC(empireNpc)
        }

        fun updateNPC(old: AbstractNPC, new: AbstractNPC? = null) {
            old.onDisable()
            empireNPCList.remove(old.npc)
            abstractNPCList.remove(old)
            abstractNPCByID.remove(old.id)
            abstractNPCByName.remove(old.npc.id)
            old.onDisable()

            new ?: return
            new.spawnNPC()
            empireNPCList.add(new.npc)
            abstractNPCList.add(new)
            abstractNPCByID[new.id] = new
            abstractNPCByName[new.npc.id] = new
        }

        fun saveNPC(npc: EmpireNPC) {
            EmpireNPC.save(npc)
        }


        /**
         * File with config and NPCS
         */
        lateinit var fileManager: FileManager

        /**
         * Config class
         */
        lateinit var npcConfig: NPCConfig

        /**
         * List of all NPCS in config
         */
        val empireNPCList: MutableSet<EmpireNPC> = mutableSetOf()

        /**
         * List of Minecraft NPCS
         */
        val abstractNPCList: MutableSet<AbstractNPC> = mutableSetOf()

        /**
         * List of Minecraft NPCS by ID
         */
        val abstractNPCByID: MutableMap<Int, AbstractNPC> = mutableMapOf()

        /**
         * List of Minecraft NPCS by their Names
         */
        val abstractNPCByName: MutableMap<String, AbstractNPC> = mutableMapOf()

        /**
         * List of spawned NPCS, which in range of player
         */
        val playerSpawnedNpcs: MutableMap<Player, MutableSet<AbstractNPC>> = mutableMapOf()

        /**
         * List of active npc track threads for player
         */
        val isThreadActive: MutableList<String> = mutableListOf()

    }

    private fun clearListAndMap() {
        empireNPCList.clear()
        abstractNPCList.clear()
        abstractNPCByID.clear()
        abstractNPCByName.clear()
        playerSpawnedNpcs.clear()
        isThreadActive.clear()
    }

    private lateinit var eventManager: EventManager

    private fun onEnable() {
        fileManager = FileManager("npcs.yml")
        npcConfig = NPCConfig.new()!!

        for (npcID in fileManager.getConfig().getConfigurationSection("npcs")?.getKeys(false) ?: listOf()) {
            val npc = EmpireNPC.new(npcID) ?: continue
            val abstractNPC = AbstractNPC(npc)
            abstractNPC.spawnNPC()

            abstractNPCByName[npcID] = abstractNPC
            abstractNPCByID[abstractNPC.id] = abstractNPC
            abstractNPCList.add(abstractNPC)
            empireNPCList.add(npc)
        }

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
        for (npc in abstractNPCList)
            npc.onDisable()
        eventManager.onDisable()
        clearListAndMap()
    }
}