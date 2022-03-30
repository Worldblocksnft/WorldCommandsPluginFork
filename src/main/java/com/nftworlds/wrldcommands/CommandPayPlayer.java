package com.nftworlds.wrldcommands;

import java.util.UUID;

import com.nftworlds.wrldcommands.util.PaymentUtil;

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

        UUID uuid = getUuidFromName(args[0]);
        double amount = Double.parseDouble(args[1]);
        StringBuilder reason = new StringBuilder(args[2]);
        for (int i = 3; i < args.length; i++) {
            reason.append(" ").append(args[i]);
        }

        PaymentUtil.payPlayer(uuid, amount, reason.toString());
        sender.sendMessage(ChatColor.GREEN + "Attempting to pay " + args[0] + " " + amount + " $WRLD!");
        return true;
    }

    private UUID getUuidFromName(String name) {
        Player player = Bukkit.getPlayer(name);
        return player != null ? player.getUniqueId() : Bukkit.getOfflinePlayerIfCached(name).getUniqueId();
    }
}
