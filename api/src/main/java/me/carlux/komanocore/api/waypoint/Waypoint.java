package me.carlux.komanocore.api.waypoint;

import java.util.UUID;

public record Waypoint(
    String name,
    UUID owner,
    String worldName,
    double x,
    double y,
    double z
) {}
