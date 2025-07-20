package me.carlux.komanocore.waypoint;

import me.carlux.komanocore.PaperPlugin;
import me.carlux.komanocore.api.waypoint.WaypointRepository;
import me.carlux.komanocore.api.waypoint.track.WaypointTrackController;
import me.carlux.komanocore.command.PluginCommand;
import me.carlux.komanocore.completion.PluginCommandCompletion;
import me.carlux.komanocore.module.SimpleModule;
import me.carlux.komanocore.waypoint.command.*;
import me.carlux.komanocore.waypoint.completion.ListWaypointsCompletion;
import me.carlux.komanocore.waypoint.track.WaypointTrackControllerImpl;

import java.util.Collection;
import java.util.List;

public class WaypointModule extends SimpleModule {

    private final WaypointRepository repository;
    private final WaypointTrackController trackController;
    public WaypointModule(PaperPlugin plugin) {
        super(plugin);
        this.repository = plugin.getRepositories().getWaypointRepository();
        this.trackController = new WaypointTrackControllerImpl(plugin);
   }

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
    public Collection<PluginCommand> getCommands() {
        return List.of(
            new WaypointCreateCommand(this.repository),
            new WaypointRemoveCommand(this.repository),
            new WaypointListCommand(this.repository),
            new WaypointTrackCommand(this.repository, this.trackController),
            new WaypointUntrackCommand(this.trackController)
        );
    }

    @Override
    public Collection<PluginCommandCompletion> getCommandCompletions() {
        return List.of(
            new ListWaypointsCompletion(this.repository)
        );
    }

}
