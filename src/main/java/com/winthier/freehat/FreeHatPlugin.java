package com.winthier.freehat;

import com.cavetale.mytems.Mytems;
import com.destroystokyo.paper.MaterialTags;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
        if (!(sender instanceof Player)) {
            sender.sendMessage("[freehat:hat] Player expected");
            return true;
        }
        if (args.length != 0) return false;
        Player player = (Player) sender;
        PlayerInventory playerInventory = player.getInventory();
        ItemStack oldHelmet = playerInventory.getHelmet();
        if (oldHelmet != null && oldHelmet.getType() != Material.AIR) {
            player.sendMessage(Component.text("You are already wearing a hat", NamedTextColor.RED));
            return true;
        }
        ItemStack hand = playerInventory.getItemInMainHand();
        if (hand == null || hand.getType() == Material.AIR) {
            player.sendMessage(Component.text("There's nothing in your hand", NamedTextColor.RED));
            return true;
        }
        ItemStack helmet = hand.clone();
        helmet.setAmount(1);
        hand.subtract(1);
        playerInventory.setHelmet(helmet);
        player.sendMessage(Component.text("Enjoy your fancy ", NamedTextColor.GREEN)
                           .append(niceItemName(helmet))
                           .append(Component.text(" hat!")));
        return true;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    void onInventoryClick(InventoryClickEvent event) {
        if (event.getClick() != ClickType.LEFT) return;
        if (event.getInventory().getType() != InventoryType.CRAFTING) return;
        if (event.getSlotType() != InventoryType.SlotType.ARMOR) return;
        if (event.getSlot() != 39) return;
        if (event.getClickedInventory() == null) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (!player.hasPermission("freehat.click")) {
            return;
        }
        ItemStack cursor = event.getCursor();
        if (cursor == null || cursor.getType() == Material.AIR) {
            return;
        }
        if (MaterialTags.HEAD_EQUIPPABLE.isTagged(cursor.getType())) return;
        ItemStack oldHelmet = event.getCurrentItem();
        boolean hasOldHat = oldHelmet != null && oldHelmet.getType() != Material.AIR;
        if (hasOldHat && cursor.getAmount() > 1) {
            return;
        }
        // Update
        event.setCancelled(true);
        ItemStack helmet = cursor.clone();
        helmet.setAmount(1);
        event.setCurrentItem(helmet);
        if (hasOldHat) {
            event.getView().setCursor(oldHelmet);
        } else {
            cursor.subtract(1);
        }
        player.sendActionBar(Component.text("Enjoy your fancy ", NamedTextColor.GREEN)
                             .append(niceItemName(helmet))
                             .append(Component.text(" hat!")));
    }

    public static Component niceItemName(ItemStack item) {
        Mytems mytems = Mytems.forItem(item);
        if (mytems != null) return mytems.getMytem().getDisplayName();
        String i18n = item.getI18NDisplayName();
        if (i18n != null) return Component.text(i18n);
        return Component.text(Stream.of(item.getType().name().split("_"))
                              .map(s -> s.substring(0, 1) + s.substring(1).toLowerCase())
                              .collect(Collectors.joining(" ")));
    }
}
