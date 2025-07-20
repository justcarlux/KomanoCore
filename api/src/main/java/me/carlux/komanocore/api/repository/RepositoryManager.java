package me.carlux.komanocore.api.repository;

import me.carlux.komanocore.api.waypoint.WaypointRepository;

public interface RepositoryManager {
    void init();
    void shutdown();

    WaypointRepository getWaypointRepository();
}
