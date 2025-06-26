package de.itsjxsper.server.listeners;

import de.itsjxsper.server.utlis.ConfigUtil;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        // Check if the damage is fatal
        DamageSource damageSource = event.getDamageSource();

        if (!ConfigUtil.getBoolean("creeper-explosion")) {
            return;
        }
        if (damageSource instanceof Creeper) {
            event.setCancelled(true);
        }
    }
}
