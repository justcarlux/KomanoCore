package me.carlux.komanocore.api.waypoint.exception;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class TrackNullWorldWaypointException extends Exception {

    private final Player player;
    private final String worldName;
    public TrackNullWorldWaypointException(Player player, String worldName) {
        super("The player " + player + " tried to track a waypoint to a non-existent world: " + worldName);
        this.player = player;
        this.worldName = worldName;
    }

}
