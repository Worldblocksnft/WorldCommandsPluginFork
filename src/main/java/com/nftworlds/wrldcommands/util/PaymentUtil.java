package com.nftworlds.wrldcommands.util;

import java.util.UUID;

import com.nftworlds.wallet.objects.NFTPlayer;
import com.nftworlds.wallet.objects.Network;
import com.nftworlds.wrldcommands.WRLDPaymentsCommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class PaymentUtil {
    public static void payPlayer(UUID uuid, double amount, String reason) {
        // Running task later for 2 reasons
        // 1.) So the "isInConfig" check happens after player gets removed (in the
        // listener)
        // 2.) So the messages get sent to the user on login properly
        new BukkitRunnable() {
            @Override
            public void run() {
                // If user is already stored (not paying them)
                if (WRLDPaymentsCommands.getInstance().getConfigManager().isInConfig(uuid)) {
                    return;
                }

                Player player = Bukkit.getPlayer(uuid);
                boolean isOffline = player == null || !player.isOnline();

                if (isOffline) {
                    storePlayer(uuid, amount, reason);
                    return;
                }

                // Payment success message (I want this to send prior to the no link message)
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        " \n&2&lPAYMENT NOTICE &aYou've been paid &f" + amount + " &a$WRLD token!"));

                if (!isWalletLinked(uuid)) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            ChatColor.RED + "You cannot be paid until you link your wallet! Once you do that, relog!"));
                    storePlayer(uuid, amount, reason);
                    return;
                }

                // Finally paying the player
                WRLDPaymentsCommands.getPayments().getNFTPlayer(uuid).getPrimaryWallet().payWRLD(amount,
                        Network.POLYGON,
                        reason.toString());
            }

        }.runTaskLater(WRLDPaymentsCommands.getInstance(), 20L);
    }

    public static boolean isWalletLinked(UUID uuid) {
        return NFTPlayer.getByUUID(uuid).isLinked();
    }

    private static void storePlayer(UUID uuid, double amount, String reason) {
        WRLDPaymentsCommands.getInstance().getConfigManager().addOfflinePlayer(uuid, amount, reason);

    }
}
