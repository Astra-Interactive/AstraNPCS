package com.astrainteractive.astranpcs.api.versioned

import com.astrainteractive.astralibs.async.AsyncHelper
import com.astrainteractive.astralibs.utils.catching
import com.astrainteractive.astralibs.utils.convertHex
import com.astrainteractive.astranpcs.AstraNPCS
import com.astrainteractive.astranpcs.AstraTaskTimer
import com.astrainteractive.astranpcs.api.INPC
import com.astrainteractive.astranpcs.api.NPCViewers
import com.astrainteractive.astranpcs.api.versioned.NMSUtil.asEntityArmorStand
import com.astrainteractive.astranpcs.api.versioned.NMSUtil.connection
import com.astrainteractive.astranpcs.api.versioned.NMSUtil.toIChatBaseComponent
import com.astrainteractive.astranpcs.api.versioned.NMSUtil.worldServer
import com.astrainteractive.astranpcs.data.EmpireNPC
import com.astrainteractive.astranpcs.data.Skin
import com.astrainteractive.astranpcs.utils.Config
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.*
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.EntityPlayer
import net.minecraft.server.level.WorldServer
import net.minecraft.world.entity.player.EntityHuman
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.scoreboard.NameTagVisibility
import org.bukkit.scoreboard.Team
import java.util.*
import kotlin.collections.HashMap


class NPC_v1_19_R1(override val empireNPC: EmpireNPC) : INPC {
    /**
     * Здесь хранится ссылка на NPC
     */
    private lateinit var entityPlayer: EntityPlayer

    /**
     * Это ID нашей Entity
     */
    override val id: Int
        get() = entityPlayer.hashCode()

    /**
     * Список людей, которые находятся вблизи NPC
     */
    private val viewers = NPCViewers()

    /**
     * Список армор стендов которые расположены над NPC
     */
    private var armorStands: List<ArmorStand> = listOf()

    private fun EntityPlayer.setLocation(l: Location) =
        (this as net.minecraft.world.entity.Entity).a(l.x, l.y, l.z, l.yaw, l.pitch)


    private val onlinePlayers: MutableCollection<out Player>
        get() = Bukkit.getOnlinePlayers()

    private fun sendPacket(player: Player?, packet: Any?) = player?.connection()?.a(packet as Packet<*>)
    private fun Float.toAngle(): Byte =
        (this * 256 / 360).toInt().toByte()

    private lateinit var removeTabTask: AstraTaskTimer
    private lateinit var rotationTask: AstraTaskTimer


    private fun setRotationTask() {
        rotationTask = AstraTaskTimer().runTaskTimer(200L) {
            catching {
                onlinePlayers.forEach { player ->
                    if (player.location.world != location.world) {
                        hideFrom(player)
                        return@forEach
                    }
                    if (player.location.distance(location) < Config.distanceTrack) {
                        lookAtPlayer(player)
                    }
                }
            }

        }
    }

    private fun setRemoveTabTask() {
        removeTabTask = AstraTaskTimer().runTaskTimer(700L) {
            catching {
                onlinePlayers.forEach { player ->
                    if (player.location.world != location.world) {
                        hideFrom(player)
                        return@forEach
                    }
                    when {
                        player.location.distance(location) < Config.distanceHide -> {
                            showTo(player)
//                            showNPCPacket(player)
//                            hideFromTabPacketTimed(player)
                        }
                        player.location.distance(location) > Config.distanceHide -> hideFrom(player)
                    }
                }
            }

        }
    }

    /**
     * Ставим армор стенды надо головой NPC
     */
    private fun setArmorStandName() {
        armorStands = empireNPC.lines?.mapIndexed { i, line ->
            val location = location.clone().add(0.0, 0.2 * i, 0.0)
            (location.world?.spawnEntity(location, EntityType.ARMOR_STAND) as ArmorStand).apply {
                customName = convertHex(line)
                isCustomNameVisible = true
                isInvisible = true
                isInvulnerable = true
                this.setCanMove(false)
            }
        } ?: listOf()
    }


    private fun removeNearArmorStand() = location.world.entities.forEach { entity ->
        if (entity !is ArmorStand)
            return@forEach
        val intArray: IntArray = arrayOf(0, 1).toIntArray()
        if (entity.location.distance(location) < 2)
            entity.remove()

    }

    /**
     * Спавним NPC в мир, ставим скин, ставим Армор Стенды и скрываем обычное имя над головой
     */
    override fun spawn() {
        removeNearArmorStand()
        val profile: GameProfile = GameProfile(UUID.randomUUID(), empireId)//No more than 16 chars
        val world: WorldServer = location.worldServer
        val server: MinecraftServer = NMSUtil.minecraftServer
        entityPlayer = EntityPlayer(server, world, profile, null)
        entityPlayer.setLocation(location)
        entityPlayer.listName = empireNPC.name?.toIChatBaseComponent
        if (empireNPC.skin != null)
            setSkin(empireNPC.skin)
        setArmorStandName()
        hideName()
        setRotationTask()
        setRemoveTabTask()

    }


