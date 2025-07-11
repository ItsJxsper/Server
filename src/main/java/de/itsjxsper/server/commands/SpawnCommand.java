package de.itsjxsper.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.itsjxsper.server.Main;
import de.itsjxsper.server.utlis.ConfigUtil;
import de.itsjxsper.server.utlis.PrefixUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class SpawnCommand {

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("spawn")
                .executes(SpawnCommand::runSpawnLogicSelf)
                .then(Commands.argument("players", ArgumentTypes.player())
                        .executes(SpawnCommand::runSpawnLogicOther)
                        .suggests(SpawnCommand::getAmountSuggestions))
                .build();
    }

    private static int runSpawnLogicSelf(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();

        if (!(sender instanceof Player player)) {
            final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() + ConfigUtil.getString("message.commands.command.only-players"));
            sender.sendMessage(message);
            return Command.SINGLE_SUCCESS;
        }

        return runTeleportLogic(sender, player);
    }

    private static int runSpawnLogicOther(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();

        if (!(sender instanceof Player player)) {
            final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() + ConfigUtil.getString("message.commands.command.only-players"));
            sender.sendMessage(message);
            return Command.SINGLE_SUCCESS;
        }

        final PlayerSelectorArgumentResolver playerSelectorArgumentResolver = ctx.getArgument("players", PlayerSelectorArgumentResolver.class);
        final Player target = playerSelectorArgumentResolver.resolve(ctx.getSource()).getFirst();

        return runTeleportLogic(sender, target);
    }

    private static int runTeleportLogic(CommandSender sender, Player player) {
        player.teleport(player.getWorld().getSpawnLocation());
        final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() + ConfigUtil.getString("message.commands.spawn.success"));
        sender.sendMessage(message);
        return Command.SINGLE_SUCCESS;
    }

    private static CompletableFuture<Suggestions> getAmountSuggestions(final CommandContext<CommandSourceStack> ctx, final SuggestionsBuilder builder) {
        for (Player player : Main.getInstance().getServer().getOnlinePlayers()) {
            builder.suggest(player.getName()); // jeder Spielername einzeln vorgeschlagen
        }
        return builder.buildFuture();
    }
}
