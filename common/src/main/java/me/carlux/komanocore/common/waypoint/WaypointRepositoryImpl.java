package me.carlux.komanocore.common.waypoint;

import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import me.carlux.komanocore.api.repository.ModelWithId;
import me.carlux.komanocore.api.waypoint.Waypoint;
import me.carlux.komanocore.api.waypoint.WaypointRepository;
import me.carlux.komanocore.common.repository.SimpleRepository;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class WaypointRepositoryImpl extends SimpleRepository<Long, Waypoint> implements WaypointRepository {

    private final Map<UUID, Map<String, Boolean>> waypointNameCache = new ConcurrentHashMap<>();

    public WaypointRepositoryImpl(HikariDataSource dataSource, Executor executor) {
        super(dataSource, executor);
    }

    @SneakyThrows
    @Override
    public void init() {
        final String sql =
            "CREATE TABLE IF NOT EXISTS waypoints (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "name VARCHAR(255) NOT NULL, " +
            "owner VARCHAR(255) NOT NULL, " +
            "worldName VARCHAR(255) NOT NULL, " +
            "x DOUBLE NOT NULL, " +
            "y DOUBLE NOT NULL, " +
            "z DOUBLE NOT NULL" +
            ")";
        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }

        this.findAll().get().forEach(waypoint -> this.cacheName(waypoint.model()));
    }

    private void cacheName(Waypoint waypoint) {
        final Map<String, Boolean> map = this.waypointNameCache
            .computeIfAbsent(waypoint.owner(), k -> new ConcurrentHashMap<>());
        map.put(waypoint.name(), true);
    }

    private void uncacheName(Waypoint waypoint) {
        final Map<String, Boolean> map = this.waypointNameCache.get(waypoint.owner());
        if (map != null) {
            map.remove(waypoint.name());
        }
    }

    @Override
    public CompletableFuture<List<ModelWithId<Long, Waypoint>>> findByOwner(UUID owner) {
        return CompletableFuture.supplyAsync(() -> {
            final String sql = "SELECT id, name, owner, worldName, x, y, z FROM waypoints WHERE owner = ?";
            try (final Connection connection = dataSource.getConnection();
                 final PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, owner.toString());
                try (final ResultSet resultSet = statement.executeQuery()) {
                    final List<ModelWithId<Long, Waypoint>> waypoints = new ArrayList<>();
                    while (resultSet.next()) {
                        waypoints.add(this.wrapResultSet(resultSet));
                    }
                    return waypoints;
                }
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        }, this.executor);
    }

    @Override
    public CompletableFuture<Optional<ModelWithId<Long, Waypoint>>> searchByOwner(UUID owner, String name) {
        return CompletableFuture.supplyAsync(() -> {
            final String sql = "SELECT id, name, owner, worldName, x, y, z FROM waypoints WHERE owner = ? AND name = ?";
            try (final Connection connection = dataSource.getConnection();
                 final PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, owner.toString());
                statement.setString(2, name);
                try (final ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return Optional.of(this.wrapResultSet(resultSet));
                    }
                    return Optional.empty();
                }
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        }, this.executor);
    }

    @Override
    public Optional<Map<String, Boolean>> findCachedNamesByOwner(UUID owner) {
        return Optional.ofNullable(this.waypointNameCache.get(owner));
    }

    @Override
    public CompletableFuture<Optional<ModelWithId<Long, Waypoint>>> findById(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            final String sql = "SELECT id, name, owner, worldName, x, y, z FROM waypoints WHERE id = ?";
            try (final Connection connection = dataSource.getConnection();
                 final PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);
                try (final ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return Optional.of(this.wrapResultSet(resultSet));
                    }
                    return Optional.empty();
                }
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        }, this.executor);
    }

    @Override
    public CompletableFuture<List<ModelWithId<Long, Waypoint>>> findAll() {
        return CompletableFuture.supplyAsync(() -> {
            final String sql = "SELECT id, name, owner, worldName, x, y, z FROM waypoints";
            try (final Connection connection = dataSource.getConnection();
                 final Statement statement = connection.createStatement();
                 final ResultSet resultSet = statement.executeQuery(sql)) {
                final List<ModelWithId<Long, Waypoint>> waypoints = new ArrayList<>();
                while (resultSet.next()) {
                    waypoints.add(this.wrapResultSet(resultSet));
                }
                return waypoints;
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        }, this.executor);
    }

    private ModelWithId<Long, Waypoint> wrapResultSet(ResultSet set) throws SQLException {
        return new ModelWithId<>(
            set.getLong("id"),
            new Waypoint(
                set.getString("name"),
                UUID.fromString(set.getString("owner")),
                set.getString("worldName"),
                set.getDouble("x"),
                set.getDouble("y"),
                set.getDouble("z")
            )
        );
    }

    @Override
    public CompletableFuture<Long> save(Waypoint model) {
        return CompletableFuture.supplyAsync(() -> {
            final String sql = "INSERT INTO waypoints (name, owner, worldName, x, y, z) VALUES (?, ?, ?, ?, ?, ?)";
            try (final Connection connection = dataSource.getConnection();
                 final PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, model.name());
                statement.setString(2, model.owner().toString());
                statement.setString(3, model.worldName());
                statement.setDouble(4, model.x());
                statement.setDouble(5, model.y());
                statement.setDouble(6, model.z());
                statement.executeUpdate();
                try (final ResultSet resultSet = statement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        this.cacheName(model);
                        return resultSet.getLong(1);
                    }
                }
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
            throw new CompletionException(new IllegalStateException("No generated ID was retrieved"));
        }, this.executor);
    }

    @Override
    public CompletableFuture<Boolean> delete(ModelWithId<Long, Waypoint> waypoint) {
        return CompletableFuture.supplyAsync(() -> {
            final String sql = "DELETE FROM waypoints WHERE id = ?";
            try (final Connection conn = dataSource.getConnection();
                 final PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, waypoint.id());
                this.uncacheName(waypoint.model());
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new CompletionException(e);
            }
        }, this.executor);
    }

}
