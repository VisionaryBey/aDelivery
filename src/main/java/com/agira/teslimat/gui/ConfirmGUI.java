package com.agira.teslimat.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfirmGUI {

    public static void open(Player player, String deliveryId, Material mat) {
        Inventory inv = Bukkit.createInventory(player, 9,
                ChatColor.GREEN + "Teslimatı Onayla: " + deliveryId);

        // EVET butonu (slot 3)
        ItemStack yes = new ItemStack(Material.GREEN_CONCRETE);
        ItemMeta yMeta = yes.getItemMeta();
        if (yMeta != null) {
            yMeta.setDisplayName(ChatColor.GREEN + "Evet, teslim et!");
            yes.setItemMeta(yMeta);
        }
        inv.setItem(3, yes);

        // INFO itemi (slot 4)
        ItemStack info = new ItemStack(mat);
        ItemMeta iMeta = info.getItemMeta();
        if (iMeta != null) {
            iMeta.setDisplayName(ChatColor.YELLOW + "Teslim edilecek: " + mat.name());
            info.setItemMeta(iMeta);
        }
        inv.setItem(4, info);

        // HAYIR butonu (slot 5)
        ItemStack no = new ItemStack(Material.RED_CONCRETE);
        ItemMeta nMeta = no.getItemMeta();
        if (nMeta != null) {
            nMeta.setDisplayName(ChatColor.RED + "Hayır, iptal");
            no.setItemMeta(nMeta);
        }
        inv.setItem(5, no);

        player.openInventory(inv);
    }
}
