package de.itsjxsper.server.listeners;

import de.itsjxsper.server.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportListener implements Listener {

    private final Main main;

    public PlayerTeleportListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        this.main.getDatabaseManager().addBackCoordinate(event.getPlayer().getUniqueId(), event.getFrom());
    }
}
