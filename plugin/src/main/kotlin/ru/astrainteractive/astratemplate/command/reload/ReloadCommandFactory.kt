package ru.astrainteractive.astratemplate.command.reload

import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.command.api.Command
import ru.astrainteractive.astralibs.command.api.CommandParser
import ru.astrainteractive.astralibs.command.api.DefaultCommandFactory
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astratemplate.AstraTemplate
import ru.astrainteractive.astratemplate.shared.core.Permissions
import ru.astrainteractive.astratemplate.shared.core.Translation
import ru.astrainteractive.klibs.kdi.Factory

class ReloadCommandFactory(
    private val plugin: JavaPlugin,
    private val translation: Translation,
    private val translationContext: BukkitTranslationContext
) : Factory<ReloadCommand> {
    private val alias = "atempreload"

    private val commandParser = CommandParser { args, sender ->
        val hasPermission = sender.toPermissible().hasPermission(Permissions.Damage)
        if (!hasPermission) return@CommandParser ReloadCommand.Result.NoPermission
        ReloadCommand.Result.Success(sender)
    }

    inner class ReloadCommandImpl :
        ReloadCommand,
        Command<ReloadCommand.Result, ReloadCommand.Input> by DefaultCommandFactory.create(
            plugin = plugin,
            alias = alias,
            commandParser = commandParser,
            commandExecutor = {
                with(translationContext) {
                    it.sender.sendMessage(translation.general.reload)
                    (plugin as AstraTemplate).reloadPlugin()
                    it.sender.sendMessage(translation.general.reloadComplete)
                }
            },
            resultHandler = { commandSender, result ->
                with(translationContext) {
                    commandSender.sendMessage(translation.general.noPermission)
                }
            },
            mapper = {
                (it as? ReloadCommand.Result.Success)?.sender?.let(ReloadCommand::Input)
            }
        )

    override fun create(): ReloadCommand {
        return ReloadCommandImpl()
    }
}