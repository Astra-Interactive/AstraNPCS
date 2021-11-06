package com.astrainteractive.empireprojekt.npc.data

import com.astrainteractive.astralibs.AstraYamlParser
import com.astrainteractive.astranpcs.NPCManager

data class NPCConfig(
    val radiusTrack: Int = 20,
    val radiusHide: Int = 50,
    val npcRemoveListTime: Long = 50,
    val spawnNPCPacketTime: Long = 100,
    val npcTrackTime: Int = 300
) {
    companion object {

        fun new() = AstraYamlParser.fromYAML<NPCConfig>(
            NPCManager.fileManager.getConfig().getConfigurationSection("config"),
            NPCConfig::class.java
        )

    }
}