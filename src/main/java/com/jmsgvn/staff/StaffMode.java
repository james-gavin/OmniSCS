package com.jmsgvn.staff;

import com.jmsgvn.OmniSCS;
import com.jmsgvn.util.CC;
import com.jmsgvn.util.ItemBuilder;
import com.jmsgvn.util.gui.Button;
import com.jmsgvn.util.gui.Menu;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.stream.Collectors;

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

        player.removeMetadata("modmode", OmniSCS.getInstance());
        player.removeMetadata("staffmode", OmniSCS.getInstance());

        unvanish();

        staffModeMap.remove(player.getUniqueId());
    }

    public void giveItems() {
        player.getInventory().setItem(0, ItemBuilder.of(Material.COMPASS).name("&eBoost").build());
        player.getInventory().setItem(1, ItemBuilder.of(Material.WATCH).name("&eRandom Teleport").build());
        player.getInventory().setItem(2, ItemBuilder.of(Material.SKULL_ITEM).name("&eOnline Staff").build());
        player.getInventory().setItem(4, ItemBuilder.of(Material.DIAMOND_SWORD).name("&ePunish").build());
        player.getInventory().setItem(6, ItemBuilder.of(Material.ICE).name("&eFreeze Player").build());
        player.getInventory().setItem(7, ItemBuilder.of(Material.BOOK).name("&eInspect Inventory").build());

        if (player.hasMetadata("vanished")){
            player.getInventory().setItem(8, ItemBuilder.of(Material.INK_SACK).data((short) 8).name("&eBecome Invisible").build());

        } else if (!player.hasMetadata("vanished")){
            player.getInventory().setItem(8, ItemBuilder.of(Material.INK_SACK).data((short) 10).name("&eBecome Visible").build());

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

    public void openOnlineStaff(Player player) {
        Menu menu = new Menu() {
            @Override
            public String getTitle(Player player) {
                return ChatColor.GRAY + "Online Staff";
            }

            @Override
            public Map<Integer, Button> getButtons(Player player) {
                Map<Integer, Button> buttons = new HashMap<>();

                List<Player> profileList = new ArrayList<>();

                for (Player online : Bukkit.getServer().getOnlinePlayers()) {
                    User user = OmniSCS.getInstance().getApi().getUserManager().getUser(online.getUniqueId());
                    if (user != null) {
                        if (user.getCachedData().getPermissionData().checkPermission("omni.staff").asBoolean()) {
                            profileList.add(online);
                        }
                    }
                }


                for (Player staffPlayer : profileList) {
                    buttons.put(buttons.size(), new Button() {

                        @Override
                        public String getName(Player player) {
                            return CC.translate("&c" + staffPlayer.getName());
                        }


                        @Override
                        public Material getMaterial(Player player) {
                            return Material.SKULL_ITEM;
                        }


                        @Override
                        public List<String> getDescription(Player player) {
                            List<String> toReturn = new ArrayList<>();
                            User user = OmniSCS.getInstance().getApi().getUserManager().getUser(staffPlayer.getUniqueId());

                            if (user == null) {
                                return List.of("null");
                            }

                            toReturn.add(CC.translate("&7&m--------------------------"));
                            toReturn.add(CC.translate("&cRank&7: &f" + user.getPrimaryGroup()));
                            toReturn.add(CC.translate("&cIn staff mode&7: " + (getStaffModeMap().containsKey(staffPlayer.getUniqueId()) ? "&ayes" : "&cno")));
                            toReturn.add(CC.translate("&cVanished&7: " + (player.hasMetadata("vanished") ? "&ayes" : "&cno")));
                            toReturn.add(CC.translate("&7&m--------------------------"));
                            return toReturn;
                        }

                        @Override
                        public void clicked(Player player, int slot, ClickType clickType) {
                            Button button = getButtons().get(slot);
                            if (button != null) {
                                player.teleport(Bukkit.getPlayer(ChatColor.stripColor(getName(player))));
                                player.sendMessage(CC.translate("&6Teleporting to " + getName(player)));
                            }
                        }
                    });
                }

                return buttons;
            }
        };

        menu.setAutoUpdate(true);
        menu.setUpdateAfterClick(true);
        menu.openMenu(player);
    }

    public void sendMessage(String message) {
        player.sendMessage(CC.translate(message));
    }

    public static Map<UUID, StaffMode> getStaffModeMap() {
        return staffModeMap;
    }
}
