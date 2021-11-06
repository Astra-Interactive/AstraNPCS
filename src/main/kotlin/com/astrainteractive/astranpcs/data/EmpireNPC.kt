package com.astrainteractive.empireprojekt.npc.data


import com.astrainteractive.astralibs.AstraUtils
import com.astrainteractive.astralibs.getHEXStringList
import com.astrainteractive.astranpcs.NPCManager
import org.bukkit.Location

data class EmpireNPC(
    val id: String,
    var name: String? = null,
    val lines: MutableList<String>? = null,
    val phrases: MutableList<String>? = null,
    val commands: MutableList<CommandEvent>? = null,
    var skin: Skin? = null,
    var location: Location
) {


    companion object {
        fun new(npc:String): EmpireNPC? {
            val section = NPCManager.fileManager.getConfig().getConfigurationSection("npcs.$npc")?:return  null
            val id = npc
            val lines = section.getHEXStringList("lines")
            val name = AstraUtils.HEXPattern(section.getString("name"))
            val phrases = section.getHEXStringList("phrases")
            val commands = CommandEvent.new(section.getConfigurationSection("commands"))
            val skin = Skin.new(section.getConfigurationSection("skin"))
            val location = section.getLocation("location")?:return null
            return EmpireNPC(
                id = id,
                name = name,
                lines = lines.toMutableList(),
                phrases = phrases.toMutableList(),
                commands = commands.toMutableList(),
                skin = skin,
                location = location
            )
        }

        fun save(npc: EmpireNPC){
            val config = NPCManager.fileManager.getConfig()
            val path = "npcs.${npc.id}"
            config.set("$path.location",npc.location)
            config.set("$path.name",npc.name)
            config.set("$path.skin.value",npc.skin?.value)
            config.set("$path.skin.signature",npc.skin?.signature)
            config.set("$path.phrases",npc.phrases)
            config.set("$path.lines",npc.lines)
            NPCManager.fileManager.saveConfig()
        }


    }
}