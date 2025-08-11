package de.itsjxsper.server.listeners;

import de.itsjxsper.server.Main;
import de.itsjxsper.server.database.DatabaseManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {
    private final Main main;

    private final DatabaseManager databaseManager;

    public PlayerRespawnListener(Main main) {
        this.main = main;
        this.databaseManager = main.getDatabaseManager();
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        event.getPlayer().getServer().getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (databaseManager.getNightvision(event.getPlayer().getUniqueId())) {
                event.getPlayer().addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.NIGHT_VISION, -1, 0, false, false));
            }
        }, 1L); // Delay to ensure the respawn location is set correctly
    }
}
