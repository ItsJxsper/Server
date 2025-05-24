package de.itsjxsper.server.listeners;

import com.destroystokyo.paper.ParticleBuilder;
import de.itsjxsper.server.utlis.ConfigUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Particle;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplodeListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onCreeperIgnite(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof Creeper)) {
            return;
        }

        if (ConfigUtil.getBoolean("creeper-explosion")) {
            event.setCancelled(true);
            ParticleBuilder particleBuilder = new ParticleBuilder(Particle.ENCHANT);
            particleBuilder.count(5);
            particleBuilder.location(event.getEntity().getLocation());
            particleBuilder.spawn();
            event.getEntity().playSound(Sound.sound().type(Key.key("entity.wither.shoot")).source(Sound.Source.HOSTILE).volume(100).pitch(1.0f).build());
            event.getEntity().remove();
        } // Überflüssiges Semikolon entfernt
    }
}
