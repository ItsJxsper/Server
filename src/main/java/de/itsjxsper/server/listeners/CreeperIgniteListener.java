package de.itsjxsper.server.listeners;

import com.destroystokyo.paper.ParticleBuilder;
import com.destroystokyo.paper.event.entity.CreeperIgniteEvent;
import de.itsjxsper.server.Main;
import de.itsjxsper.server.utlis.ConfigUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CreeperIgniteListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onCreeperIgnite(CreeperIgniteEvent event) {
        if (ConfigUtil.getBoolean("creeper-explosion")) {
            Bukkit.getServer().getScheduler()
                    .scheduleSyncDelayedTask(Main.getInstance(),() -> {
                        ParticleBuilder particleBuilder = new ParticleBuilder(Particle.ENCHANT);
                        particleBuilder.count(5);
                        particleBuilder.location(event.getEntity().getLocation());
                        particleBuilder.spawn();
                        event.getEntity().playSound(Sound.sound().type(Key.key("entity.wither.shoot")).source(Sound.Source.HOSTILE).volume(100).pitch(1L).build());
                        event.getEntity().remove();
                    }, 1L);
                    };
        }
}

