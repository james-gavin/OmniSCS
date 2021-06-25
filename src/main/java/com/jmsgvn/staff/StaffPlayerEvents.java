package com.jmsgvn.staff;

import com.jmsgvn.OmniSCS;
import com.jmsgvn.staff.StaffMode;
import com.jmsgvn.util.CC;
import com.jmsgvn.util.ItemBuilder;
import com.jmsgvn.util.gui.Button;
import com.jmsgvn.util.gui.Menu;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class StaffPlayerEvents implements Listener {

    private final OmniSCS plugin;

    public StaffPlayerEvents(OmniSCS plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = plugin.getApi().getUserManager().getUser(player.getUniqueId());

        if (user == null) {
            return;
        }

        if (user.getCachedData().getPermissionData().checkPermission("omni.staff").asBoolean()) {
            new StaffMode(player);
        }

        for (Player otherPlayer : Bukkit.getServer().getOnlinePlayers().stream().filter(otherPlayer -> player !=
                otherPlayer).collect(Collectors.toList())) {
            if (otherPlayer.hasMetadata("vanished")) {
                if (!player.hasPermission("omni.staff") || player.hasMetadata("hidestaff")) {
                    player.hidePlayer(otherPlayer);
                }
            }
        }

        if (player.hasMetadata("frozen")) {
            StaffMode.getStaffModeMap().values().forEach(staffMode1 -> {
                staffMode1.sendMessage("&7[&4&lWARNING&7] &c" + event.getPlayer().getName() + " &7has joined while frozen.");
            });
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        StaffMode staffMode = StaffMode.getStaffModeMap().get(player.getUniqueId());

        if (staffMode != null) {
            if (player.hasMetadata("modmode")) {
                staffMode.destroy();
            }
        }

        if (player.hasMetadata("frozen")) {
            StaffMode.getStaffModeMap().values().forEach(staffMode1 -> {
                staffMode1.sendMessage("&7[&4&lWARNING&7] &c" + event.getPlayer().getName() + " &7has disconnected while frozen.");
            });
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();


        if(!player.hasMetadata("modmode")) {
            return;
        }

        if ((event.hasBlock())) {
            if(event.getClickedBlock().getType().equals(Material.FENCE_GATE)
                    || event.getClickedBlock().getType().equals(Material.WOOD_BUTTON)
                    || event.getClickedBlock().getType().equals(Material.STONE_BUTTON)
                    || event.getClickedBlock().getType().equals(Material.GOLD_PLATE)
                    || event.getClickedBlock().getType().equals(Material.IRON_PLATE)
                    || event.getClickedBlock().getType().equals(Material.STONE_BUTTON)
                    || event.getClickedBlock().getType().equals(Material.WOOD_PLATE)
                    || event.getClickedBlock().getType().equals(Material.STONE_PLATE)
                    || event.getClickedBlock().getType().equals(Material.WOOD_DOOR)
                    || event.getClickedBlock().getType().equals(Material.WOODEN_DOOR)
                    || event.getClickedBlock().getType().equals(Material.TRAP_DOOR)
                    || event.getClickedBlock().getType().equals(Material.LEVER))
            {
                event.setCancelled(true);
            }
        }


        if (event.getClickedBlock() != null && event.getClickedBlock().getType() != null && event.getClickedBlock().getType() == Material.CHEST) {
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                Block chestBlock = event.getClickedBlock();
                BlockState chestState = chestBlock.getState();
                if (chestState instanceof Chest) {
                    Chest chest = (Chest) chestState;
                    Inventory inventory = chest.getInventory();
                    if (inventory instanceof DoubleChestInventory) {
                        DoubleChest doubleChest = (DoubleChest) inventory.getHolder();
                        Inventory doubleChestInventory = Bukkit.createInventory(player, 54);
                        doubleChestInventory.setContents(doubleChest.getInventory().getContents());

                        event.setCancelled(true);

                        chest.update();
                        player.openInventory(doubleChestInventory);
                    } else {
                        Inventory singleChestInventory = Bukkit.createInventory(player, 27);
                        singleChestInventory.setContents(inventory.getContents());

                        event.setCancelled(true);

                        chest.update();
                        player.openInventory(singleChestInventory);
                    }
                }
                player.sendMessage(CC.translate("&eOpening chest silently..."));
            }
        }


        if (!(event.hasItem())) {
            return;
        }

        if (!(event.getItem().hasItemMeta())) {
            return;
        }

        if (!(event.getItem().getItemMeta().hasDisplayName())) {
            return;
        }

        StaffMode staffMode = StaffMode.getStaffModeMap().get(player.getUniqueId());
        event.setCancelled(true);
        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);

        if (event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Online Staff")) {
            staffMode.openOnlineStaff(player);
        }

        if (event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Random Teleport")) {
            List<Player> eligiblePlayers = new ArrayList<>();

            for (Player online : Bukkit.getServer().getOnlinePlayers()) {
                if (!online.hasMetadata("modmode")) {
                    eligiblePlayers.add(online);
                }
            }

            if (eligiblePlayers.size() == 0) {
                player.sendMessage(CC.translate("&cNo one online to teleport to."));
                return;
            }

            Random rand = new Random();
            Player randomPlayer = eligiblePlayers.get(rand.nextInt(eligiblePlayers.size()));

            player.teleport(randomPlayer);
            player.sendMessage(CC.translate("&6Teleporting you to &e" + randomPlayer.getName()));
        }

        if (event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Become Visible")) {
            staffMode.unvanish();
            player.getInventory().setItem(8, ItemBuilder.of(Material.INK_SACK).data((short) 8).name("&eBecome Invisible").build());
            player.updateInventory();
        }

        if (event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Become Invisible")) {
            staffMode.vanish();
            player.getInventory().setItem(8, ItemBuilder.of(Material.INK_SACK).data((short) 10).name("&eBecome Visible").build());
            player.updateInventory();
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {
            Player player = event.getPlayer();
            Player rightClicked = (Player) event.getRightClicked();
            ItemStack itemStack = player.getItemInHand();

            if (itemStack == null) return;

            if (!(itemStack.hasItemMeta())) return;

            if (!(itemStack.getItemMeta().hasDisplayName())) return;

            if (!player.hasMetadata("modmode")) return;

            if (itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Inspect Inventory")) {
                new Menu() {

                    @Override
                    public String getTitle(Player player) {
                        return rightClicked.getName();
                    }

                    @Override
                    public Map<Integer, Button> getButtons(Player player) {
                        Map<Integer, Button> buttons = new HashMap<>();

                        for (ItemStack stack : rightClicked.getInventory().getContents()) {
                            buttons.put(buttons.size(), Button.fromItem(stack));
                        }

                        for (ItemStack stack : rightClicked.getInventory().getArmorContents()) {
                            buttons.put(buttons.size(), Button.fromItem(stack));
                        }

                        return buttons;
                    }
                }.openMenu(player);
            }

            if (itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Freeze Player")) {
                Bukkit.dispatchCommand(player, "freeze " + rightClicked.getName());
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
        if (player.hasMetadata("frozen") && !event.getMessage().equalsIgnoreCase("/msg")) {
            event.setCancelled(true);
            return;
        }

        if (event.getMessage().equalsIgnoreCase("/vanish")){
            if (player.hasPermission("omni.vanish") && player.hasMetadata("modmode") ){
                if (player.hasMetadata("vanished")) {
                    player.getInventory().setItem(8, ItemBuilder.of(Material.INK_SACK).data((short) 8).name(ChatColor.YELLOW + "Become Invisible").build());
                    player.updateInventory();
                } else if (!player.hasMetadata("vanished")) {
                    player.getInventory().setItem(8, ItemBuilder.of(Material.INK_SACK).data((short) 10).name(ChatColor.YELLOW + "Become Visible").build());
                    player.updateInventory();

                }
            }

        }


    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity().hasMetadata("modmode") || event.getEntity().hasMetadata("frozen")) event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (event.getWhoClicked().hasMetadata("modmode") || event.getWhoClicked().hasMetadata("frozen")) event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().hasMetadata("modmode") || event.getEntity().hasMetadata("frozen")) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().hasMetadata("modmode") || event.getPlayer().hasMetadata("frozen")) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().hasMetadata("modmode") || event.getPlayer().hasMetadata("frozen")) event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().hasMetadata("modmode") || event.getPlayer().hasMetadata("frozen")) event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (event.getPlayer().hasMetadata("modmode") || event.getPlayer().hasMetadata("frozen")) event.setCancelled(true);
    }

    @EventHandler
    public void onInvDrag(InventoryDragEvent event) {
        if (event.getWhoClicked().hasMetadata("modmode") || event.getWhoClicked().hasMetadata("frozen")) event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().hasMetadata("frozen")) event.getPlayer().teleport(event.getPlayer().getLocation());
    }

}
