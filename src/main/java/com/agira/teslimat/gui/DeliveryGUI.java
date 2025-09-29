package com.agira.teslimat.gui;

import com.agira.teslimat.TeslimatPlugin;
import com.agira.teslimat.utils.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class DeliveryGUI {

    private final TeslimatPlugin plugin;

    public DeliveryGUI(TeslimatPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        String title = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("settings.gui.title", "&aTeslimat Menüsü"));

        Inventory inv = Bukkit.createInventory(player, 27, title);

        // filler
        Material fillerMat = Material.BLACK_STAINED_GLASS_PANE;
        ItemStack filler = new ItemStack(fillerMat);
        ItemMeta fMeta = filler.getItemMeta();
        if (fMeta != null) {
            fMeta.setDisplayName(" ");
            fMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            filler.setItemMeta(fMeta);
        }
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, filler);
        }

        // ürünler
        ConfigurationSection deliveries = plugin.getConfig().getConfigurationSection("deliveries");
        if (deliveries != null) {
            for (String id : deliveries.getKeys(false)) {
                int slot = deliveries.getInt(id + ".gui-slot", -1);
                String matName = deliveries.getString(id + ".item.material", "STONE");
                Material mat = Material.matchMaterial(matName);
                if (mat == null) mat = Material.STONE;

                String name = ChatColor.translateAlternateColorCodes('&',
                        deliveries.getString(id + ".item.name", "&fÜrün"));
                List<String> lore = new ArrayList<>();
                for (String line : deliveries.getStringList(id + ".item.lore")) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                lore.add("");
                lore.add(ChatColor.YELLOW + "Toplam Teslim: " + ChatColor.WHITE + DataManager.getTotalAmount(id));
                lore.add(ChatColor.GREEN + "Senin Teslimin: " + ChatColor.WHITE + DataManager.getPlayerAmount(id, player.getName()));
                lore.add(ChatColor.AQUA + "§eTıklayarak teslim et!");

                ItemStack stack = new ItemStack(mat, 1);
                ItemMeta meta = stack.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(name);
                    meta.setLore(lore);
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
                    stack.setItemMeta(meta);
                }

                if (slot >= 0 && slot < inv.getSize()) {
                    inv.setItem(slot, stack);
                }
            }
        }

        player.openInventory(inv);
    }
}
