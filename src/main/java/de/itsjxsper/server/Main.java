package de.itsjxsper.server;

import de.itsjxsper.server.database.DatabaseManager;
import de.itsjxsper.server.listeners.*;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    @Getter
    private final DatabaseManager databaseManager = new DatabaseManager(this);

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        saveResource("sqlite.db", false);

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(this), this);
        pluginManager.registerEvents(new PlayerDeadListener(this), this);
        pluginManager.registerEvents(new PlayerTeleportListener(this), this);
        pluginManager.registerEvents(new PlayerQuitListener(), this);
        pluginManager.registerEvents(new EntityExplodeListener(), this);
        pluginManager.registerEvents(new PlayerDamageListener(), this);
        getLogger().info("Server Plugin is now enabled");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        databaseManager.disconnect();

        getLogger().info("Server Plugin is now disabled");
    }
}
