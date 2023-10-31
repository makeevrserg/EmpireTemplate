package ru.astrainteractive.astratemplate.command

import CommandManager
import ru.astrainteractive.astralibs.command.registerTabCompleter
import ru.astrainteractive.astralibs.util.withEntry

/**
 * Tab completer for your plugin which is called when player typing commands
 */
fun CommandManager.tabCompleter() = plugin.registerTabCompleter("atemp") {
    when {
        args.isEmpty() -> listOf("atemp", "atempreload")
        args.size == 1 -> listOf("atemp", "atempreload").withEntry(args.last())
        else -> emptyList()
    }
}
