package com.agira.teslimat.commands;

import com.agira.teslimat.TeslimatPlugin;
import com.agira.teslimat.gui.DeliveryGUI;
import com.agira.teslimat.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeslimatCommand implements CommandExecutor {

    private final TeslimatPlugin plugin;

    public TeslimatCommand(TeslimatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtils.send(sender, "errors.not_player");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            new DeliveryGUI(plugin).open(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("teslimat.admin")) {
                MessageUtils.send(sender, "errors.no_permission");
                return true;
            }

            plugin.reloadConfig();
            plugin.reloadMessages();

            MessageUtils.send(sender, "commands.reload");
            return true;
        }

        MessageUtils.send(sender, "errors.invalid_usage", "usage", "/teslimat [reload]");
        return true;
    }
}
