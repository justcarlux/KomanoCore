package me.carlux.komanocore.command;

import co.aikar.commands.BaseCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class PluginBaseCommand extends BaseCommand {

    private final static String LINE = "--------------------------------------------------";
    protected final static Component START_BLUE_LINE = Component
        .text(LINE, NamedTextColor.BLUE)
        .appendNewline();
    protected final static Component END_BLUE_LINE = Component
        .empty()
        .appendNewline()
        .append(Component.text(LINE, NamedTextColor.BLUE));

    @Nullable
    protected Player requiresPlayer(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Este comando solo puede ser usado por un jugador.", NamedTextColor.RED));
            return null;
        }
        return player;
    }

}
