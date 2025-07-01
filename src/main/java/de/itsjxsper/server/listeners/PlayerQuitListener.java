package de.itsjxsper.server.listeners;

import de.itsjxsper.server.commands.TpaCommand;
import de.itsjxsper.server.utlis.ConfigUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        TpaCommand.getTpaRequests().remove(event.getPlayer().getUniqueId());

        final Component message = MiniMessage.miniMessage().deserialize(ConfigUtil.getString("message.quit"), Placeholder.parsed("player", event.getPlayer().getName()));
        event.quitMessage(message);
    }
}