    /**
     * Отправляем игроку пакет, с которым NPC будет смотреть на игрока
     */
    override fun lookAtPlayer(player: Player) {
        val location = location.clone().setDirection(player.location.subtract(location.clone()).toVector())

        val yaw = location.yaw.toAngle()
        val pitch = location.pitch.toAngle()
        val headRotationPacket = PacketPlayOutEntityHeadRotation(
            entityPlayer,
            yaw
        )
        sendPacket(player, headRotationPacket)
        val lookPacket = PacketPlayOutEntity.PacketPlayOutEntityLook(
            id,
            yaw,
            pitch,
            false
        )
        sendPacket(player, lookPacket)
        AsyncHelper.launch {
            delay(Config.removeListTime.toLong())
            AsyncHelper.callSyncMethod {
                hideFromTab(player)
            }
        }
    }


    override fun setSkinByName(name: String) {
        AsyncHelper.launch {
            val skin = Skin.getSkinByName(name) ?: return@launch
            AsyncHelper.callSyncMethod {
                despawn()
                empireNPC.skin = skin
                spawn()
            }
        }
    }

    override fun setLocation(l: Location) {
        despawn()
        empireNPC.location = l
        empireNPC.save()
        spawn()
    }

    // GameProfile
    private fun setSkin(skin: Skin?) =
        (entityPlayer as EntityHuman).fz().properties.put(
            "textures",
            Property("textures", skin?.value, skin?.signature)
        )

    override fun showTo(player: Player?) {
        /**
         * Отправляем игроку пакет, с которым он сможет видеть NPC
         */
        fun showNPCPacket(p: Player) {
            val packet = PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a,
                entityPlayer
            )
            sendPacket(p, packet)
        }

        fun spawnNPCPacket(p: Player) {
            sendPacket(p, PacketPlayOutNamedEntitySpawn(entityPlayer))
        }

        /**
         * Отправляем пакет, который покажет армор стенды
         */
        fun showArmorStandPacket(p: Player) {
            armorStands.forEach { stand ->
                val entityArmorStand = stand.asEntityArmorStand()
                val packetPlayOutSpawnEntity = PacketPlayOutSpawnEntity(entityArmorStand);
                val metadata = PacketPlayOutEntityMetadata(
                    (entityArmorStand as net.minecraft.world.entity.Entity).hashCode(),
                    (entityArmorStand as net.minecraft.world.entity.Entity).ai(),
                    true
                );
                sendPacket(p, packetPlayOutSpawnEntity)
                sendPacket(p, metadata)
            }
        }


        val lastLogin = viewers.getLastViewTime(player ?: return)
        if (lastLogin != null && lastLogin == player.lastLogin)
            return
        viewers.addViewer(player, player.lastLogin)
        showNPCPacket(player)
        spawnNPCPacket(player)
        showArmorStandPacket(player)
        lookAtPlayer(player)
    }

    private fun hideFromTab(p: Player?) {
        val packet = PacketPlayOutPlayerInfo(
            PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e,
            entityPlayer
        )
        sendPacket(p, packet)
    }

    override fun hideFrom(player: Player?) {
        /**
         * Отправляем пакет, который спрячет Армор стенды
         */
        fun hideArmorStandPacket(p: Player) = armorStands.forEach {
            val packet =
                PacketPlayOutEntityDestroy((it.asEntityArmorStand() as net.minecraft.world.entity.Entity).hashCode())
            sendPacket(p, packet)
        }

        /**
         * Отправляем игроку пакет, с которым NPC исчезнет
         */
        fun hideNPCPacket(p: Player) {
            sendPacket(p, PacketPlayOutEntityDestroy(id))
        }



        if (!viewers.has(player ?: return)) return
        viewers.remove(player)
        player?.let {
            hideArmorStandPacket(player)
            hideNPCPacket(player)
            hideFromTab(player)
//            hideFromTabPacketTimed(player)
        }
    }

    private fun hideName() {
        val scoreboard = Bukkit.getScoreboardManager()?.mainScoreboard
        scoreboard?.getTeam(empireId)?.unregister()
        val scoreboardHideNameTeam: Team = scoreboard?.registerNewTeam(empireId) ?: return
        scoreboardHideNameTeam.nameTagVisibility = NameTagVisibility.NEVER
        for (team in Bukkit.getScoreboardManager()?.mainScoreboard?.teams ?: return)
            team.removeEntry(empireId)
        scoreboardHideNameTeam.addEntry(empireId)
    }


    override fun despawn() {
        runBlocking {
            rotationTask.cancel()
            removeTabTask.cancel()
        }
        armorStands.forEach(ArmorStand::remove)
        armorStands = listOf()
        hideFromAll()
        viewers.viewers.mapNotNull { Bukkit.getPlayer(it.key) }.forEach(this::hideFrom)
        removeNearArmorStand()
    }
}