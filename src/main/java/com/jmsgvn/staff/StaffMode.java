package com.jmsgvn.staff;

import com.jmsgvn.OmniSCS;
import com.jmsgvn.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StaffMode {

    private static Map<UUID, StaffMode> staffModeMap = new HashMap<>();

    private ItemStack[] previousInventory;
    private final Player player;

    public StaffMode(Player player) {
        this.player = player;
        staffModeMap.put(player.getUniqueId(), this);

        initialise();
    }

    public void initialise() {
        this.previousInventory = player.getInventory().getContents();

        player.getInventory().clear();

        giveItems();

        // Maybe something for scoreboard?
        player.setMetadata("modmode", new FixedMetadataValue(OmniSCS.getInstance(), "modmode"));
        player.setMetadata("staffmode", new FixedMetadataValue(OmniSCS.getInstance(), "staffmode"));

        player.setGameMode(GameMode.CREATIVE);

        vanish();
    }

    public void destroy() {
        player.getInventory().clear();
        if (previousInventory[0] != null && previousInventory[0].getType() != Material.COMPASS) {
            player.getInventory().setContents(previousInventory);
        }

        player.updateInventory();

        player.setGameMode(GameMode.SURVIVAL);

        player.removeMetadata("modemode", OmniSCS.getInstance());
        player.removeMetadata("staffmode", OmniSCS.getInstance());

        unvanish();

        staffModeMap.remove(player.getUniqueId());
    }

    public void giveItems() {
        player.getInventory().setItem(0, ItemBuilder.of(Material.COMPASS).name("§eBoost").build());
        player.getInventory().setItem(1, ItemBuilder.of(Material.WATCH).name("§eRandom Teleport").build());
        player.getInventory().setItem(2, ItemBuilder.of(Material.SKULL).name("§eOnline Staff").build());
        player.getInventory().setItem(4, ItemBuilder.of(Material.DIAMOND_SWORD).name("§ePunish").build());
        player.getInventory().setItem(6, ItemBuilder.of(Material.ICE).name("§eFreeze").build());
        player.getInventory().setItem(7, ItemBuilder.of(Material.BOOK).name("§eInvsee").build());

        if (player.hasMetadata("vanished")){
            player.getInventory().setItem(8, ItemBuilder.of(Material.INK_SACK).data((short) 8).name("§eBecome Invisible").build());

        } else if (!player.hasMetadata("vanished")){
            player.getInventory().setItem(8, ItemBuilder.of(Material.INK_SACK).data((short) 10).name("§eBecome Visible").build());

        }

        player.updateInventory();
    }

    public void vanish() {
        player.setMetadata("vanished", new FixedMetadataValue(OmniSCS.getInstance(), "vanished"));
        for (Player online : Bukkit.getServer().getOnlinePlayers()) {
            if (!online.hasPermission("omni.staff") || online.hasMetadata("hidestaff")) {
                online.hidePlayer(player);
            }
        }
    }

    public void unvanish() {
        player.removeMetadata("vanished", OmniSCS.getInstance());
        for (Player online : Bukkit.getServer().getOnlinePlayers()) {
            online.showPlayer(player);
        }
    }

    public static Map<UUID, StaffMode> getStaffModeMap() {
        return staffModeMap;
    }
}
