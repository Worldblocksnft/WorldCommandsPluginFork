package com.nftworlds.wrldcommands.offlinepayments;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Easiest way to handle the offline player storage
 * 
 * Will update to SQL if this needs to exist on more
 * than 1 server instance
 */
public class ConfigManager {
    private static final String SECTION = "Players";
    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }

    public boolean isInConfig(UUID uuid) {
        return getOfflineNFTPlayer(uuid) != null;
    }

    public OfflineNFTPlayer getOfflineNFTPlayer(UUID uuid) {
        List<String> offlinePlayerList = getOfflinePlayers();

        if (offlinePlayerList.isEmpty() || offlinePlayerList == null)
            return null;

        for (String user : offlinePlayerList) {
            String[] args = user.split(":");
            String offlineUuidString = args[0];

            if (!offlineUuidString.equalsIgnoreCase(uuid.toString()))
                continue;

            return new OfflineNFTPlayer(UUID.fromString(offlineUuidString), Double.parseDouble(args[1]), args[2]);
        }

        return null;
    }

    /**
     * Adding an offline player, so that
     * I can pay them later when they login
     */
    public void addOfflinePlayer(UUID uuid, double amount, String reason) {
        List<String> offlinePlayers = getOfflinePlayers();
        offlinePlayers.add(uuid.toString() + ":" + amount + ":" + reason);
        setConfigList(offlinePlayers);
    }

    /**
     * Removes user from config list
     */
    public void removeOfflinePlayer(OfflineNFTPlayer player) {
        List<String> offlinePlayers = config.getStringList(SECTION);
        offlinePlayers.remove(convertOfflineNFTPlayerToConfigString(player));
        setConfigList(offlinePlayers);
    }

    private String convertOfflineNFTPlayerToConfigString(OfflineNFTPlayer nftPlayer) {
        return nftPlayer.getUuid().toString() + ":" + nftPlayer.getPayAmount() + ":" + nftPlayer.getPayReason();
    }

    /**
     * Getting players that should be paid
     */
    private final List<String> getOfflinePlayers() {
        List<String> offlinePlayers = config.getStringList(SECTION);

        if (offlinePlayers.isEmpty() || offlinePlayers == null)
            return new ArrayList<>();

        return config.getStringList(SECTION);
    }

    /**
     * Updates the config list (and saves it)
     */
    private void setConfigList(List<String> list) {
        runAsync(() -> {
            config.set(SECTION, list);
            plugin.saveConfig();
        });
    }

    private void runAsync(Runnable runnable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskAsynchronously(plugin);
    }
}
