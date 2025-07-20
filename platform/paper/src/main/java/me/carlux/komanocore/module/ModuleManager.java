package me.carlux.komanocore.module;

import me.carlux.komanocore.PaperPlugin;
import me.carlux.komanocore.api.module.Module;
import me.carlux.komanocore.waypoint.WaypointModule;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager implements Module {

    private final List<Module> modules = new ArrayList<>();

    public ModuleManager(PaperPlugin plugin) {
        this.modules.add(new WaypointModule(plugin));
    }

    @Override
    public void onEnable() {
        this.modules.forEach(Module::onEnable);
    }

    @Override
    public void onDisable() {
        this.modules.forEach(Module::onDisable);
    }

}
