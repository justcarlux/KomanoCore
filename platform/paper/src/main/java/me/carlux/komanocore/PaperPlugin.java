package me.carlux.komanocore;

import co.aikar.commands.Locales;
import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import me.carlux.komanocore.api.repository.RepositoryManager;
import me.carlux.komanocore.common.repository.RepositoryManagerImpl;
import me.carlux.komanocore.config.PluginConfig;
import me.carlux.komanocore.module.ModuleManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PaperPlugin extends JavaPlugin {

    private RepositoryManager repositories;
    private ModuleManager modules;
    private PaperCommandManager commandManager;
    private PluginConfig pluginConfig;

    @Override
    public void onEnable() {
        this.pluginConfig = new PluginConfig(this);
        this.getLogger().info("Successfully initialized plugin config");

        this.repositories = new RepositoryManagerImpl(this.pluginConfig.getConnectionInfo());
        this.repositories.init();
        this.getLogger().info("Successfully initialized plugin repositories");

        this.commandManager = new PaperCommandManager(this);
        this.commandManager.getLocales().setDefaultLocale(Locales.SPANISH);

        this.modules = new ModuleManager(this);
        this.modules.onEnable();
        this.getLogger().info("Successfully initialized plugin modules");
    }

    @Override
    public void onDisable() {
        if (this.modules != null) {
            this.modules.onDisable();
        }
        if (this.repositories != null) {
            this.repositories.shutdown();
        }
    }

}
