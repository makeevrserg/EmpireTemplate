package ru.astrainteractive.astratemplate.shared.di

import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astralibs.logging.JUtilLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astralibs.serialization.YamlSerializer
import ru.astrainteractive.astratemplate.shared.core.PluginConfiguration
import ru.astrainteractive.astratemplate.shared.core.PluginTranslation
import ru.astrainteractive.astratemplate.shared.di.factory.MainConfigurationFactory
import ru.astrainteractive.astratemplate.shared.di.factory.TranslationFactory
import ru.astrainteractive.klibs.kdi.Dependency
import ru.astrainteractive.klibs.kdi.Reloadable
import ru.astrainteractive.klibs.kdi.Single
import java.io.File

interface CoreModule {

    val yamlSerializer: Single<YamlSerializer>
    val logger: Dependency<Logger>
    val pluginScope: Dependency<AsyncComponent>
    val translation: Reloadable<PluginTranslation>
    val configurationModule: Reloadable<PluginConfiguration>

    class Default(
        dataFolder: File
    ) : CoreModule {
        override val yamlSerializer: Single<YamlSerializer> = Single {
            YamlSerializer()
        }
        override val logger: Dependency<Logger> = Single {
            JUtilLogger(
                tag = "AstraTemplate",
                folder = dataFolder
            )
        }

        override val pluginScope = Single {
            AsyncComponent.Default()
        }

        override val translation: Reloadable<PluginTranslation> = Reloadable {
            TranslationFactory(
                dataFolder = dataFolder,
                yamlSerializer = yamlSerializer.value
            ).create()
        }

        override val configurationModule: Reloadable<PluginConfiguration> = Reloadable {
            MainConfigurationFactory(
                dataFolder = dataFolder,
                yamlSerializer = yamlSerializer.value
            ).create()
        }
    }
}