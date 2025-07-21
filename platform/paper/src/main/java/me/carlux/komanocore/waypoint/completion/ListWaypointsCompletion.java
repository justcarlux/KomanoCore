package me.carlux.komanocore.waypoint.completion;

import co.aikar.commands.BukkitCommandCompletionContext;
import me.carlux.komanocore.api.waypoint.WaypointRepository;
import me.carlux.komanocore.completion.PluginCommandCompletion;

import java.util.Collection;

public class ListWaypointsCompletion extends PluginCommandCompletion {

    private final WaypointRepository repository;
    public ListWaypointsCompletion(WaypointRepository repository) {
        super("listwaypoints");
        this.repository = repository;
    }

    @Override
    public Collection<String> handle(BukkitCommandCompletionContext context) {
        return this.repository.findCachedNamesByOwner(context.getPlayer().getUniqueId());
    }

}
