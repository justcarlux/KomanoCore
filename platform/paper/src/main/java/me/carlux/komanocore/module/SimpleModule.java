package me.carlux.komanocore.module;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import lombok.RequiredArgsConstructor;
import me.carlux.komanocore.PaperPlugin;
import me.carlux.komanocore.api.module.Module;
import me.carlux.komanocore.command.PluginCommand;
import me.carlux.komanocore.completion.PluginCommandCompletion;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public abstract class SimpleModule implements Module {

    protected final PaperPlugin plugin;

    @Override
    public void onEnable() {
        final PaperCommandManager commandManager = this.plugin.getCommandManager();
        this.getCommands().forEach(commandManager::registerCommand);

        final CommandCompletions<BukkitCommandCompletionContext> commandCompletions = commandManager.getCommandCompletions();
        this.getCommandCompletions().forEach(completion -> {
            commandCompletions.registerAsyncCompletion(completion.getId(), completion::handle);
        });
    }

    public Collection<PluginCommand> getCommands() {
        return List.of();
    }

    public Collection<PluginCommandCompletion> getCommandCompletions() {
        return List.of();
    }

}
