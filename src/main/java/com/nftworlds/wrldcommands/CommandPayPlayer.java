package com.nftworlds.wrldcommands;

import java.util.UUID;

import com.nftworlds.wallet.objects.Network;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.md_5.bungee.api.ChatColor;

public class CommandPayPlayer implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!(args.length >= 3)) {
            sender.sendMessage("usage: /payplayer <player> <amount> <reason>");
            return false;
        }

        UUID payee = getUuidFromName(args[0]);
        double amount = Double.parseDouble(args[1]);
        StringBuilder reason = new StringBuilder(args[2]);
        for (int i = 3; i < args.length; i++) {
            reason.append(" ").append(args[i]);
        }

        // Taking offline player into consideration
        // The API doesn't currently function with
        // offline users
        if (Bukkit.getPlayer(args[0]) == null) {
            sender.sendMessage(ChatColor.GOLD
                    + "That user is offline! No need to worry! I've added them to storage for you! They will be payed when they login!");
            WRLDPaymentsCommands.getInstance().getConfigManager().addOfflinePlayer(payee, amount, reason.toString());
            return false;
        }

        WRLDPaymentsCommands.getPayments().getNFTPlayer(payee).getPrimaryWallet().payWRLD(amount, Network.POLYGON,
                reason.toString());
        sender.sendMessage(ChatColor.GREEN + args[0] + " has been paid " + amount + " $WRLD!");
        return true;
    }

    private UUID getUuidFromName(String name) {
        Player player = Bukkit.getPlayer(name);
        return player != null ? player.getUniqueId() : Bukkit.getOfflinePlayerIfCached(name).getUniqueId();
    }
}
