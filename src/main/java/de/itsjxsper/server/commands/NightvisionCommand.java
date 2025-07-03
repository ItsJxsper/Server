package de.itsjxsper.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.itsjxsper.server.Main;
import de.itsjxsper.server.database.DatabaseManager;
import de.itsjxsper.server.utlis.ConfigUtil;
import de.itsjxsper.server.utlis.PrefixUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NightvisionCommand {

    private final Main main;
    private static DatabaseManager databaseManager;

    public NightvisionCommand(Main main) {
        this.main = main;
        databaseManager = main.getDatabaseManager();
    }

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
       return Commands.literal("nightvision")
               .executes(NightvisionCommand::runNightvisionLogic)
               .build();
    }

    private static int runNightvisionLogic(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();

        if (!(sender instanceof Player player)) {
            final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() + ConfigUtil.getString("message.commands.command.only-players"));
            sender.sendMessage(message);
            return Command.SINGLE_SUCCESS;
        }

        boolean nightVisionEnabled = databaseManager.getNightvision(player.getUniqueId());

        if (nightVisionEnabled) {
            databaseManager.setNightvision(player.getUniqueId(), false);
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() + ConfigUtil.getString("message.commands.nightvision.disabled"));
            player.sendMessage(message);
        } else {
            databaseManager.setNightvision(player.getUniqueId(), true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
            final Component message = MiniMessage.miniMessage().deserialize(PrefixUtil.getPrefix() + ConfigUtil.getString("message.commands.nightvision.enabled"));
            player.sendMessage(message);
        }


        return Command.SINGLE_SUCCESS;
    }
}
