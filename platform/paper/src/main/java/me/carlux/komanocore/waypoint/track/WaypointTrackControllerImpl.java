package me.carlux.komanocore.waypoint.track;

import lombok.RequiredArgsConstructor;
import me.carlux.komanocore.PaperPlugin;
import me.carlux.komanocore.api.waypoint.Waypoint;
import me.carlux.komanocore.api.waypoint.exception.TrackNullWorldWaypointException;
import me.carlux.komanocore.api.waypoint.track.WaypointTrackController;
import me.carlux.komanocore.api.waypoint.track.WaypointTrackEntry;
import me.carlux.komanocore.util.Format;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class WaypointTrackControllerImpl implements WaypointTrackController {

    private final Map<Player, WaypointTrackEntry> entries = new ConcurrentHashMap<>();
    private final PaperPlugin plugin;

    private BukkitTask task;

    @Override
    public void init() {
        this.task = this.plugin
            .getServer()
            .getScheduler()
            .runTaskTimerAsynchronously(this.plugin, this::onTick, 0, 20);
    }

    @Override
    public void shutdown() {
        if (this.task != null) {
            this.task.cancel();
        }
    }

    @Override
    public void startTracking(Player player, Waypoint waypoint) throws TrackNullWorldWaypointException {
        final World world = player.getServer().getWorld(waypoint.worldName());
        if (world == null) {
            throw new TrackNullWorldWaypointException(player, waypoint.worldName());
        }
        final WaypointTrackEntry entry = new WaypointTrackEntry(waypoint, new Location(world, waypoint.x(), waypoint.y(), waypoint.z()));
        this.entries.put(player, entry);
        this.displayTrackingMessage(player, entry);
    }

    @Override
    public WaypointTrackEntry stopTracking(Player player) {
        player.sendActionBar(Component.empty());
        return this.entries.remove(player);
    }

    @Override
    public boolean isTracking(Player player) {
        return this.entries.containsKey(player);
    }

    @Override
    public void onTick() {
        final Iterator<Map.Entry<Player, WaypointTrackEntry>> iterator = this.entries.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<Player, WaypointTrackEntry> entry = iterator.next();
            if (!entry.getKey().isValid()) {
                iterator.remove();
                continue;
            }
            this.displayTrackingMessage(entry.getKey(), entry.getValue());
        }
    }

    private void displayTrackingMessage(Player player, WaypointTrackEntry entry) {
        if (!player.getWorld().getName().equals(entry.waypoint().worldName())) {
            player.sendActionBar(Component.text("¡No te encuentras en el mundo de \"" + entry.waypoint().name() + "\"!", NamedTextColor.YELLOW));
            return;
        }
        final String distance = Format.distanceDecimal(player.getLocation().distance(entry.location()));
        final String blocks = distance.equals("1") ? "1 bloque" : distance + " bloques";
        player.sendActionBar(
            Component.text(this.computeDirectionArrow(player, entry.location()) + " Te encuentras a " + blocks + " de \"" + entry.waypoint().name() + "\"")
        );
    }

    private String computeDirectionArrow(Player player, Location targetLocation) {
        final Location playerLocation = player.getLocation();
        float playerYaw = playerLocation.getYaw();

        final Vector towardsTarget = targetLocation.toVector().subtract(playerLocation.toVector());
        towardsTarget.setY(0);
        if (towardsTarget.lengthSquared() == 0) return "⬆";

        double targetYaw = Math.toDegrees(Math.atan2(-towardsTarget.getX(), towardsTarget.getZ()));
        playerYaw = (playerYaw % 360 + 360) % 360;
        targetYaw = (targetYaw % 360 + 360) % 360;

        final double angleDifference = (targetYaw - playerYaw + 180) % 360 - 180;

        if (angleDifference > 22.5 && angleDifference <= 67.5) {
            return "↗";
        } else if (angleDifference > 67.5 && angleDifference <= 112.5) {
            return "➡";
        } else if (angleDifference > 112.5 && angleDifference <= 157.5) {
            return "↘";
        } else if (angleDifference > 157.5 || angleDifference <= -157.5) {
            return "⬇";
        } else if (angleDifference > -157.5 && angleDifference <= -112.5) {
            return "↙";
        } else if (angleDifference > -112.5 && angleDifference <= -67.5) {
            return "⬅";
        } else if (angleDifference > -67.5 && angleDifference <= -22.5) {
            return "↖";
        }

        return "⬆";
    }

}
