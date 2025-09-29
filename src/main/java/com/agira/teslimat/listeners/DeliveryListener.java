package com.agira.teslimat.listeners;

import com.agira.teslimat.TeslimatPlugin;
import com.agira.teslimat.gui.ConfirmGUI;
import com.agira.teslimat.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class DeliveryListener implements Listener {

    private final TeslimatPlugin plugin;

    public DeliveryListener(TeslimatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String guiTitle = plugin.getConfig().getString("settings.gui.title", "&aTeslimat Menüsü").replace("&", "§");
        if (!event.getView().getTitle().equals(guiTitle)) return;

        event.setCancelled(true);

        if (event.getClick().isShiftClick()
                || event.getClick() == ClickType.NUMBER_KEY
                || event.getClick() == ClickType.DROP
                || event.getClick() == ClickType.CONTROL_DROP
                || event.getClick() == ClickType.MIDDLE) {
            return;
        }

        if (event.getRawSlot() >= event.getView().getTopInventory().getSize()) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) return;

        ConfigurationSection deliveries = plugin.getConfig().getConfigurationSection("deliveries");
        if (deliveries == null) return;

        for (String id : deliveries.getKeys(false)) {
            int slot = plugin.getConfig().getInt("deliveries." + id + ".gui-slot", -1);
            if (slot == event.getSlot()) {
                String matName = plugin.getConfig().getString("deliveries." + id + ".item.material", "STONE");
                Material mat = Material.matchMaterial(matName);
                if (mat == null) mat = Material.STONE;

                ConfirmGUI.open(player, id, mat);
                MessageUtils.send(player, "delivery.confirm_title", "id", id);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInventoryDrag(InventoryDragEvent event) {
        String guiTitle = plugin.getConfig().getString("settings.gui.title", "&aTeslimat Menüsü").replace("&", "§");
        if (event.getView().getTitle().equals(guiTitle)) {
            event.setCancelled(true);
        }
    }
}
