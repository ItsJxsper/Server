package de.itsjxsper.server.listeners;

import de.itsjxsper.server.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportListener implements Listener {

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Main.getInstance().getDatabaseManager().addBackCoordinate(event.getPlayer().getUniqueId(), event.getFrom());
    }
}
