package me.carlux.komanocore.completion;

import co.aikar.commands.BukkitCommandCompletionContext;
import lombok.Getter;

import java.util.Collection;

@Getter
public abstract class PluginCommandCompletion {

    private final String id;
    public PluginCommandCompletion(String id) {
        this.id = id;
    }

    public abstract Collection<String> handle(BukkitCommandCompletionContext context);

}
