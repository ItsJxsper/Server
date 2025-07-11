package de.itsjxsper.server.listeners;

import de.itsjxsper.server.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeadListener implements Listener {

    private final Main main;

    public PlayerDeadListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        this.main.getDatabaseManager().addBackCoordinate(event.getEntity().getUniqueId(), event.getEntity().getLocation());
    }

}
