package com.agira.teslimat.gui;

import com.agira.teslimat.TeslimatPlugin;
import com.agira.teslimat.utils.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TopGUI {

    private final TeslimatPlugin plugin;

    public TopGUI(TeslimatPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        String title = ChatColor.translateAlternateColorCodes('&', "&6Teslimat Sıralamaları");
        Inventory inv = Bukkit.createInventory(player, 27, title);

        ConfigurationSection deliveries = plugin.getConfig().getConfigurationSection("deliveries");
        if (deliveries != null) {
            int slot = 10; // ürünleri koymaya başlama slotu
            for (String id : deliveries.getKeys(false)) {
                String matName = deliveries.getString(id + ".item.material", "STONE");
                Material mat = Material.matchMaterial(matName);
                if (mat == null) mat = Material.STONE;

                String display = ChatColor.translateAlternateColorCodes('&',
                        deliveries.getString(id + ".item.name", "&fÜrün"));

                // lore → top 5 listesi
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GOLD + "=== İlk 5 ===");

                List<Map.Entry<String, Integer>> topList = DataManager.getTopList(id, 5);
                if (topList.isEmpty()) {
                    lore.add(ChatColor.GRAY + "Hiç teslimat yapılmadı.");
                } else {
                    int rank = 1;
                    for (Map.Entry<String, Integer> entry : topList) {
                        lore.add(ChatColor.YELLOW + "" + rank + ". "
                                + ChatColor.AQUA + entry.getKey()
                                + ChatColor.GRAY + " (" + entry.getValue() + ")");
                        rank++;
                    }
                }

                lore.add("");
                lore.add(ChatColor.GREEN + "Toplam Teslim: "
                        + ChatColor.WHITE + DataManager.getTotalAmount(id));
                lore.add(ChatColor.BLUE + "Senin Teslimin: "
                        + ChatColor.WHITE + DataManager.getPlayerAmount(id, player.getName()));

                ItemStack item = new ItemStack(mat);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(display);
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }

                inv.setItem(slot, item);
                slot += 2; // her ürün 2 slot aralıklı (10, 12, 14 gibi)
            }
        }

        player.openInventory(inv);
    }
}
