package de.itsjxsper.server.listeners;

import de.itsjxsper.server.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerDeadListener implements Listener {

    private final Main main;

    public PlayerDeadListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        this.main.getDatabaseManager().addBackCoordinate(event.getEntity().getUniqueId(), event.getEntity().getLocation());

        if (main.getDatabaseManager().getNightvision(event.getPlayer().getUniqueId())) {
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
        }
    }

}
