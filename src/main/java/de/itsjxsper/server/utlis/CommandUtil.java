package de.itsjxsper.server.utlis;

import de.itsjxsper.server.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandUtil {

    public void runInvincibilityTask(Player player) {
        if (!(ConfigUtil.getBoolean("config.invincibility.enabled")))
            return;
        Bukkit.getServer().getScheduler()
                .scheduleSyncDelayedTask(Main.getInstance(),
                        () -> player.setInvulnerable(false),
                        ConfigUtil.getInt("config.invincibility.duration") * 20L);
    }
}
