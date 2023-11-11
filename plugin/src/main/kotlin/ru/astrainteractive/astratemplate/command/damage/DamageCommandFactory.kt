package ru.astrainteractive.astratemplate.command.damage

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import ru.astrainteractive.astralibs.command.api.Command
import ru.astrainteractive.astralibs.command.api.CommandExecutor
import ru.astrainteractive.astralibs.command.api.CommandParser
import ru.astrainteractive.astralibs.command.api.DefaultCommandFactory
import ru.astrainteractive.astralibs.command.registerTabCompleter
import ru.astrainteractive.astralibs.command.types.OnlinePlayerArgument
import ru.astrainteractive.astralibs.permission.BukkitPermissibleExt.toPermissible
import ru.astrainteractive.astralibs.string.BukkitTranslationContext
import ru.astrainteractive.astratemplate.shared.core.Permissions
import ru.astrainteractive.astratemplate.shared.core.Translation
import ru.astrainteractive.klibs.kdi.Factory

class DamageCommandFactory(
    private val plugin: JavaPlugin,
    private val translation: Translation,
    private val translationContext: BukkitTranslationContext
) : Factory<DamageCommand> {
    private val alias = "adamage"

    private val commandParser = CommandParser { args, sender ->

        val hasPermission = sender.toPermissible().hasPermission(Permissions.Damage)
        if (!hasPermission) return@CommandParser DamageCommand.Result.NoPermission

        val player = args.getOrNull(0)
            ?.let(OnlinePlayerArgument::transform)
            ?: return@CommandParser DamageCommand.Result.PlayerNotExists

        val damage = args.getOrNull(1)
            ?.toDoubleOrNull()
            ?: 0.0

        DamageCommand.Result.Success(
            player = player,
            damage = damage,
            damagerName = sender.name
        )
    }

    private val resultHandler = Command.ResultHandler<DamageCommand.Result> { commandSender, result ->
        when (result) {
            DamageCommand.Result.PlayerNotExists -> with(translationContext) {
                commandSender.sendMessage(translation.custom.noPlayerName)
            }

            DamageCommand.Result.NoPermission -> with(translationContext) {
                commandSender.sendMessage(translation.general.noPermission)
            }

            DamageCommand.Result.NoOp -> Unit
            is DamageCommand.Result.Success -> Unit
        }
    }

    private val commandExecutor = CommandExecutor<DamageCommand.Input> {
        with(translationContext) { it.player.sendMessage(translation.custom.damaged(it.damagerName)) }
        it.player.damage(it.damage)
    }

    private fun tabCompleter() = plugin.registerTabCompleter(alias) {
        when (args.size) {
            0 -> listOf("adamage")
            1 -> Bukkit.getOnlinePlayers().map { it.name }
            2 -> listOf(translation.custom.damageHint.raw)
            else -> Bukkit.getOnlinePlayers().map { it.name }
        }
    }

    inner class DamageCommandImpl :
        DamageCommand,
        Command<DamageCommand.Result, DamageCommand.Input> by DefaultCommandFactory.create(
            plugin = plugin,
            alias = alias,
            commandParser = commandParser,
            commandExecutor = commandExecutor,
            resultHandler = resultHandler,
            mapper = {
                (it as? DamageCommand.Result.Success)?.let {
                    DamageCommand.Input(
                        player = it.player,
                        damage = it.damage,
                        damagerName = it.damagerName
                    )
                }
            }
        )

    override fun create(): DamageCommand {
        tabCompleter()
        return DamageCommandImpl()
    }
}