package com.astrainteractive.astranpcs.api.versioned

import com.astrainteractive.astralibs.*
import com.astrainteractive.astranpcs.api.NPC
import com.astrainteractive.astranpcs.data.EmpireNPC
import com.astrainteractive.astranpcs.data.Skin
import com.astrainteractive.astranpcs.utils.Config
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.*
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.EntityPlayer
import net.minecraft.server.level.WorldServer
import net.minecraft.server.network.PlayerConnection
import net.minecraft.world.entity.player.EntityHuman
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_18_R1.CraftServer
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftArmorStand
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_18_R1.util.CraftChatMessage
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.plugin.IllegalPluginAccessException
import org.bukkit.scheduler.BukkitTask
import org.bukkit.scoreboard.NameTagVisibility
import org.bukkit.scoreboard.Team
import java.util.*
import kotlin.collections.HashMap

class NPC_v1_18_R1(override val empireNPC: EmpireNPC) : NPC {
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
    private val viewers: MutableMap<UUID, Long> = HashMap()

    /**
     * Список армор стендов которые расположены над NPC
     */
    private var armorStands: List<ArmorStand> = listOf()

    private fun EntityPlayer.setLocation(l: Location) =
        (this as net.minecraft.world.entity.Entity).a(l.x, l.y, l.z, l.yaw, l.pitch)

    private fun Player.connection(): PlayerConnection =
        (this as CraftPlayer).handle.b


    private fun sendPacket(player: Player?, packet: Any?) = player?.connection()?.a(packet as Packet<*>)
    private fun Float.toAngle(): Byte =
        (this * 256 / 360).toInt().toByte()

    private fun ArmorStand.asEntityArmorStand() = (this as CraftArmorStand).handle
    private lateinit var removeTabTask: BukkitTask
    private lateinit var rotationTask: BukkitTask

    init {
        spawn()
    }

    private val onlinePlayers: MutableCollection<out Player>
        get() = Bukkit.getOnlinePlayers()

    fun setTasks() {
        rotationTask = Bukkit.getScheduler().runTaskTimer(AstraLibs.instance, Runnable {
            catching {
                onlinePlayers.forEach { player ->
                    if (player.location.world!=location.world) {
                        hideFrom(player)
                        return@forEach
                    }
                    if (player.location.distance(location) < Config.distanceTrack) {
                        headRotationPacket(player)
                    }
                }
            }

        }, 0, 4)

        removeTabTask = Bukkit.getScheduler().runTaskTimer(AstraLibs.instance, Runnable {
            catching {
                onlinePlayers.forEach { player ->
                    if (player.location.world!=location.world) {
                        hideFrom(player)
                        return@forEach
                    }
                    when {
                        player.location.distance(location) < Config.distanceHide -> {
                            showTo(player)
                            showNPCPacket(player)
//                            hideFromTabPacketTimed(player)
                        }
                        player.location.distance(location) > Config.distanceHide -> hideFrom(player)
                    }
                }
            }

        }, 0, 4)
    }

    /**
     * Ставим армор стенды надо головой NPC
     */
    private fun setName() {
        armorStands = empireNPC.lines?.mapIndexed { i, line ->
            val location = location.clone().add(0.0, 0.2 * i, 0.0)
            (location.world?.spawnEntity(location, EntityType.ARMOR_STAND) as ArmorStand).apply {
                customName = AstraUtils.HEXPattern(line)
                isCustomNameVisible = true
                isInvisible = true
                isInvulnerable = true
            }
        } ?: listOf()
    }

    /**
     * Спавним NPC в мир, ставим скин, ставим Армор Стенды и скрываем обычное имя над головой
     */
    fun spawn() {
        removeNearArmorStand()
        val profile: GameProfile = GameProfile(UUID.randomUUID(), empireId)//No more than 16 chars
        val world: WorldServer = (location.world as CraftWorld).handle
        val server: MinecraftServer = (Bukkit.getServer() as CraftServer).server
        entityPlayer = EntityPlayer(server, world, profile)
        entityPlayer.setLocation(location)
        entityPlayer.listName = CraftChatMessage.fromString(empireNPC.name?.HEX() ?: "").first()
        setSkin(empireNPC.skin)
        setName()
        hideName()
        setTasks()
    }

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
     * Отправляем игроку пакет, с которым NPC исчезнет
     */
    fun hideNPCPacket(p: Player) {
        sendPacket(p, PacketPlayOutEntityDestroy(id))
    }

    /**
     * Отправляем игроку пакет, с которым удалим NPC из Tab-листа
     */
    fun hideFromTabPacketTimed(p: Player) {
        try {
            Bukkit.getScheduler().runTaskLater(AstraLibs.instance, Runnable {
                hideFromTab(p)
            }, Config.removeListTime)
        } catch (e: IllegalPluginAccessException) {
            hideFromTab(p)
        }
    }

    fun hideFromTab(p: Player?) {
        val packet = PacketPlayOutPlayerInfo(
            PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e,
            entityPlayer
        )
        sendPacket(p, packet)
    }

    /**
     * Отправляем игроку пакет, с которым NPC будет смотреть на игрока
     */
    fun headRotationPacket(player: Player) {
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
    }

    /**
     * Отправляем пакет, который спрячет Армор стенды
     */
    fun hideArmorStandPacket(p: Player) = armorStands.forEach {
        val packet =
            PacketPlayOutEntityDestroy((it.asEntityArmorStand() as net.minecraft.world.entity.Entity).hashCode())
        sendPacket(p, packet)
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

    /**
     * Деспавн NPC из мира
     */
    private fun despawn() {
        armorStands.forEach(ArmorStand::remove)
        armorStands = listOf()
        Bukkit.getOnlinePlayers().forEach(this::hideFrom)
    }

    override fun setSkinByName(name: String) {
        runAsyncTask {
            val skin = Skin.getSkinByName(name) ?: return@runAsyncTask
            callSyncMethod {
                delete()
                empireNPC.skin = skin
                empireNPC.save()
                spawn()
            }
        }
    }

    override fun setLocation(l: Location) {
        delete()
        empireNPC.location = l
        empireNPC.save()
        spawn()
    }

    private fun setSkin(skin: Skin?) =
        (entityPlayer as EntityHuman).fp().properties.put(
            "textures",
            Property("textures", skin?.value, skin?.signature)
        )

    override fun showTo(player: Player?) {
        val lastLogin = viewers[player?.uniqueId ?: return]
        if (lastLogin != null && lastLogin == player.lastLogin)
            return
        viewers[player.uniqueId] = player.lastLogin
        showNPCPacket(player)
        spawnNPCPacket(player)
        showArmorStandPacket(player)
        headRotationPacket(player)
    }

    override fun hideFrom(player: Player?) {
        if (!viewers.contains(player?.uniqueId)) return
        viewers.remove(player?.uniqueId)
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

    private fun removeNearArmorStand() = location.world.entities.forEach { entity ->
        if (entity !is ArmorStand)
            return@forEach
        if (entity.location.distance(location) < 2)
            entity.remove()
    }

    override fun delete() {
        rotationTask.cancel()
        removeTabTask.cancel()
        despawn()
        viewers.mapNotNull { Bukkit.getPlayer(it.key) }.forEach(this::hideFrom)
        removeNearArmorStand()
    }
}