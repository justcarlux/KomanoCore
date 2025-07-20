package me.carlux.komanocore.waypoint.command;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import lombok.RequiredArgsConstructor;
import me.carlux.komanocore.api.repository.ModelWithId;
import me.carlux.komanocore.api.waypoint.Waypoint;
import me.carlux.komanocore.api.waypoint.WaypointRepository;
import me.carlux.komanocore.command.PluginCommand;
import me.carlux.komanocore.util.Format;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.UUID;

@RequiredArgsConstructor
@CommandAlias("waypoint")
public class WaypointListCommand extends PluginCommand {

    private final WaypointRepository repository;

    @Subcommand("list")
    @Description("Muestra los waypoints que tengas guardados.")
    public void list(CommandSender sender) {
        final Player player = this.requiresPlayer(sender);
        if (player == null) return;

        final UUID uuid = player.getUniqueId();
        this.repository.findByOwner(uuid)
            .thenAccept(waypoints -> {
                if (waypoints.isEmpty()) {
                    player.sendMessage(
                        Component.text("¡No tienes ningún waypoint guardado!", NamedTextColor.RED)
                    );
                    return;
                }
                TextComponent message =
                    Component
                        .empty()
                        .append(START_BLUE_LINE)
                        .append(Component.text("Tus waypoints (" + waypoints.size() + "):", NamedTextColor.YELLOW, TextDecoration.BOLD))
                        .appendNewline();
                for (ModelWithId<Long, Waypoint> waypoint : waypoints) {
                    message = message.appendNewline();
                    message = message.append(
                        Component
                            .text("• ", NamedTextColor.YELLOW)
                            .decoration(TextDecoration.BOLD, false)
                            .append(
                                Component
                                    .text(waypoint.model().name(), NamedTextColor.WHITE)
                                    .append(
                                        Component.text(
                                            MessageFormat.format(
                                                " (en \"{0}\" ➡ {1}, {2}, {3})",
                                                waypoint.model().worldName(),
                                                Format.coordinateDecimal(waypoint.model().x()),
                                                Format.coordinateDecimal(waypoint.model().y()),
                                                Format.coordinateDecimal(waypoint.model().z())
                                            ),
                                            NamedTextColor.YELLOW
                                        )
                                    )
                                    .clickEvent(ClickEvent.suggestCommand("/waypoint track " + waypoint.model().name()))
                                    .hoverEvent(HoverEvent.showText(Component.text("¡Click aquí para rastrear este waypoint!")))
                            )
                    );
                    message = message.appendSpace();
                    message = message.append(
                        Component
                            .text("✖", NamedTextColor.RED)
                            .clickEvent(ClickEvent.suggestCommand("/waypoint remove " + waypoint.model().name()))
                            .hoverEvent(HoverEvent.showText(Component.text("¡Click aquí para eliminar este waypoint!")))
                    );
                }
                message = message.append(END_BLUE_LINE);
                player.sendMessage(message);
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
