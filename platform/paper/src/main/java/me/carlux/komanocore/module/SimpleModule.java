package me.carlux.komanocore.module;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import lombok.RequiredArgsConstructor;
import me.carlux.komanocore.PaperPlugin;
import me.carlux.komanocore.api.module.Module;

import java.util.List;

@RequiredArgsConstructor
public abstract class SimpleModule implements Module {

    protected final PaperPlugin plugin;

    @Override
    public void onEnable() {
        this.getCommands()
            .forEach(command -> this.plugin.getCommandManager().registerCommand(command));
        this.registerCommandCompletions(this.plugin.getCommandManager().getCommandCompletions());
    }

    public List<BaseCommand> getCommands() {
        return List.of();
    }

    public void registerCommandCompletions(CommandCompletions<BukkitCommandCompletionContext> completions) {}

}
