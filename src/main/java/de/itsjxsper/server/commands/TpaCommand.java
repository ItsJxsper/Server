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
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpaCommand {
    // Store teleport requests: key = target player UUID, value = requesting player UUID
    private static final Map<UUID, UUID> tpaRequests = new HashMap<>();

    public static LiteralCommandNode<CommandSourceStack> createTpaCommand() {
        return Commands.literal("tpa")
                .requires(commandSourceStack -> {
                    if (!commandSourceStack.getSender().hasPermission("server.tpa")) {
                        final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() + ConfigUtil.getString("message.commands.command.no-permission"));
                        commandSourceStack.getSender().sendMessage(message);
                        return false;
                    }
                    return true;
                })
                .then(Commands.argument("players", ArgumentTypes.player())
                        .executes(TpaCommand::runTpa))
                .build();
    }

    public static LiteralCommandNode<CommandSourceStack> createTpaAcceptCommand() {
        return Commands.literal("tpaccept")
                .requires(commandSourceStack -> {
                    if (!commandSourceStack.getSender().hasPermission("server.tpa")) {
                        final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() + ConfigUtil.getString("message.commands.command.no-permission"));
                        commandSourceStack.getSender().sendMessage(message);
                        return false;
                    }
                    return true;
                })
                .executes(TpaCommand::runTpaccept)
                .build();
    }

    public static LiteralCommandNode<CommandSourceStack> createTpaDenyCommand() {
        return Commands.literal("tpadeny")
                .requires(commandSourceStack -> {
                    if (!commandSourceStack.getSender().hasPermission("server.tpa")) {
                        final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() +
                                ConfigUtil.getString("message.commands.command.no-permission"));
                        commandSourceStack.getSender().sendMessage(message);
                        return false;
                    }
                    return true;
                })
                .executes(TpaCommand::runTpdeny)
                .build();
    }

    private static int runTpa(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();


        if (!(sender instanceof Player player)) {
            final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() +
                    ConfigUtil.getString("message.commands.command.only-players"));
            sender.sendMessage(message);
            return Command.SINGLE_SUCCESS;
        }

        if (tpaRequests.containsKey(player.getUniqueId())) {
            final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() +
                    ConfigUtil.getString("message.commands.tpa.already-requested"));
            sender.sendMessage(message);
            return Command.SINGLE_SUCCESS;
        }

        final PlayerSelectorArgumentResolver playerSelectorArgumentResolver = ctx.getArgument("players", PlayerSelectorArgumentResolver.class);
        final Player target = playerSelectorArgumentResolver.resolve(ctx.getSource()).getFirst();

        // Prevent teleporting to self
        if (player.equals(target)) {
            final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() +
                    ConfigUtil.getString("message.commands.tpa.cannot-teleport-to-self"));
            sender.sendMessage(message);
            return Command.SINGLE_SUCCESS;
        }

        // Store the teleport request
        tpaRequests.put(target.getUniqueId(), player.getUniqueId());

        // Send teleport request messages
        final Component requestMessage = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() +
                ConfigUtil.getString("message.commands.tpa.request-sent"), Placeholder.parsed("player", target.getName()));
        sender.sendMessage(requestMessage);

        final Component targetMessage = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() +
                ConfigUtil.getString("message.commands.tpa.request-received"), Placeholder.parsed("player", player.getName()));
        target.sendMessage(targetMessage);

        return Command.SINGLE_SUCCESS;
    }

    private static int runTpaccept(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();

        if (!(sender instanceof Player player)) {
            final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() +
                    ConfigUtil.getString("message.commands.command.only-players"));
            sender.sendMessage(message);
            return Command.SINGLE_SUCCESS;
        }

        // Check if there's a pending request for this player
        UUID requesterUUID = tpaRequests.get(player.getUniqueId());
        if (requesterUUID == null) {
            final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() +
                    ConfigUtil.getString("message.commands.tpa.no-pending-requests"));
            sender.sendMessage(message);
            return Command.SINGLE_SUCCESS;
        }

        // Get the requester player
        Player requester = player.getServer().getPlayer(requesterUUID);
        if (requester == null || !requester.isOnline()) {
            final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() +
                    ConfigUtil.getString("message.commands.tpa.player-offline"));
            sender.sendMessage(message);
            tpaRequests.remove(player.getUniqueId());
            return Command.SINGLE_SUCCESS;
        }

        // Teleport the requester to the player
        requester.teleport(player.getLocation());


        // Send success messages
        final Component acceptMessage = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() +
                ConfigUtil.getString("message.commands.tpa.request-accepted"), Placeholder.parsed("player", requester.getName()));
        sender.sendMessage(acceptMessage);

        final Component teleportMessage = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() +
                ConfigUtil.getString("message.commands.tpa.teleport-success"), Placeholder.parsed("player", player.getName()));
        requester.sendMessage(teleportMessage);

        // Remove the request
        tpaRequests.remove(player.getUniqueId());

        return Command.SINGLE_SUCCESS;
    }

    private static int runTpdeny(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();

        if (!(sender instanceof Player player)) {
            final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() +
                    ConfigUtil.getString("message.commands.command.only-players"));
            sender.sendMessage(message);
            return Command.SINGLE_SUCCESS;
        }

        // Check if there's a pending request for this player
        UUID requesterUUID = tpaRequests.get(player.getUniqueId());
        if (requesterUUID == null) {
            final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() +
                    ConfigUtil.getString("message.commands.tpa.no-pending-requests"));
            sender.sendMessage(message);
            return Command.SINGLE_SUCCESS;
        }

        // Get the requester player
        Player requester = player.getServer().getPlayer(requesterUUID);

        // Send denial messages
        final Component denyMessage = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() +
                ConfigUtil.getString("message.commands.tpa.request-declined"), Placeholder.parsed("player", player.getName()));
        sender.sendMessage(denyMessage);

        if (requester != null && requester.isOnline()) {
            final Component rejectedMessage = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() +
                    ConfigUtil.getString("message.commands.tpa.request-rejected"), Placeholder.parsed("player", player.getName()));
            requester.sendMessage(rejectedMessage);
        }

        // Remove the request
        tpaRequests.remove(player.getUniqueId());

        return Command.SINGLE_SUCCESS;
    }
}
