package me.carlux.komanocore.common.repository;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import me.carlux.komanocore.api.repository.Repository;

import java.util.concurrent.Executor;

@RequiredArgsConstructor
public abstract class SimpleRepository<I, M> implements Repository<I, M> {
    protected final HikariDataSource dataSource;
    protected final Executor executor;
}
