package me.carlux.komanocore.api.waypoint;

import me.carlux.komanocore.api.repository.ModelWithId;
import me.carlux.komanocore.api.repository.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface WaypointRepository extends Repository<Long, Waypoint> {
    CompletableFuture<List<ModelWithId<Long, Waypoint>>> findByOwner(UUID owner);
    CompletableFuture<Optional<ModelWithId<Long, Waypoint>>> searchByOwner(UUID owner, String name);
    Optional<Map<String, Boolean>> findCachedNamesByOwner(UUID owner);
}
