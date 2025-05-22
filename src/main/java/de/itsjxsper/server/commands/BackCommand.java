package de.itsjxsper.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.itsjxsper.server.Main;
import de.itsjxsper.server.utlis.CommandUtil;
import de.itsjxsper.server.utlis.ConfigUtil;
import de.itsjxsper.server.utlis.PrefixUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackCommand {

    private static final CommandUtil commandUtil = new CommandUtil();

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("back")
                .requires(commandSourceStack -> {
                    if (!commandSourceStack.getSender().hasPermission("server.back")) {
                        final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() + ConfigUtil.getString("message.commands.command.no-permission"));
                        commandSourceStack.getSender().sendMessage(message);
                        return false;
                    }
                    return true;
                })
                .executes(BackCommand::runBackTeleportLogicSelf)
                .then(Commands.argument("players", ArgumentTypes.player())
                        .executes(BackCommand::runBackTeleportLogicOther))
                .build();
    }

    private static int runBackTeleportLogicSelf(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();

        if (!(sender instanceof Player player)) {
            final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() + ConfigUtil.getString("message.commands.command.only-players"));
            sender.sendMessage(message);
            return Command.SINGLE_SUCCESS;
        }

        return runBackTeleportLogic(sender, player);
    }

    private static int runBackTeleportLogicOther(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();

        if (!(sender instanceof Player player)) {
            final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() + ConfigUtil.getString("message.commands.command.only-players"));
            sender.sendMessage(message);
            return Command.SINGLE_SUCCESS;
        }

        final PlayerSelectorArgumentResolver playerSelectorArgumentResolver = ctx.getArgument("players", PlayerSelectorArgumentResolver.class);
        final Player target = playerSelectorArgumentResolver.resolve(ctx.getSource()).getFirst();

        return runBackTeleportLogic(sender, target);
    }

    private static int runBackTeleportLogic(CommandSender sender, Player player) {
        Location location = Main.getInstance().getDatabaseManager().getBackLocation(player.getUniqueId());
        if (location == null) {
            final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() + ConfigUtil.getString("message.commands.back.no-back-coordinates"));
            sender.sendMessage(message);
            return Command.SINGLE_SUCCESS;
        }

        player.teleport(location);
        player.setInvulnerable(true);
        commandUtil.runInvincibilityTask(player);
        final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() + ConfigUtil.getString("message.commands.back.success"));
        sender.sendMessage(message);
        return Command.SINGLE_SUCCESS;
    }
}
