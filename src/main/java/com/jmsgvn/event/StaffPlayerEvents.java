package com.jmsgvn.event;

import com.jmsgvn.OmniSCS;
import com.jmsgvn.staff.StaffMode;
import com.jmsgvn.util.CC;
import com.jmsgvn.util.ItemBuilder;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
            plugin.getLogger().info("A null user.");
            return;
        }

        if (user.getCachedData().getPermissionData().checkPermission("omni.staff").asBoolean()) {
            new StaffMode(player);
        }

        for (Player otherPlayer : Bukkit.getServer().getOnlinePlayers().stream().filter(otherPlayer -> player !=
                otherPlayer).collect(Collectors.toList())) {
            if (otherPlayer.hasMetadata("vanished")) {
                player.hidePlayer(otherPlayer);
            }
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
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(!player.hasMetadata("modemode")) {
            return;
        }

        if (!(event.hasItem())) {
            return;
        }

        if (!(event.hasBlock())) {
            return;
        }

        if (!(event.getItem().hasItemMeta())) {
            return;
        }

        if (!(event.getItem().getItemMeta().hasDisplayName())) {
            return;
        }

        if(event.getClickedBlock().getType().equals(Material.FENCE_GATE)
                || event.getClickedBlock().getType().equals(Material.WOOD_BUTTON)
                || event.getClickedBlock().getType().equals(Material.STONE_BUTTON)
                || event.getClickedBlock().getType().equals(Material.GOLD_PLATE)
                || event.getClickedBlock().getType().equals(Material.IRON_PLATE)
                || event.getClickedBlock().getType().equals(Material.STONE_BUTTON)
                || event.getClickedBlock().getType().equals(Material.WOOD_PLATE)
                || event.getClickedBlock().getType().equals(Material.STONE_PLATE) || event.getClickedBlock().getType().equals(Material.WOOD_DOOR) || event.getClickedBlock().getType().equals(Material.WOODEN_DOOR)
                || event.getClickedBlock().getType().equals(Material.TRAP_DOOR))
        {
            event.setCancelled(true);
        }

        StaffMode staffMode = StaffMode.getStaffModeMap().get(player.getUniqueId());
        event.setCancelled(true);
        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);

        if (event.getClickedBlock() != null && event.getClickedBlock().getType() != null && event.getClickedBlock().getType() == Material.CHEST) {
            Chest chest = (Chest) event.getClickedBlock().getState();
            player.openInventory(chest.getBlockInventory());
        }

        if (event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("§eRandom Teleport")) {
            List<Player> eligiblePlayers = new ArrayList<>();

            for (Player online : Bukkit.getServer().getOnlinePlayers()) {
                if (!online.hasMetadata("modemode")) {
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
            player.getInventory().setItem(8, ItemBuilder.of(Material.INK_SACK).data((short) 8).name(ChatColor.YELLOW + "Become Invisible").build());
            player.updateInventory();
        }

        if (event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Become Invisible")) {
            staffMode.vanish();
            player.getInventory().setItem(8, ItemBuilder.of(Material.INK_SACK).data((short) 10).name(ChatColor.YELLOW + "Become Visible").build());
            player.updateInventory();
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
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
        if (event.getEntity().hasMetadata("modmode")) event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (event.getWhoClicked().hasMetadata("modmode")) event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().hasMetadata("modmode")) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().hasMetadata("modmode")) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().hasMetadata("modmode")) event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().hasMetadata("modmode")) event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (event.getPlayer().hasMetadata("modmode")) event.setCancelled(true);
    }

    @EventHandler
    public void onInvDrag(InventoryDragEvent event) {
        if (event.getWhoClicked().hasMetadata("modmode")) event.setCancelled(true);
    }

}
