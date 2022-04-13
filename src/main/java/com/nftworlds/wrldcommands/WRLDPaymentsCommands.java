package com.nftworlds.wrldcommands;

import com.nftworlds.wallet.api.WalletAPI;
import com.nftworlds.wrldcommands.menus.HelpMenu;
import com.nftworlds.wrldcommands.offlinepayments.ConfigManager;
import com.nftworlds.wrldcommands.offlinepayments.OfflineNFTPaymentListener;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class WRLDPaymentsCommands extends JavaPlugin {
    private static WRLDPaymentsCommands plugin;
    private static WalletAPI wallet;

    public static WRLDPaymentsCommands getInstance() {
        return plugin;
    }

    public static WalletAPI getPayments() {
        return wallet;
    }

    private ConfigManager configManager;

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void onEnable() {
        plugin = this;
        wallet = new WalletAPI();
        configManager = new ConfigManager(this);

        Objects.requireNonNull(getCommand("createrequest")).setExecutor(new CommandServerRequestWRLD());
        Objects.requireNonNull(getCommand("listrequests")).setExecutor(new CommandListPendingRequests());
        Objects.requireNonNull(getCommand("simulatetx")).setExecutor(new CommandSimulateWRLDTransaction());
        Objects.requireNonNull(getCommand("payplayer")).setExecutor(new CommandPayPlayer());

        getServer().getPluginManager().registerEvents(new PlayerTransactEventListener(), this);
        getServer().getPluginManager().registerEvents(new OfflineNFTPaymentListener(), this);

        getServer().getLogger().info("NFT Worlds WRLD Payments Commands enabled!");
        HelpMenu.registerCommand();
    }
}
