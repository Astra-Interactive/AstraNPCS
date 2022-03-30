package com.astrainteractive.astranpcs.interact


import com.astrainteractive.astralibs.EventListener
import com.astrainteractive.astranpcs.api.NPCInteractionEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
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
        for (command in enpc.commands?: listOf()){
            if (command.asConsole)
                Bukkit.dispatchCommand(Bukkit.getServer().consoleSender,command.command)
            else
                player.performCommand(command.command)
        }

    }


    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
    }

    @EventHandler
    fun playerJoinEvent(e:PlayerJoinEvent){

    }
    @EventHandler
    fun playerQuitEvent(e:PlayerQuitEvent){
    }
    @EventHandler
    fun playerDeathEvent(e:PlayerDeathEvent){
    }


    public override fun onDisable() {
    }
}