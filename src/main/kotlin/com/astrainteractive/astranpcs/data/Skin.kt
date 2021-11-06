package com.astrainteractive.empireprojekt.npc.data

import org.bukkit.configuration.ConfigurationSection

data class Skin(
    val value:String,
    val signature:String
){
    companion object{
        fun new(section: ConfigurationSection?):Skin?{
            section?:return null
            val value = section.getString("value")?:return null
            val signature = section.getString("signature")?:return null
            return Skin(value,signature)
        }
    }
}