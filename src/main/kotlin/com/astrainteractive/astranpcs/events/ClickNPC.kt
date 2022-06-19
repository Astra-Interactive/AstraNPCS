package com.astrainteractive.astranpcs.events


import com.astrainteractive.astralibs.events.EventListener
import com.astrainteractive.astranpcs.api.NPCInteractionEvent
import com.astrainteractive.astranpcs.api.NPCManager
import org.bukkit.Bukkit
import org.bukkit.entity.ArmorStand
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*
import kotlin.random.Random

class ClickNPC : EventListener {

    @EventHandler
    fun onClickNPC(e: NPCInteractionEvent) {
        val player = e.player
        val enpc = e.clicked.empireNPC
        if (enpc.phrases?.isNotEmpty() == true) {
            val phrases = enpc.phrases
            val phrase = phrases[Random.nextInt(phrases.size)]
            e.player.sendMessage(phrase)
        }
        enpc.commands.forEach { t, command ->
            if (command.asConsole)
                Bukkit.dispatchCommand(Bukkit.getServer().consoleSender,command.command)
            else
                player.performCommand(command.command)
        }

    }


    @EventHandler
    fun entityDamageEvent(e: EntityDamageEvent) {
        println("entityDamageEvent ${e.entity.type}")
        val armorStand = e.entity as? ArmorStand?:return
        NPCManager.registeredModels.firstOrNull { it.armorStand==armorStand }?:return
        e.isCancelled = true
    }

    public override fun onDisable() {
    }
}