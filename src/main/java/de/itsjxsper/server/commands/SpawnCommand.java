package de.itsjxsper.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
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

public class SpawnCommand {
    public static LiteralCommandNode<CommandSourceStack> onCommand() {
        return Commands.literal("spawn")
                .requires(sender -> sender.getSender().hasPermission("server.spawn"))
                .executes(SpawnCommand::runSpawnLogicSelf)
                .then(Commands.argument("players", ArgumentTypes.player())
                        .executes(SpawnCommand::runSpawnLogicOther))
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
}
