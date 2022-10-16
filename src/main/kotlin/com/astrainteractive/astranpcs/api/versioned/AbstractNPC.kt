package com.astrainteractive.astranpcs.api.versioned

import com.astrainteractive.astranpcs.AstraTaskTimer
import com.astrainteractive.astranpcs.api.NPCViewers
import com.astrainteractive.astranpcs.api.remote.MojangApi
import com.astrainteractive.astranpcs.data.EmpireNPC
import com.astrainteractive.astranpcs.data.Skin
import com.astrainteractive.astranpcs.data.Config
import com.astrainteractive.astranpcs.utils.MojangAPIModule
import com.mojang.authlib.GameProfile
import kotlinx.coroutines.*
import net.minecraft.network.protocol.game.*
import net.minecraft.server.level.EntityPlayer
import net.minecraft.server.level.WorldServer
import org.bukkit.Bukkit
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.scoreboard.NameTagVisibility
import org.bukkit.scoreboard.Team
import ru.astrainteractive.astralibs.async.BukkitMain
import ru.astrainteractive.astralibs.async.PluginScope
import ru.astrainteractive.astralibs.utils.convertHex
import java.util.*


abstract class AbstractNPC {
    protected abstract val config: Config
    abstract val empireNPC: EmpireNPC
    protected val mojangAPI: MojangApi = MojangAPIModule.value
    protected val viewers: NPCViewers = NPCViewers()
    protected val entityPlayer: EntityPlayer = run {
        val profile = GameProfile(UUID.randomUUID(), empireNPC.id)
        val world: WorldServer = npcController.worldServer(empireNPC.location)
        val entityPlayer = EntityPlayer(npcController.dedicatedServer, world, profile, null)
        npcController.setLocation(entityPlayer, empireNPC.location)
        npcController.setListName(entityPlayer, empireNPC.name)
        entityPlayer
    }
    val entityID: Int
        get() = entityPlayer.hashCode()
    protected var armorStands: List<ArmorStand> = emptyList()
    abstract val npcController: INpcController
    private fun Float.toAngle(): Byte =
        (this * 256 / 360).toInt().toByte()

    private fun setSkin(skin: Skin?){
        npcController.setSkin(entityPlayer, skin)
        empireNPC.skin = skin
        empireNPC.save()
    }


    fun loadSkinByName(name: String) {
        despawn(false)
        PluginScope.launch {
            val uuid = mojangAPI.fetchProfile(name)
            println(uuid)

            mojangAPI.fetchProfile(name)?.id?.let { uuid ->
                mojangAPI.fetchProfileSkin(uuid)
            }?.properties?.firstOrNull()?.let {
                Skin(it.value, it.signature)
            }?.let{
                withContext(Dispatchers.BukkitMain) {
                    despawn(false)
                    setSkin(it)
                    spawn()
                }
            }
        }
    }

    private fun setArmorStandName() {
        armorStands = empireNPC.lines?.mapIndexed { i, line ->
            val location = empireNPC.location.clone().add(0.0, 0.2 * i, 0.0)
            (location.world?.spawnEntity(location, EntityType.ARMOR_STAND) as ArmorStand).apply {
                customName = convertHex(line)
                isCustomNameVisible = true
                isInvisible = true
                isInvulnerable = true
                this.setCanMove(false)
            }
        } ?: listOf()
    }

    fun spawn() {
        empireNPC.location.getNearbyEntitiesByType(ArmorStand::class.java, 5.0).forEach {
            it.remove()
        }
        setArmorStandName()
        hideName()
        setSkin(empireNPC.skin)
    }

    fun despawn(full: Boolean = true) {
        armorStands.forEach(ArmorStand::remove)
        armorStands = listOf()
        Bukkit.getOnlinePlayers().forEach(::hideFromPlayer)
        viewers.viewers.mapNotNull { Bukkit.getPlayer(it.key) }.forEach(this::hideFromPlayer)
        if (full)
            runBlocking { rotationTask.cancel() }
    }

    private fun hideName() {
        val scoreboard = Bukkit.getScoreboardManager()?.mainScoreboard
        scoreboard?.getTeam(empireNPC.id)?.unregister()
        val scoreboardHideNameTeam: Team = scoreboard?.registerNewTeam(empireNPC.id) ?: return
        scoreboardHideNameTeam.nameTagVisibility = NameTagVisibility.NEVER
        for (team in Bukkit.getScoreboardManager()?.mainScoreboard?.teams ?: return)
            team.removeEntry(empireNPC.id)
        scoreboardHideNameTeam.addEntry(empireNPC.id)
    }

    private var rotationTask: AstraTaskTimer = AstraTaskTimer().runTaskTimer(200) {
        Bukkit.getOnlinePlayers().forEach { player ->
            if (player.location.world != empireNPC.location.world) {
                hideFromPlayer(player)
            } else if (player.location.distance(empireNPC.location) < config.distanceTrack) {
                showToPlayer(player)
                lookAtPlayer(player)
            } else {
                hideFromPlayer(player)
            }
        }
    }


    fun hideFromPlayer(player: Player) {
        if (!viewers.has(player ?: return)) return
        viewers.remove(player)
        npcController.sendPacket(player, PacketPlayOutEntityDestroy(entityID))
        armorStands.forEach {
            npcController.sendPacket(player, PacketPlayOutEntityDestroy(it.entityId))
        }
        npcController.sendPacket(player, npcController.removeFromTabPacket(entityPlayer))
    }

    fun showToPlayer(player: Player) {
        val lastLogin = viewers.getLastViewTime(player ?: return)
        if (lastLogin != null && lastLogin == player.lastLogin)
            return
        viewers.addViewer(player, player.lastLogin)

        npcController.sendPacket(player, npcController.addToTabPacket(entityPlayer))
        npcController.sendPacket(player, PacketPlayOutNamedEntitySpawn(entityPlayer))
        armorStands.forEach {
            npcController.sendPacket(player, PacketPlayOutSpawnEntity(npcController.entityArmorStand(it)))
            npcController.sendPacket(player, npcController.entityMetadataPacket(npcController.entityArmorStand(it)))
        }
    }

    fun lookAtPlayer(player: Player) {
        val location = empireNPC.location.clone().apply {
            val diff = player.location.subtract(this).toVector()
            direction = diff
        }

        val yaw = location.yaw.toAngle()
        val pitch = location.pitch.toAngle()
        val headRotationPacket = PacketPlayOutEntityHeadRotation(
            entityPlayer,
            yaw
        )
        npcController.sendPacket(player, headRotationPacket)
        val lookPacket = PacketPlayOutEntity.PacketPlayOutEntityLook(
            entityID,
            yaw,
            pitch,
            false
        )
        npcController.sendPacket(player, lookPacket)
        PluginScope.launch {
            delay(config.removeListTime.toLong())
            withContext(Dispatchers.BukkitMain) {
                npcController.sendPacket(player, npcController.removeFromTabPacket(entityPlayer))
            }
        }
    }
}