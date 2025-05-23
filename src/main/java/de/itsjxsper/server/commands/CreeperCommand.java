package de.itsjxsper.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.itsjxsper.server.utlis.ConfigUtil;
import de.itsjxsper.server.utlis.PrefixUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreeperCommand {

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("creeper")
                .requires(commandSourceStack -> {
                    if (!commandSourceStack.getSender().hasPermission("server.back")) {
                        final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() + ConfigUtil.getString("message.commands.command.no-permission"));
                        commandSourceStack.getSender().sendMessage(message);
                        return false;
                    }
                    return true;
                })
                .executes(CreeperCommand::runCreeperLogic)
                .build();
    }

    private static int runCreeperLogic(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();

        if (!(sender instanceof Player player)) {
            final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() + ConfigUtil.getString("message.commands.command.only-players"));
            sender.sendMessage(message);
            return Command.SINGLE_SUCCESS;
        }

        if (ConfigUtil.getBoolean("creeper-explosion")) {
            ConfigUtil.set("creeper-explosion", false);
            final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() + ConfigUtil.getString("message.commands.creeper.explosion-enabled"));
            player.sendMessage(message);
        } else {
            ConfigUtil.set("creeper-explosion", true);
            final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() + ConfigUtil.getString("message.commands.creeper.explosion-disabled"));
            player.sendMessage(message);
        }
        return Command.SINGLE_SUCCESS;
    }
}
