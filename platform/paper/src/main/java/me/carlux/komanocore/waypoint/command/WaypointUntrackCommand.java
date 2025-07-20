package me.carlux.komanocore.waypoint.command;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import lombok.RequiredArgsConstructor;
import me.carlux.komanocore.api.waypoint.track.WaypointTrackController;
import me.carlux.komanocore.api.waypoint.track.WaypointTrackEntry;
import me.carlux.komanocore.command.PluginBaseCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@CommandAlias("waypoint")
public class WaypointUntrackCommand extends PluginBaseCommand {

    private final WaypointTrackController trackController;

    @Subcommand("untrack")
    @Description("Te permite dejar de rastreaer el waypoint que estés siguiendo.")
    public void untrack(CommandSender sender) {
        final Player player = this.requiresPlayer(sender);
        if (player == null) return;

        if (!this.trackController.isTracking(player)) {
            player.sendMessage(
                Component.text("¡No te encuentras rastreando ningún waypoint!", NamedTextColor.RED)
            );
            return;
        }

        final WaypointTrackEntry entry = this.trackController.stopTracking(player);
        player.sendMessage(
            Component.text("¡Has dejado de rastrear el waypoint \"" + entry.waypoint().name() + "\"!", NamedTextColor.GREEN)
        );
    }

}
