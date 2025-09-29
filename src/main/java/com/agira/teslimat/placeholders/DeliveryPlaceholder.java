package com.agira.teslimat.placeholders;

import com.agira.teslimat.TeslimatPlugin;
import com.agira.teslimat.utils.DataManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class DeliveryPlaceholder extends PlaceholderExpansion {

    private final TeslimatPlugin plugin;

    public DeliveryPlaceholder(TeslimatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "teslimat";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Agira";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {

        // === Genel ===
        if (params.equals("expire")) {
            long remain = TeslimatPlugin.getExpireTime() - System.currentTimeMillis();
            if (remain <= 0) return "0";
            long days = remain / (1000 * 60 * 60 * 24);
            return days + " gün";
        }

        // === Toplam teslim: %teslimat_toplam_<id>% ===
        if (params.startsWith("toplam_")) {
            String id = params.substring("toplam_".length());
            return String.valueOf(DataManager.getTotalAmount(id));
        }

        // === Oyuncunun teslimi: %teslimat_ben_<id>% ===
        if (params.startsWith("ben_") && player != null) {
            String id = params.substring("ben_".length());
            return String.valueOf(DataManager.getPlayerAmount(id, player.getName()));
        }

        // === Top listeler: %teslimat_top1_name_<id>%, %teslimat_top1_amount_<id>% ===
        if (params.startsWith("top")) {
            // örnek: top1_name_elmas
            String[] split = params.split("_");
            if (split.length >= 3) {
                try {
                    int rank = Integer.parseInt(split[0].replace("top", "")); // "1" -> 1
                    String type = split[1]; // name / amount
                    String id = split[2];   // elmas, altin, demir

                    List<Map.Entry<String, Integer>> topList = DataManager.getTopList(id, 5);
                    if (rank <= 0 || rank > topList.size()) return "-";

                    Map.Entry<String, Integer> entry = topList.get(rank - 1);
                    if (type.equalsIgnoreCase("name")) {
                        return entry.getKey();
                    } else if (type.equalsIgnoreCase("amount")) {
                        return String.valueOf(entry.getValue());
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return null;
    }
}
