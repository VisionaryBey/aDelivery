package com.agira.teslimat;

import com.agira.teslimat.commands.TeslimatCommand;
import com.agira.teslimat.listeners.ConfirmListener;
import com.agira.teslimat.listeners.DeliveryListener;
import com.agira.teslimat.placeholders.DeliveryPlaceholder;
import com.agira.teslimat.tasks.TeslimatTask;
import com.agira.teslimat.utils.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class TeslimatPlugin extends JavaPlugin {

    private static TeslimatPlugin instance;
    private static long expireTime;

    private FileConfiguration messages;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveResource("messages.yml", false);

        reloadMessages();
        DataManager.setup(this);

        long days = getConfig().getLong("settings.duration-days", 7);
        expireTime = System.currentTimeMillis() + (days * 24L * 60L * 60L * 1000L);

        getCommand("teslimat").setExecutor(new TeslimatCommand(this));

        Bukkit.getPluginManager().registerEvents(new DeliveryListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ConfirmListener(this), this);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new DeliveryPlaceholder(this).register();
        }

        long ticks = days * 24L * 60L * 60L * 20L;
        Bukkit.getScheduler().runTaskTimer(this, new TeslimatTask(this), ticks, ticks);

        getLogger().info("TeslimatPlugin etkin!");
    }

    @Override
    public void onDisable() {
        DataManager.save();
        getLogger().info("TeslimatPlugin kapatıldı!");
    }

    public static TeslimatPlugin getInstance() {
        return instance;
    }

    public static long getExpireTime() {
        return expireTime;
    }

    public static void setExpireTime(long time) {
        expireTime = time;
    }

    public FileConfiguration getMessages() {
        return messages;
    }

    public void reloadMessages() {
        File msgFile = new File(getDataFolder(), "messages.yml");
        this.messages = YamlConfiguration.loadConfiguration(msgFile);
    }
}
