package com.nftworlds.wrldcommands.offlinepayments;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.nftworlds.wrldcommands.WRLDPaymentsCommands;
import com.nftworlds.wrldcommands.util.PaymentUtil;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class OfflineNFTPaymentListener implements Listener {
    private final Map<UUID, OfflineNFTPlayer> offlineNFTPlayers = new HashMap<>();

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        OfflineNFTPlayer nftPlayer = WRLDPaymentsCommands.getInstance().getConfigManager()
                .getOfflineNFTPlayer(uuid);

        if (nftPlayer == null)
            return;

        offlineNFTPlayers.put(uuid, nftPlayer);
    }

    /**
     * Highest priority so that it runs last
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        OfflineNFTPlayer nftPlayer = offlineNFTPlayers.get(uuid);

        if (nftPlayer == null)
            return;

        removeUser(nftPlayer);

        // Running task later for 2 reasons
        // 1.) So the "isInConfig" check happens after player gets removed
        // 2.) So the messages get sent to the user on login properly
        new BukkitRunnable() {
            @Override
            public void run() {
                payOfflineNFTPlayer(nftPlayer);
            }
        }.runTaskLater(WRLDPaymentsCommands.getInstance(), 20L);
    }

    private void payOfflineNFTPlayer(OfflineNFTPlayer nftPlayer) {
        UUID uuid = nftPlayer.getUuid();
        double amount = nftPlayer.getPayAmount();
        String reason = nftPlayer.getPayReason();

        PaymentUtil.payPlayer(uuid, amount, reason);
    }

    private void removeUser(OfflineNFTPlayer nftPlayer) {
        UUID uuid = nftPlayer.getUuid();

        if (offlineNFTPlayers.containsKey(uuid)) {
            offlineNFTPlayers.remove(uuid);

            // Only removing from file IF their wallet is linked
            if (PaymentUtil.isWalletLinked(uuid))
                WRLDPaymentsCommands.getInstance().getConfigManager().removeOfflinePlayer(nftPlayer);
        }
    }
}
