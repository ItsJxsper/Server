package de.itsjxsper.server;

import de.itsjxsper.server.listeners.PlayerJoinListener;
import de.itsjxsper.server.listeners.PlayerQuitListener;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    @Override
    public void onLoad() {
        instance = this;

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "lobby:main");
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new PlayerQuitListener(), this);

        getLogger().info("Server Plugin is now enabled");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Server Plugin is now disabled");
    }
}
