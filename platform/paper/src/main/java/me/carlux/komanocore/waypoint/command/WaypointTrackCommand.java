package me.carlux.komanocore.waypoint.command;

import co.aikar.commands.annotation.*;
import lombok.RequiredArgsConstructor;
import me.carlux.komanocore.api.waypoint.WaypointRepository;
import me.carlux.komanocore.api.waypoint.track.WaypointTrackController;
import me.carlux.komanocore.api.waypoint.exception.TrackNullWorldWaypointException;
import me.carlux.komanocore.command.PluginBaseCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@CommandAlias("waypoint")
public class WaypointTrackCommand extends PluginBaseCommand {

    private final WaypointRepository repository;
    private final WaypointTrackController trackController;

    @Subcommand("track")
    @CommandCompletion("@listwaypoints @nothing")
    @Syntax("<nombre>")
    @Description("Te permite rastrear alguno de tus waypoints.")
    public void track(CommandSender sender, String name) {
        final Player player = this.requiresPlayer(sender);
        if (player == null) return;

        if (name.length() > 30) {
            player.sendMessage(
                Component.text("¡El nombre del waypoint es demasiado largo!", NamedTextColor.RED)
            );
            return;
        }
        if (name.contains(" ")) {
            player.sendMessage(
                Component.text("¡El nombre del waypoint no puede contener espacios!", NamedTextColor.RED)
            );
            return;
        }
        if (this.trackController.isTracking(player)) {
            player.sendMessage(
                Component.text("¡Ya te encuentras rastreando otro waypoint!", NamedTextColor.RED)
            );
            return;
        }

        this.repository.searchByOwner(player.getUniqueId(), name)
            .thenAccept(stored -> {
                stored.ifPresentOrElse(
                    (waypoint) -> {
                        try {
                            this.trackController.startTracking(player, waypoint.model());
                        } catch (TrackNullWorldWaypointException e) {
                            e.printStackTrace(System.err);
                            player.sendMessage(
                                Component.text("¡Error! Has intentado rastrear un waypoint de un mundo (" + e.getWorldName() + ") que ya no existe.", NamedTextColor.RED)
                            );
                            return;
                        }
                        player.sendMessage(
                            Component
                                .text("¡Ahora te encuentras rastreando el waypoint \"" + waypoint.model().name() + "\"! Puedes dejar de hacerlo en cualquier momento usando el comando ", NamedTextColor.GREEN)
                                .append(
                                    Component
                                        .text("/waypoint untrack")
                                        .decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED)
                                        .clickEvent(ClickEvent.suggestCommand("/waypoint untrack"))
                                        .hoverEvent(HoverEvent.showText(Component.text("¡Click aquí para ejecutar este comando!")))
                                )
                        );
                    },
                    () -> {
                        player.sendMessage(
                            Component.text("¡No tienes ningún waypoint guardado con ese nombre!", NamedTextColor.RED)
                        );
                    }
                );
            })
            .exceptionally(throwable -> {
                throwable.printStackTrace(System.err);
                player.sendMessage(
                    Component.text("¡Ha ocurrido un error interno!", NamedTextColor.RED)
                );
                return null;
            });
    }

}
