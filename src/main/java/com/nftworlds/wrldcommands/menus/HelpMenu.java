package com.nftworlds.wrldcommands.menus;

import com.nftworlds.wallet.objects.NFTPlayer;
import com.nftworlds.wallet.objects.Network;
import me.lucko.helper.Commands;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HelpMenu extends Gui {
    public HelpMenu(Player player) {
        super(player, 3, "Your NFT World Wallet");
    }

    @Override
    public void redraw() {
        // Filling background, doing this first so that I
        // can override the necessary slots
        fillWith(Item.builder(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)).build());
        setItem(13, getDisplayItem());
    }

    /**
     * There's 2 item return types here
     *
     * 1.) If the wallet is connected
     * 2.) If the wallet isn't connected
     */
    private Item getDisplayItem() {
        NFTPlayer nftPlayer = NFTPlayer.getByUUID(getPlayer().getUniqueId());
        boolean isLinked = nftPlayer.isLinked();

        Material material = isLinked ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK;
        String name = isLinked ? "&6&lWallet Info" : "&c&lERROR";
        String lore = isLinked ? "&7Balance: &f" + nftPlayer.getPrimaryWallet().getWRLDBalance(Network.POLYGON) : "&7Click to connect wallet";

        return ItemStackBuilder.of(material).name(name).lore(lore).build(() -> {
            if (!isLinked) {
                getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Link your wallet at &a&nhttps://nftworlds.com/login&r"));
                getPlayer().closeInventory();
            }
        });
    }

    public static void registerCommand() {
        Commands.create().assertPlayer().handler((c) -> {
            new HelpMenu(c.sender()).open();
        }).register("walletinfo");
    }
}
