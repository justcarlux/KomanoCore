package me.carlux.komanocore.api.repository;

public record ConnectionInfo(
    String vendorName,
    String host,
    int port,
    String databaseName,
    String username,
    String password
) {}
