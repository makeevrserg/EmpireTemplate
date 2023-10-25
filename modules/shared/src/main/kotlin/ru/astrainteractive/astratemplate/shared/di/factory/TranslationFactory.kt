package ru.astrainteractive.astratemplate.shared.di.factory

import kotlinx.serialization.encodeToString
import ru.astrainteractive.astralibs.filemanager.impl.JVMFileManager
import ru.astrainteractive.astralibs.serialization.YamlSerializer
import ru.astrainteractive.astratemplate.shared.core.Translation
import ru.astrainteractive.klibs.kdi.Factory
import java.io.File

class TranslationFactory(
    private val dataFolder: File,
    private val yamlSerializer: YamlSerializer
) : Factory<Translation> {
    override fun create(): Translation {
        val configFile = JVMFileManager("translation.yml", dataFolder)
        val translation = yamlSerializer.toClassOrDefault(
            configFile.configFile,
            ::Translation
        )
        if (!configFile.configFile.exists()) {
            configFile.configFile.createNewFile()
            configFile.configFile.writeText(yamlSerializer.yaml.encodeToString(translation))
        }
        return translation
    }
}