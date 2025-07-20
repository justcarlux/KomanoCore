package me.carlux.komanocore.waypoint.completion;

import co.aikar.commands.BukkitCommandCompletionContext;
import me.carlux.komanocore.api.waypoint.WaypointRepository;
import me.carlux.komanocore.completion.PluginCommandCompletion;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ListWaypointsCompletion extends PluginCommandCompletion {

    private final WaypointRepository repository;
    public ListWaypointsCompletion(WaypointRepository repository) {
        super("listwaypoints");
        this.repository = repository;
    }

    @Override
    public Collection<String> handle(BukkitCommandCompletionContext context) {
        final Optional<Map<String, Boolean>> cachedNames = this.repository.findCachedNamesByOwner(context.getPlayer().getUniqueId());
        if (cachedNames.isEmpty()) return List.of();
        return cachedNames.get().keySet();
    }

}
