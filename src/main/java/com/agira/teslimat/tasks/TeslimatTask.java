package com.agira.teslimat.tasks;

import com.agira.teslimat.TeslimatPlugin;
import com.agira.teslimat.utils.DataManager;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Map;

public class TeslimatTask implements Runnable {

    private final TeslimatPlugin plugin;

    public TeslimatTask(TeslimatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        if (now >= TeslimatPlugin.getExpireTime()) {
            plugin.getLogger().info("=== Teslimat Süresi Bitti ===");

            // Config'teki tüm ürünler için top 5 listesi
            for (String id : plugin.getConfig().getConfigurationSection("deliveries").getKeys(false)) {
                plugin.getLogger().info("Ürün: " + id);

                List<Map.Entry<String, Integer>> top = DataManager.getTopList(id, 5);
                if (top.isEmpty()) {
                    plugin.getLogger().info("  Hiç teslimat yapılmadı.");
                } else {
                    int rank = 1;
                    for (Map.Entry<String, Integer> entry : top) {
                        plugin.getLogger().info("  " + rank + ". " + entry.getKey() + " -> " + entry.getValue() + " teslim");
                        rank++;
                    }
                }
            }

            // yeni expireTime belirle
            long days = plugin.getConfig().getLong("settings.duration-days", 7);
            long newExpire = now + (days * 24L * 60L * 60L * 1000L);
            TeslimatPlugin.setExpireTime(newExpire);

            // tekrar planlama
            long ticks = days * 24L * 60L * 60L * 20L;
            Bukkit.getScheduler().runTaskTimer(plugin, new TeslimatTask(plugin), ticks, ticks);

            plugin.getLogger().info("Yeni teslimat sezonu başladı. Süre: " + days + " gün");
        }
    }
}
