package com.astrainteractive.astranpcs.events

import com.astrainteractive.astralibs.events.DSLEvent
import com.astrainteractive.astranpcs.api.NPCInteractionEvent
import org.bukkit.Bukkit
import kotlin.random.Random

class ClickNpcEvent {
    val interactionEvent = DSLEvent.event(NPCInteractionEvent::class.java) { e ->
        val player = e.player
        val enpc = e.clicked.empireNPC
        if (enpc.phrases?.isNotEmpty() == true) {
            val phrases = enpc.phrases
            val phrase = phrases[Random.nextInt(phrases.size)]
            e.player.sendMessage(phrase)
        }
        for (command in enpc.commands ?: listOf()) {
            if (command.asConsole)
                Bukkit.dispatchCommand(Bukkit.getServer().consoleSender, command.command)
            else
                player.performCommand(command.command)
        }
    }
}