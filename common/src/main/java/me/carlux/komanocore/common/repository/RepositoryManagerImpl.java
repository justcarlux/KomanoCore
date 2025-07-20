package me.carlux.komanocore.common.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.carlux.komanocore.api.repository.ConnectionInfo;
import me.carlux.komanocore.api.repository.RepositoryManager;
import me.carlux.komanocore.api.waypoint.WaypointRepository;
import me.carlux.komanocore.common.waypoint.WaypointRepositoryImpl;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RepositoryManagerImpl implements RepositoryManager {

    private final ConnectionInfo info;
    private final HikariConfig config;
    private final WaypointRepository waypointRepository;
    private final ExecutorService executor;

    private final Map<String, String> driverMap = Map.of(
        "mariadb", "org.mariadb.jdbc.Driver",
        "mysql", "com.mysql.jdbc.Driver"
    );

    public RepositoryManagerImpl(ConnectionInfo info) {
        this.info = info;
        this.forceLoadDriver();

        this.config = new HikariConfig();
        this.setupHikariConfig();
        final HikariDataSource dataSource = new HikariDataSource(config);

        this.executor = Executors.newFixedThreadPool(10);
        this.waypointRepository = new WaypointRepositoryImpl(dataSource, this.executor);
    }

    private void forceLoadDriver() {
        try {
            final String driver = this.driverMap.get(this.info.vendorName());
            if (driver == null) throw new IllegalStateException("Invalid vendor name");
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("No database driver found for vendor name: " + info.vendorName());
        }
    }

    private void setupHikariConfig() {
        this.config.setJdbcUrl(
            MessageFormat.format("jdbc:{0}://{1}:{2}/{3}", info.vendorName(), info.host(), String.valueOf(info.port()), info.databaseName())
        );
        this.config.setUsername(info.username());
        this.config.setPassword(info.password());
        this.config.setMaximumPoolSize(10);

        // https://github.com/brettwooldridge/hikaricp/wiki/MYSQL-Configuration
        this.config.addDataSourceProperty("cachePrepStmts", "true");
        this.config.addDataSourceProperty("prepStmtCacheSize", "250");
        this.config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        this.config.addDataSourceProperty("useServerPrepStmts", "true");
    }

    public void init() {
        this.waypointRepository.init();
    }

    public void shutdown() {
        this.executor.shutdown();
    }

    @Override
    public WaypointRepository getWaypointRepository() {
        return this.waypointRepository;
    }

}
