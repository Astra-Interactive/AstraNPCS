package com.astrainteractive.astranpcs.utils

import ru.astrainteractive.astralibs.file_manager.FileManager

val Files: IFiles
    get() = IFiles.instance

class IFiles {
    val configFile: FileManager =
        FileManager("config.yml")
    companion object {
        lateinit var instance: IFiles
    }

    init {
        instance = this
    }
}