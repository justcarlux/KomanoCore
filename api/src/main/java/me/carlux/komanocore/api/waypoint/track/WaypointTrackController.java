package me.carlux.komanocore.api.waypoint.track;

import me.carlux.komanocore.api.waypoint.Waypoint;
import me.carlux.komanocore.api.waypoint.exception.TrackNullWorldWaypointException;
import org.bukkit.entity.Player;

public interface WaypointTrackController {
    void init();
    void shutdown();

    void startTracking(Player player, Waypoint waypoint) throws TrackNullWorldWaypointException;
    WaypointTrackEntry stopTracking(Player player);
    boolean isTracking(Player player);
    void onTick();
}
