package me.carlux.komanocore.api.module;

public interface Module {
    default void onEnable() {};
    default void onDisable() {};
}
