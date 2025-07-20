package me.carlux.komanocore.config;

import lombok.Getter;
import me.carlux.komanocore.PaperPlugin;
import me.carlux.komanocore.api.repository.ConnectionInfo;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class PluginConfig {

    private final FileConfiguration config;
    private final ConnectionInfo connectionInfo;

    public PluginConfig(PaperPlugin plugin) {
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
        this.connectionInfo = this.serializeConnectionInfo();
    }

    private ConnectionInfo serializeConnectionInfo() {
        final ConfigurationSection section = this.config.getConfigurationSection("db");
        if (section == null) {
            throw new IllegalStateException("Config section \"db\" was not found");
        }
        final String vendorName = section.getString("vendorName");
        if (vendorName == null || vendorName.isEmpty()) {
            throw new IllegalStateException("\"vendorName\" was not found in config section \"db\"");
        }
        final String host = section.getString("host");
        if (host == null || host.isEmpty()) {
            throw new IllegalStateException("\"host\" was not found in config section \"db\"");
        }
        final int port = section.getInt("port");
        if (port == 0) {
            throw new IllegalStateException("\"port\" was not properly specified in config section \"db\"");
        }
        final String databaseName = section.getString("databaseName");
        if (databaseName == null || databaseName.isEmpty()) {
            throw new IllegalStateException("\"databaseName\" was not found in config section \"db\"");
        }
        final String username = section.getString("username");
        if (username == null || username.isEmpty()) {
            throw new IllegalStateException("\"username\" was not found in config section \"db\"");
        }
        final String password = section.getString("password");
        if (password == null || password.isEmpty()) {
            throw new IllegalStateException("\"password\" was not found in config section \"db\"");
        }
        return new ConnectionInfo(vendorName, host, port, databaseName, username, password);
    }

}
