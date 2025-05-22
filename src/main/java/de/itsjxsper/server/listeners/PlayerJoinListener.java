package de.itsjxsper.server.listeners;

import de.itsjxsper.server.Main;
import de.itsjxsper.server.database.DatabaseManager;
import de.itsjxsper.server.utlis.ConfigUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinListener implements Listener {

    private final DatabaseManager databaseManager = Main.getInstance().getDatabaseManager();

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        final Component message = MiniMessage.miniMessage().deserialize(ConfigUtil.getString("message.join"), Placeholder.parsed("player", event.getPlayer().getName()));
        event.joinMessage(message);

        databaseManager.addBackCoordinate(event.getPlayer().getUniqueId(), event.getPlayer().getLocation());
        databaseManager.addPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName());
    }

}
