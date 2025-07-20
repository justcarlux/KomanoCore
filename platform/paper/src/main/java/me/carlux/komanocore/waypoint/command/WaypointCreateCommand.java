package me.carlux.komanocore.waypoint.command;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import lombok.RequiredArgsConstructor;
import me.carlux.komanocore.api.waypoint.Waypoint;
import me.carlux.komanocore.api.waypoint.WaypointRepository;
import me.carlux.komanocore.command.PluginCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor
@CommandAlias("waypoint")
public class WaypointCreateCommand extends PluginCommand {

    private final WaypointRepository repository;

    @Subcommand("create")
    @Syntax("<nombre>")
    @Description("Te permite crear un waypoint.")
    public void create(CommandSender sender, String name) {
        final Player player = this.requiresPlayer(sender);
        if (player == null) return;

        if (name.length() > 30) {
            player.sendMessage(
                Component.text("¡El nombre para el waypoint es demasiado largo!", NamedTextColor.RED)
            );
            return;
        }
        if (name.contains(" ")) {
            player.sendMessage(
                Component.text("¡El nombre para el waypoint no puede contener espacios!", NamedTextColor.RED)
            );
            return;
        }

        final UUID uuid = player.getUniqueId();
        this.repository.searchByOwner(uuid, name)
            .thenAccept(waypoint -> {
                if (waypoint.isPresent()) {
                    player.sendMessage(
                        Component.text("¡Ya tienes un waypoint creado con ese nombre!", NamedTextColor.RED)
                    );
                    return;
                }

                final Location location = player.getLocation();
                this.repository.save(new Waypoint(name, uuid, player.getWorld().getName(), location.getX(), location.getY(), location.getZ()))
                    .thenAccept(id -> {
                        player.sendMessage(
                            Component
                                .text("¡Waypoint \"" + name + "\" guardado satisfactoriamente! Ahora puedes usar ", NamedTextColor.GREEN)
                                .append(
                                    Component
                                        .text("/waypoint list")
                                        .decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED)
                                        .clickEvent(ClickEvent.suggestCommand("/waypoint list"))
                                        .hoverEvent(HoverEvent.showText(Component.text("¡Click aquí para ejecutar este comando!")))
                                )
                                .append(
                                    Component
                                        .text(" para verlo.", NamedTextColor.GREEN)
                                )
                        );
                    })
                    .exceptionally(throwable -> {
                        throwable.printStackTrace(System.err);
                        player.sendMessage(
                            Component.text("¡Ha ocurrido un error interno al guardar el waypoint!", NamedTextColor.RED)
                        );
                        return null;
                    });
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
