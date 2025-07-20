package me.carlux.komanocore.waypoint;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import me.carlux.komanocore.PaperPlugin;
import me.carlux.komanocore.api.waypoint.WaypointRepository;
import me.carlux.komanocore.api.waypoint.track.WaypointTrackController;
import me.carlux.komanocore.module.SimpleModule;
import me.carlux.komanocore.waypoint.command.*;
import me.carlux.komanocore.waypoint.track.WaypointTrackControllerImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WaypointModule extends SimpleModule {

    private final WaypointRepository repository;
    private final WaypointTrackController trackController;
    public WaypointModule(PaperPlugin plugin) {
        super(plugin);
        this.repository = plugin.getRepositories().getWaypointRepository();
        this.trackController = new WaypointTrackControllerImpl(plugin);
;   }

    @Override
    public void onEnable() {
        super.onEnable();
        this.trackController.init();
    }

    @Override
    public void onDisable() {
        this.trackController.shutdown();
    }

    @Override
    public List<BaseCommand> getCommands() {
        return List.of(
            new WaypointCreateCommand(this.repository),
            new WaypointRemoveCommand(this.repository),
            new WaypointListCommand(this.repository),
            new WaypointTrackCommand(this.repository, this.trackController),
            new WaypointUntrackCommand(this.trackController)
        );
    }

    @Override
    public void registerCommandCompletions(CommandCompletions<BukkitCommandCompletionContext> completions) {
        completions.registerAsyncCompletion("listwaypoints", (context -> {
            final Optional<Map<String, Boolean>> cachedNames = this.repository.findCachedNamesByOwner(context.getPlayer().getUniqueId());
            if (cachedNames.isEmpty()) return List.of();
            return cachedNames.get().keySet();
        }));
    }

}
