package com.astrainteractive.astranpcs.data


import com.astrainteractive.astralibs.HEX
import com.astrainteractive.astralibs.catchingNoStackTrace
import com.astrainteractive.astralibs.getFloat
import com.astrainteractive.astralibs.getHEXStringList
import com.astrainteractive.astranpcs.AstraNPCS
import com.google.gson.JsonParser
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import java.io.InputStreamReader
import java.net.URL

data class EmpireNPC(
    val id: String,
    val name: String?,
    val lines: List<String>?,
    val phrases: List<String>?,
    val commands: List<Command>,
    var skin: Skin?,
    var location: Location
) {
    companion object {
//        fun ConfigurationSection.getLocation(): Location {
//            val world = getString("world")?:"world"
//            val x = getDouble("x",0.0)
//            val y = getDouble("y",0.0)
//            val z = getDouble("z",0.0)
//            val pitch = getFloat("pitch",0.0f)
//            val yaw = getFloat("yaw",0.0f)
//            return Location(Bukkit.getWorld(world),x,y,z,yaw,pitch)
//        }
        fun getList(): List<EmpireNPC> {
            val config = AstraNPCS.npcsConfig.getConfig().getConfigurationSection("npcs")?:return listOf()
            return config.getKeys(false).mapNotNull { id ->
                val c = config.getConfigurationSection(id) ?: return@mapNotNull null
                val name = c.getString("name")?.HEX()
                val lines = c.getHEXStringList("lines")
                val phrases = c.getHEXStringList("phrases")
                val commands = Command.getCommands(c.getConfigurationSection("commands"))
                val skin = Skin.getSkin(c.getConfigurationSection("skin"))
                val location = c.getLocation("location")?:return@mapNotNull null
                EmpireNPC(
                    id = id,
                    name = name,
                    lines = lines,
                    phrases=phrases,
                    commands = commands,
                    skin = skin,
                    location = location
                )
            }
        }
    }
    fun save(){
        val c = AstraNPCS.npcsConfig.getConfig()
        c.set("npcs.$id.name",name)
        c.set("npcs.$id.lines",lines)
        c.set("npcs.$id.phrases",phrases)
        commands.forEach {cmd->
            c.set("npcs.$id.commands.${cmd.id}.command",cmd.command)
            c.set("npcs.$id.commands.${cmd.id}.asConsole",cmd.asConsole)
        }
        c.set("npcs.$id.skin.value",skin?.value)
        c.set("npcs.$id.skin.signature",skin?.signature)
        c.set("npcs.$id.location",location)
        AstraNPCS.npcsConfig.saveConfig()
    }
}

data class Command(
    val id:String,
    val command: String,
    val asConsole: Boolean
) {
    companion object {
        fun getCommands(s: ConfigurationSection?): List<Command> =
            s?.getKeys(false)?.mapNotNull { key ->
                Command(
                    key,
                    s.getString("$key.command") ?: return@mapNotNull null,
                    s.getBoolean("$key.asConsole", false)
                )
            }?: listOf()
    }
}

data class Skin(
    val value: String,
    val signature: String
){
    companion object{
        fun getSkin(s:ConfigurationSection?): Skin? {
            s?:return null
            return Skin(
                s.getString("value")?:return null,
                s.getString("signature")?:return null
            )
        }
        fun getSkinByName(name:String)= catchingNoStackTrace {
            val url = URL("https://api.mojang.com/users/profiles/minecraft/$name")
            val reader = InputStreamReader(url.openStream())
            val uuid = JsonParser().parse(reader).asJsonObject.get("id").asString
            val url2 = URL("https://sessionserver.mojang.com/session/minecraft/profile/$uuid?unsigned=false")
            val reader2 = InputStreamReader(url2.openStream())
            val property =
                JsonParser().parse(reader2).asJsonObject.get("properties").asJsonArray.get(0).asJsonObject
            val value = property.get("value").asString
            val signature = property.get("signature").asString
            Skin(value, signature)
        }
    }
}