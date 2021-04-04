package com.winthier.freehat;

import com.destroystokyo.paper.MaterialTags;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public final class FreeHatPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String string, String[] args) {
        if (!(sender instanceof HumanEntity)) {
            msg(sender, "&cPlayer expected");
            return true;
        }
        if (args.length != 0) return false;
        Player player = (Player) sender;
        PlayerInventory playerInventory = player.getInventory();
        ItemStack old = playerInventory.getHelmet();
        if (old != null && old.getType() != Material.AIR) {
            msg(player, "&cYou are already wearing a hat.");
            return true;
        }
        ItemStack hand = playerInventory.getItemInHand();
        if (hand == null || hand.getType() == Material.AIR) {
            msg(player, "&cThere's nothing in your hand.");
            return true;
        }
        if (hand.getAmount() > 1) {
            msg(player, "&cYou cannot wear " + hand.getAmount()
                + " items on your head.");
            return true;
        }
        playerInventory.setHelmet(hand);
        playerInventory.setItemInHand(old);
        msg(player, "&bEnjoy your fancy %s hat!", niceItemName(hand));
        return true;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    void onInventoryClick(InventoryClickEvent event) {
        if (event.getClick() != ClickType.LEFT) {
            return;
        }
        if (event.getInventory().getType() != InventoryType.CRAFTING) {
            return;
        }
        if (event.getSlotType() != InventoryType.SlotType.ARMOR) {
            return;
        }
        if (event.getSlot() != 39) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (!player.hasPermission("freehat.click")) {
            return;
        }
        ItemStack cursor = event.getCursor();
        if (cursor == null || cursor.getType() == Material.AIR) {
            return;
        }
        if (MaterialTags.HEAD_EQUIPPABLE.isTagged(cursor.getType())) return;
        if (cursor.getAmount() > 1) return;
        event.setCancelled(true);
        // Update
        ItemStack old = event.getCurrentItem();
        player.getInventory().setHelmet(cursor);
        event.getView().setCursor(old);
        msg(player, "&bEnjoy your fancy %s hat!", niceItemName(cursor));
    }

    static void msg(CommandSender sender, String msg, Object... args) {
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        if (args.length > 0) {
            msg = String.format(msg, args);
        }
        sender.sendMessage(msg);
    }

    static String niceItemName(ItemStack item) {
        return Stream.of(item.getType().name().split("_"))
            .map(s -> s.substring(0, 1) + s.substring(1).toLowerCase())
            .collect(Collectors.joining(" "));
    }
}
