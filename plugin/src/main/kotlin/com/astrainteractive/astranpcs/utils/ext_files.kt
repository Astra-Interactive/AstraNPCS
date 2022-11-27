package com.astrainteractive.astranpcs.utils

import com.astrainteractive.astranpcs.models.EmpireNPC

fun EmpireNPC.save() {
    val c = Files.configFile.fileConfiguration
    c.set("npcs.$id.name", name)
    c.set("npcs.$id.lines", lines)
    c.set("npcs.$id.phrases", phrases)
    commands.forEach { cmd ->
        c.set("npcs.$id.commands.${cmd.id}.command", cmd.command)
        c.set("npcs.$id.commands.${cmd.id}.asConsole", cmd.asConsole)
    }
    c.set("npcs.$id.skin.value", skin?.value)
    c.set("npcs.$id.skin.signature", skin?.signature)
    c.set("npcs.$id.location", location)
    Files.configFile.save()
}