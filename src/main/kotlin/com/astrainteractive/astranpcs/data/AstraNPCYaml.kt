package com.astrainteractive.astranpcs.data

import com.astrainteractive.astralibs.EmpireSerializer
import com.astrainteractive.astralibs.catching
import com.astrainteractive.astranpcs.AstraNPCS
import com.charleskorn.kaml.Yaml
import com.google.gson.JsonParser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.configuration.ConfigurationSection
import java.io.InputStreamReader
import java.net.URL

val CONFIG: AstraNPCYaml._Config
    get() = AstraNPCYaml.instance.config
val empireNPCS: Map<String, AstraNPCYaml.YamlNPC>
    get() = AstraNPCYaml.instance.npcs

val astraNPCYaml: AstraNPCYaml
    get() = AstraNPCYaml.instance
val models: Map<String, AstraNPCYaml.YamlNPC>
    get() = AstraNPCYaml.instance.npcs.values.filter { it.modelID != null }.associateBy { it.id }

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class AstraNPCYaml(
    val config: _Config,
    val npcs: MutableMap<String, YamlNPC>
) {
    fun save() {
//        val text = Yaml.default.encodeToString(serializer(AstraNPCYaml::class.java))
//        AstraNPCS.npcsConfig.getFile().writeText(text)
//        create()
    }

    companion object {
        lateinit var instance: AstraNPCYaml
            private set

        fun create(): AstraNPCYaml {
            instance = EmpireSerializer.toClass<AstraNPCYaml>(AstraNPCS.npcsConfig.getFile())!!
            return instance
        }
    }

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class _Config(
        val distanceTrack: Int,
        val distanceHide: Int,
        val removeListTime: Int,
        val stayInList: Boolean,
    )

    @Suppress("PROVIDED_RUNTIME_TOO_LOW")
    @Serializable
    data class YamlNPC(
        val id: String,
        val modelID: String? = null,
        val invisible:Boolean = false,
        val name: String = "",
        val lines: List<String> = emptyList(),
        val phrases: List<String> = emptyList(),
        val commands: Map<String, PlayCommand> = emptyMap(),
        var skin: Skin? = null,
        var location: YamlLocation,
    ) {
        @Suppress("PROVIDED_RUNTIME_TOO_LOW")
        @Serializable
        data class Skin(
            val value: String,
            val signature: String
        ) {
            companion object {
                fun getSkin(s: ConfigurationSection?): Skin? {
                    s ?: return null
                    return Skin(
                        s.getString("value") ?: return null,
                        s.getString("signature") ?: return null
                    )
                }

                fun getSkinByName(name: String) = catching {
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

        @Suppress("PROVIDED_RUNTIME_TOO_LOW")
        @Serializable
        data class PlayCommand(
            val command: String,
            val asConsole: Boolean = false
        )

        @Suppress("PROVIDED_RUNTIME_TOO_LOW")
        @Serializable
        data class YamlLocation(
            @SerialName("world")
            val _world: String,
            val x: Double,
            val y: Double,
            val z: Double,
            val pitch: Float,
            val yaw: Float
        ) {
            val world: World?
                get() = Bukkit.getWorld(_world)
            val bukkitLocation: Location
                get() = Location(world, x, y, z, yaw, pitch)

            companion object {
                fun fromBukkitLocation(location: Location) = YamlLocation(
                    _world = location.world.name,
                    x = location.x,
                    y = location.y,
                    z = location.z,
                    pitch = location.pitch,
                    yaw = location.yaw,
                )
            }
        }
    }
}