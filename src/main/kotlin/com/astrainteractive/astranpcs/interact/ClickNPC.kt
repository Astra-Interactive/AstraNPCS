package com.astrainteractive.empireprojekt.npc.interact


import com.astrainteractive.astralibs.IAstraListener
import com.astrainteractive.astranpcs.NPCManager
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import kotlin.random.Random

class ClickNPC : IAstraListener {

    @EventHandler
    fun onClickNPC(e: RightClickNPC) {
        val player = e.player


        if (e.npc.npc.phrases?.isNotEmpty() == true) {
            val phrases = e.npc.npc.phrases
            val phrase = phrases[Random.nextInt(phrases.size)]
            e.player.sendMessage(phrase)
        }
        for (command in e.npc.npc.commands?: listOf()){
            if (command.asConsole)
                Bukkit.dispatchCommand(Bukkit.getServer().consoleSender,command.command)
            else
                player.performCommand(command.command)
        }

    }


    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
        val player = e.player
        NPCManager.playerMoveEvent(player)
    }

    @EventHandler
    fun playerJoinEvent(e:PlayerJoinEvent){
        NPCManager.playerJoinEvent(e.player)

    }
    @EventHandler
    fun playerQuitEvent(e:PlayerQuitEvent){
        NPCManager.playerQuitEvent(e.player)
    }
    @EventHandler
    fun playerDeathEvent(e:PlayerDeathEvent){
        NPCManager.playerQuitEvent(e.entity)
    }


    public override fun onDisable() {
        RightClickNPC.HANDLERS.unregister(this)
        PlayerQuitEvent.getHandlerList().unregister(this)
        PlayerJoinEvent.getHandlerList().unregister(this)
        PlayerMoveEvent.getHandlerList().unregister(this)
        PlayerDeathEvent.getHandlerList().unregister(this)
    }
}