package com.nftworlds.wrldcommands.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.nftworlds.wallet.contracts.nftworlds.WRLD;
import com.nftworlds.wallet.objects.NFTPlayer;
import com.nftworlds.wallet.objects.Network;
import com.nftworlds.wrldcommands.WRLDPaymentsCommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class PaymentUtil {
    public static void payPlayerWithChecks(UUID uuid, double amount, String reason) {
        Player player = Bukkit.getPlayer(uuid);

        if (!canPayPlayer(player)) {
            // If user is already stored (not paying them)
            if (WRLDPaymentsCommands.getInstance().getConfigManager().isInConfig(uuid))
                return;

            // Storing user
            storePlayer(uuid, amount, reason);
            return;
        }

        if (player == null) return;

        forcePayPlayer(uuid, amount, reason);

        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                " \n&2&lPAYMENT NOTICE &aYou've been paid &f" + amount + " &a$WRLD token!"));
    }

    public static void payPlayers(Map<UUID, Double> players, String reason) {
        Map<UUID, Double> offlinePlayers = new HashMap<>();

        players.forEach((uuid, amount) -> {
            if (canPayPlayer(Bukkit.getPlayer(uuid))) {
                forcePayPlayer(uuid, amount, reason);
            } else {
                offlinePlayers.put(uuid, amount);
            }
        });

        WRLDPaymentsCommands.getInstance().getConfigManager().addOfflinePlayers(offlinePlayers, reason);
    }

    /**
     * Determines if the player can be paid, which
     * basically just makes sure the player is online
     * and has a wallet linked
     */
    public static boolean canPayPlayer(Player player) {
        boolean isOffline = player == null || !player.isOnline();

        if (isOffline) return false;

        if (!isWalletLinked(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    ChatColor.RED + "You cannot be paid until you link your wallet! Once you do that, relog!"));
            return false;
        }

        return true;
    }

    public static void forcePayPlayer(UUID uuid, double amount, String reason) {
        WRLDPaymentsCommands.getPayments().getNFTPlayer(uuid).getPrimaryWallet().payWRLD(amount,
                Network.POLYGON,
                reason);
    }

    public static boolean isWalletLinked(UUID uuid) {
        return NFTPlayer.getByUUID(uuid).isLinked();
    }

    private static void storePlayer(UUID uuid, double amount, String reason) {
        WRLDPaymentsCommands.getInstance().getConfigManager().addOfflinePlayer(uuid, amount, reason);
    }
}
