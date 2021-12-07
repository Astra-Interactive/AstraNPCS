package com.astrainteractive.astranpcs.utils

import com.astrainteractive.astranpcs.AstraNPCS

object Config {
    var distanceTrack:Long = 10L
    var distanceHide:Long = 30L
    var removeListTime:Long = 50L
    fun load(){
        val c = AstraNPCS.npcsConfig.getConfig().getConfigurationSection("config")?:return
        distanceTrack = c.getLong("distanceTrack",distanceTrack)
        distanceHide = c.getLong("distanceHide",distanceHide)
        removeListTime = c.getLong("removeListTime",removeListTime)
    }
}