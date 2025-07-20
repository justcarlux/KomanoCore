package me.carlux.komanocore.api.repository;

public record ModelWithId<I, M>(I id, M model) {}
