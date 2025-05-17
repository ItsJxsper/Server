package de.itsjxsper.server.listeners;

import de.itsjxsper.server.utlis.ConfigUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        final Component massage = MiniMessage.miniMessage().deserialize(ConfigUtil.getString("massage.join"), Placeholder.parsed("player", event.getPlayer().getName()));
        event.joinMessage(massage);
    }

}
