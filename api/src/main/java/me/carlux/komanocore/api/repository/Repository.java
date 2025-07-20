package me.carlux.komanocore.api.repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Repository<I, M> {
    void init();

    CompletableFuture<Optional<ModelWithId<I, M>>> findById(I id);
    CompletableFuture<List<ModelWithId<I, M>>> findAll();
    CompletableFuture<I> save(M model);
    CompletableFuture<Boolean> delete(ModelWithId<I, M> model);
}
