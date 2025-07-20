package me.carlux.komanocore.api.waypoint.track;

import me.carlux.komanocore.api.waypoint.Waypoint;
import org.bukkit.Location;

public record WaypointTrackEntry(
    Waypoint waypoint,
    Location location
) {}