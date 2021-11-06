package com.astrainteractive.astranpcs

import com.astrainteractive.astralibs.AstraUtils
import com.astrainteractive.astralibs.HEX
import com.google.gson.JsonParser
import com.astrainteractive.empireprojekt.npc.data.EmpireNPC
import com.astrainteractive.empireprojekt.npc.data.Skin
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.minecraft.network.protocol.game.*
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.EntityPlayer
import net.minecraft.server.network.PlayerConnection
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_17_R1.CraftServer
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftArmorStand
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_17_R1.util.CraftChatMessage
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.scoreboard.NameTagVisibility
import org.bukkit.scoreboard.Team
import java.io.InputStreamReader
import java.lang.Exception
import java.net.URL
import java.util.*

class AbstractNPC(val npc: EmpireNPC) {

    private var armorStands: List<ArmorStand> = listOf()
    public lateinit var nmsNpc: EntityPlayer
    val id: Int
        get() = nmsNpc.id
    val location: Location
        get() = npc.location

    /**
     * Установка имени NPC
     */
    private fun setName() {
        var offset = 0.0
        val mArmorStands = mutableListOf<ArmorStand>()
        for (line in npc.lines ?: listOf()) {
            val location = npc.location.clone().add(0.0, offset, 0.0)
            val armorStand = (location.world?.spawnEntity(location, EntityType.ARMOR_STAND) as ArmorStand).apply {
                customName = AstraUtils.HEXPattern(line)
                isCustomNameVisible = true
                isInvisible = true
                isInvulnerable = true
            }

            offset += 0.2
            mArmorStands.add(armorStand)
        }
        armorStands = mArmorStands
    }

    /**
     * Конвертация локации из BukkitAPI в Native MinecraftAPI
     */
    private fun EntityPlayer.setLocation(l: Location) {
        this.setLocation(l.x, l.y, l.z, l.yaw, l.pitch)
    }

    /**
     * Спавн NPC в мир
     */
    fun spawnNPC() {
        val profile = GameProfile(UUID.randomUUID(), npc.id)//No more than 16 chars
        val world = (npc.location.world as CraftWorld).handle
        val server: MinecraftServer = (Bukkit.getServer() as CraftServer).server
        nmsNpc = EntityPlayer(server, world, profile)
        nmsNpc.setLocation(npc.location)
        val nm = CraftChatMessage.fromString(npc.name?.HEX() ?: "")
        nmsNpc.listName = nm[0]

        if (npc.skin != null)
            setSkin(npc.skin!!)
        setName()
        showNPCToOnlinePlayers()
        hideName()
    }


    fun despawnNPC() {
        hideNPCFromOnlinePlayers()
    }


    /**
     * Set skin
     */
    fun setSkin(skin: Skin) {
        nmsNpc.profile.properties.put(
            "textures",
            Property("textures", skin.value, skin.signature)
        )

    }

    /**
     * Set skin by Name
     */
    fun setSkinByName(playerName: String) {
        val skin = getSkinByPlayerName(playerName) ?: return
        npc.skin = skin
        despawnNPC()
        spawnNPC()
        NPCManager.saveNPC(npc)
    }


    /**
     * Download skin from player
     */
    private fun getSkinByPlayerName(name: String): Skin? {
        return try {
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
        } catch (e: Exception) {
            null
        }
    }


    /**
     * Set location of NPC
     */
    fun setLocation(l: Location) {

//        hideNPCFromOnlinePlayers()
//        nmsNpc.setLocation(l)
        npc.location = l

        despawnNPC()
        spawnNPC()
        NPCManager.saveNPC(npc)
//        setName()
//        showNPCToOnlinePlayers()
//        if (npc.skin != null)
//            setSkin(npc.skin!!)


    }

