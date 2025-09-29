package com.agira.teslimat.listeners;

import com.agira.teslimat.TeslimatPlugin;
import com.agira.teslimat.utils.DataManager;
import com.agira.teslimat.utils.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class ConfirmListener implements Listener {

    private final TeslimatPlugin plugin;

    public ConfirmListener(TeslimatPlugin plugin) {
        this.plugin = plugin;
    }

    private boolean isConfirmTitle(String rawTitle) {
        String stripped = ChatColor.stripColor(rawTitle);
        return stripped != null && stripped.startsWith("Teslimatı Onayla: ");
    }

    private String parseDeliveryId(String rawTitle) {
        String stripped = ChatColor.stripColor(rawTitle);
        return stripped.replace("Teslimatı Onayla: ", "").trim();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onConfirmClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();
        if (!isConfirmTitle(title)) return;

        event.setCancelled(true);

        if (event.getRawSlot() >= event.getView().getTopInventory().getSize()) return;

        if (event.getClick().isShiftClick()
                || event.getClick() == ClickType.NUMBER_KEY
                || event.getClick() == ClickType.DROP
                || event.getClick() == ClickType.CONTROL_DROP
                || event.getClick() == ClickType.MIDDLE) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) return;

        String deliveryId = parseDeliveryId(title);

        if (clicked.getType() == Material.GREEN_CONCRETE) {
            handleConfirm(player, deliveryId);
        } else if (clicked.getType() == Material.RED_CONCRETE) {
            MessageUtils.send(player, "delivery.confirm_no");
            player.closeInventory();
        }
    }

    private void handleConfirm(Player player, String deliveryId) {
        String matName = plugin.getConfig().getString("deliveries." + deliveryId + ".item.material", "STONE");
        Material mat = Material.matchMaterial(matName);
        if (mat == null) mat = Material.STONE;

        int totalDelivered = 0;
        for (ItemStack is : player.getInventory().getContents()) {
            if (is != null && is.getType() == mat) {
                totalDelivered += is.getAmount();
            }
        }

        if (totalDelivered <= 0) {
            MessageUtils.send(player, "errors.no_item", "item", mat.name());
            player.closeInventory();
            return;
        }

        player.getInventory().remove(mat);
        DataManager.addDelivery(deliveryId, player.getName(), totalDelivered);

        MessageUtils.send(player, "delivery.confirm_yes", "amount", totalDelivered, "item", mat.name());
        player.closeInventory();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onConfirmDrag(InventoryDragEvent event) {
        String title = event.getView().getTitle();
        if (isConfirmTitle(title)) {
            event.setCancelled(true);
        }
    }
}
