package me.carlux.komanocore.waypoint.command;

import co.aikar.commands.annotation.*;
import lombok.RequiredArgsConstructor;
import me.carlux.komanocore.api.waypoint.WaypointRepository;
import me.carlux.komanocore.command.PluginCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@CommandAlias("waypoint")
public class WaypointRemoveCommand extends PluginCommand {

    private final WaypointRepository repository;

    @Subcommand("remove")
    @CommandCompletion("@listwaypoints @nothing")
    @Syntax("<nombre>")
    @Description("Te permite eliminar alguno de tus waypoints.")
    public void remove(CommandSender sender, String name) {
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

        this.repository.searchByOwner(player.getUniqueId(), name)
            .thenAccept(stored -> {
                stored.ifPresentOrElse(
                    (waypoint) -> {
                        this.repository.delete(waypoint)
                            .thenAccept(id -> {
                                player.sendMessage(
                                    Component.text("¡Waypoint \"" + waypoint.model().name() + "\" eliminado satisfactoriamente!", NamedTextColor.GREEN)
                                );
                            })
                            .exceptionally(throwable -> {
                                throwable.printStackTrace(System.err);
                                player.sendMessage(
                                    Component.text("¡Ha ocurrido un error interno al eliminar el waypoint!", NamedTextColor.RED)
                                );
                                return null;
                            });
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
