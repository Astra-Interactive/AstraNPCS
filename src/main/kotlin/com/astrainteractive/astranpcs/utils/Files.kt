package com.astrainteractive.astranpcs.utils

import com.astrainteractive.astralibs.FileManager

val Files: _Files
    get() = _Files.instance

class _Files {
    val configFile: FileManager =
        FileManager("config.yml")
    companion object {
        lateinit var instance: _Files
    }

    init {
        instance = this
    }
}