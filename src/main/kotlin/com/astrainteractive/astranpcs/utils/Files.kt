package com.astrainteractive.astranpcs.utils

import com.astrainteractive.astralibs.FileManager


/**
 * All plugin files such as config.yml and other should only be stored here!
 * Except for translation.yml - it should be stored at EmpireTranslation
 * @see EmpireTranslation
 */
public class Files() {
    val npcs: FileManager =
        FileManager("npcs.yml")
}