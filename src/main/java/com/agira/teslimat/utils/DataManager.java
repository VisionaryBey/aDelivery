package com.agira.teslimat.utils;

import com.agira.teslimat.TeslimatPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DataManager {

    private static File file;
    private static FileConfiguration data;

    // id -> (oyuncu -> teslim miktarı)
    private static final Map<String, Map<String, Integer>> deliveries = new HashMap<>();

    public static void setup(TeslimatPlugin plugin) {
        file = new File(plugin.getDataFolder(), "deliveries.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        data = YamlConfiguration.loadConfiguration(file);
        load();
    }

    public static void save() {
        try {
            for (String id : deliveries.keySet()) {
                for (Map.Entry<String, Integer> entry : deliveries.get(id).entrySet()) {
                    data.set(id + "." + entry.getKey(), entry.getValue());
                }
            }
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        deliveries.clear();
        if (data == null) return;

        for (String id : data.getKeys(false)) {
            Map<String, Integer> map = new HashMap<>();
            for (String player : data.getConfigurationSection(id).getKeys(false)) {
                map.put(player, data.getInt(id + "." + player));
            }
            deliveries.put(id, map);
        }
    }

    public static void addDelivery(String id, String player, int amount) {
        deliveries.putIfAbsent(id, new HashMap<>());
        Map<String, Integer> map = deliveries.get(id);
        map.put(player, map.getOrDefault(player, 0) + amount);
        save();
    }

    public static int getPlayerAmount(String id, String player) {
        if (!deliveries.containsKey(id)) return 0;
        return deliveries.get(id).getOrDefault(player, 0);
    }

    public static int getTotalAmount(String id) {
        if (!deliveries.containsKey(id)) return 0;
        return deliveries.get(id).values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * İlk X oyuncuyu teslimat miktarına göre döndürür
     * @param id ürün id (ör: elmas, altin)
     * @param limit kaç kişi (ör: 5)
     * @return List<Map.Entry<oyuncu, teslim>>
     */
    public static List<Map.Entry<String, Integer>> getTopList(String id, int limit) {
        if (!deliveries.containsKey(id)) return Collections.emptyList();

        return deliveries.get(id).entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue())) // büyükten küçüğe
                .limit(limit)
                .collect(Collectors.toList());
    }
}
