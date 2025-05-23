package de.itsjxsper.server;

import de.itsjxsper.server.commands.BackCommand;
import de.itsjxsper.server.commands.CreeperCommand;
import de.itsjxsper.server.commands.SpawnCommand;
import de.itsjxsper.server.commands.TpaCommand;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

public class MainBootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(BackCommand.createCommand());
            commands.registrar().register(SpawnCommand.createCommand());
            commands.registrar().register(TpaCommand.createTpaCommand());
            commands.registrar().register(TpaCommand.createTpaAcceptCommand());
            commands.registrar().register(TpaCommand.createTpaDenyCommand());
            commands.registrar().register(CreeperCommand.createCommand());
        });
    }
}