    /**
     * Creating npc packet to shop npc to player
     */
    private fun spawnNPCPacket(connection: PlayerConnection) {
        connection.sendPacket(
            PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a,
                nmsNpc
            )
        )//WARNING EnumPlayerInfoAction.a==EnumPlayerInfoAction.ADD_PLAYER
//        Bukkit.getScheduler().runTaskLaterAsynchronously(
//            EmpirePlugin.instance,
//            Runnable {
//                connection.sendPacket(
//                    PacketPlayOutPlayerInfo(
//                        PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e,
//                        nmsNpc
//                    )
//                )
//            }, NPCManager.npcConfig.npcRemoveListTime
//        )
    }

    /**
     * Track player movement
     */
    public fun trackPlayer(player: Player) {
        val connection = player.connection()
        val npcLoc = nmsNpc.bukkitEntity.location
        val newLoc = npcLoc.setDirection(player.location.subtract(npcLoc).toVector())
        connection.sendPacket(
            PacketPlayOutEntity.PacketPlayOutEntityLook(
                nmsNpc.id,
                newLoc.yaw.toAngle(),
                newLoc.pitch.toAngle(),
                false
            )
        )
        Bukkit.getScheduler().runTaskLaterAsynchronously(
            AstraNPCS.instance,
            Runnable {
                spawnNPCPacket(connection)
            }, NPCManager.npcConfig.spawnNPCPacketTime
        )
        connection.sendPacket(PacketPlayOutEntityHeadRotation(nmsNpc, newLoc.yaw.toAngle()))
    }


    /**
     * Get connection of player
     */
    private fun Player.connection(): PlayerConnection {
        return (this as CraftPlayer).handle.b
    }

    /**
     * Convert float angle to byte angle for minecraft packet
     */
    private fun Float.toAngle(): Byte {
        return (this * 256 / 360).toInt().toByte()
    }


    /**
     * Get NMS Armor Stand
     */
    private fun ArmorStand.asEntityArmorStand() = (this as CraftArmorStand).handle

    /**
     * Hide Armor stand from player
     */
    private fun hideArmorStandsForPlayer(player: Player) {
        val connection = player.connection()
        for (stand in armorStands) {
            connection.sendPacket(PacketPlayOutEntityDestroy(stand.asEntityArmorStand().id))
        }
    }

    /**
     * Show armor stand for Player
     */
    private fun showArmorStandsToPlayer(player: Player) {
        val connection = player.connection()
        for (stand in armorStands) {
            val entityArmorStand = stand.asEntityArmorStand()
            val packetPlayOutSpawnEntity = PacketPlayOutSpawnEntity(entityArmorStand);
            val metadata = PacketPlayOutEntityMetadata(entityArmorStand.id, entityArmorStand.dataWatcher, true);
            connection.sendPacket(packetPlayOutSpawnEntity)
            connection.sendPacket(metadata)
        }
    }

    /**
     * Destroy armor stands
     */
    private fun removeArmorStands() {
        for (stand in armorStands)
            stand.remove()
    }


    /**
     * Hide NPC From Player
     */
    public fun hideNPCForPlayer(player: Player) {
        val connection = player.connection()

        connection.sendPacket(
            PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e,
                nmsNpc
            )
        )//WARNING EnumPlayerInfoAction.e==EnumPlayerInfoAction.REMOVE_PLAYER
        connection.sendPacket(PacketPlayOutEntityDestroy(nmsNpc.id))
        hideArmorStandsForPlayer(player)
    }

    /**
     * Hide NPC from All Players
     */
    fun hideNPCFromOnlinePlayers() {
        for (p in Bukkit.getOnlinePlayers())
            hideNPCForPlayer(p)
        removeArmorStands()
    }


    /**
     * Show NPC to Player
     */
    fun showNPCToPlayer(player: Player) {
        val connection = player.connection()//WARNING handle.b==handle.playerConnection
        spawnNPCPacket(connection)
        connection.sendPacket(PacketPlayOutNamedEntitySpawn(nmsNpc))
        showArmorStandsToPlayer(player)
    }

    /**
     * Show NPC to all players
     */
    fun showNPCToOnlinePlayers() {
        for (p in Bukkit.getOnlinePlayers())
            showNPCToPlayer(p)

    }


    /**
     * Hide default Name of NPC
     */
    fun hideName() {
        val scoreboard = Bukkit.getScoreboardManager()?.mainScoreboard
        scoreboard?.getTeam(npc.id)?.unregister()
        val scoreboardHideNameTeam: Team = scoreboard?.registerNewTeam(npc.id) ?: return
        scoreboardHideNameTeam.nameTagVisibility = NameTagVisibility.NEVER
        for (team in Bukkit.getScoreboardManager()?.mainScoreboard?.teams ?: return)
            team.removeEntry(npc.id)
        scoreboardHideNameTeam.addEntry(npc.id)
    }


    fun onDisable() {
        removeArmorStands()
        hideNPCFromOnlinePlayers()
        despawnNPC()

    }

}