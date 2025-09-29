package com.agira.teslimat.utils;

import com.agira.teslimat.TeslimatPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageUtils {

    public static void send(CommandSender sender, String path, Object... replacements) {
        String raw = TeslimatPlugin.getInstance().getMessages().getString(path, "&cMesaj bulunamadı: " + path);
        String msg = color(applyPlaceholders(raw, replacements));
        sender.sendMessage(getPrefix() + msg);
    }

    private static String getPrefix() {
        String p = TeslimatPlugin.getInstance().getMessages().getString("prefix", "&6[ Teslimat ] ");
        return color(p);
    }

    private static String applyPlaceholders(String msg, Object... replacements) {
        if (msg == null) return "";
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            String key = String.valueOf(replacements[i]);
            String val = String.valueOf(replacements[i + 1]);
            msg = msg.replace("{" + key + "}", val);
        }
        return msg;
    }

    private static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
