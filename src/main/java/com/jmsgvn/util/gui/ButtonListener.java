package com.jmsgvn.util.gui;

import com.jmsgvn.OmniSCS;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class ButtonListener implements Listener {


    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void onButtonPress(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Menu openMenu = Menu.currentlyOpenedMenus.get(player.getName());

        if (openMenu != null) {
            if (event.getSlot() != event.getRawSlot()) {
                if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                    event.setCancelled(true);
//                    if (openMenu.isNoncancellingInventory() && event.getCurrentItem() != null) {
//                        player.getOpenInventory().getTopInventory().addItem(event.getCurrentItem());
//                        event.setCurrentItem(null);
//                    }
                }

                return;
            }

            if (openMenu.getButtons().containsKey(event.getSlot())) {
                Button button = (Button) openMenu.getButtons().get(event.getSlot());
                boolean cancel = button.shouldCancel(player, event.getSlot(), event.getClick());
                if (cancel || event.getClick() != ClickType.SHIFT_LEFT && event.getClick() != ClickType.SHIFT_RIGHT) {
                    event.setCancelled(cancel);
                } else {
                    event.setCancelled(true);
                }

                button.clicked(player, event.getSlot(), event.getClick());
                if (Menu.currentlyOpenedMenus.containsKey(player.getName())) {
                    Menu newMenu = (Menu) Menu.currentlyOpenedMenus.get(player.getName());
                    if (newMenu == openMenu && newMenu.isUpdateAfterClick()) {
                        newMenu.openMenu(player);
                    }
                }

                if (event.isCancelled()) {
                    Bukkit.getScheduler().runTaskLater(OmniSCS.getInstance(), player::updateInventory, 1L);
                }
            } else if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                event.setCancelled(true);
//                if (openMenu.isNoncancellingInventory() && event.getCurrentItem() != null) {
//                    player.getOpenInventory().getTopInventory().addItem(event.getCurrentItem());
//                    event.setCurrentItem((ItemStack) null);
//                }
            }

            if(!openMenu.isNoncancellingInventory()) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Menu openMenu = (Menu) Menu.currentlyOpenedMenus.get(player.getName());

        if (openMenu != null) {
            openMenu.onClose(player);
            Menu.cancelCheck(player);
            Menu.currentlyOpenedMenus.remove(player.getName());
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        Menu openMenu = Menu.currentlyOpenedMenus.get(player.getName());

        if (openMenu != null && !openMenu.isNoncancellingInventory()) {
            event.setCancelled(true);
        }
    }
}
