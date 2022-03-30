package com.nftworlds.wrldcommands.offlinepayments;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.nftworlds.wallet.objects.Network;
import com.nftworlds.wrldcommands.WRLDPaymentsCommands;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

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

        payOfflineNFTPlayer(nftPlayer);
        removeUser(nftPlayer);
    }

    private void payOfflineNFTPlayer(OfflineNFTPlayer nftPlayer) {
        UUID uuid = nftPlayer.getUuid();
        double amount = nftPlayer.getPayAmount();
        String reason = nftPlayer.getPayReason();

        WRLDPaymentsCommands.getPayments().getNFTPlayer(uuid).getPrimaryWallet().payWRLD(amount, Network.POLYGON,
                reason.toString());
    }

    private void removeUser(OfflineNFTPlayer nftPlayer) {
        UUID uuid = nftPlayer.getUuid();

        if (offlineNFTPlayers.containsKey(uuid)) {
            offlineNFTPlayers.remove(uuid);
            WRLDPaymentsCommands.getInstance().getConfigManager().removeOfflinePlayer(nftPlayer);
        }
    }
}
