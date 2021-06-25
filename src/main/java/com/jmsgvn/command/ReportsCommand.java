package com.jmsgvn.command;

import com.jmsgvn.OmniSCS;
import com.jmsgvn.util.CC;
import com.jmsgvn.util.DataSource;
import com.jmsgvn.util.gui.Button;
import com.jmsgvn.util.gui.Menu;
import net.luckperms.api.model.user.User;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.translate("&cThis command may only be executed by players."));
            return true;
        }

        Player player = (Player) sender;

        User user = OmniSCS.getInstance().getApi().getUserManager().getUser(player.getUniqueId());

        if (user == null) {
            return true;
        }

        if (!user.getCachedData().getPermissionData().checkPermission("omni.reports").asBoolean()) {
            player.sendMessage(CC.translate("&cNo permission."));
            return true;
        }

        new Menu() {
            @Override
            public String getTitle(Player player) {
                return CC.translate("&7Reports Menu");
            }

            @Override
            public Map<Integer, Button> getButtons(Player var1) {
                Map<Integer, Button> buttons = new HashMap<>();

                buttons.put(3, new Button() {
                    @Override
                    public String getName(Player player) {
                        return CC.translate("&aActive Reports");
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        List<String> desc = new ArrayList<>();
                        desc.add(CC.translate("&7&oView active reports..."));
                        return desc;
                    }

                    @Override
                    public Material getMaterial(Player player) {
                        return Material.BOOK_AND_QUILL;
                    }

                    @Override
                    public void clicked(Player player, int slot, ClickType clickType) {
                        player.closeInventory();
                        DataSource.loadReportsMenu(player, false);
                    }
                });

                buttons.put(5, new Button() {
                    @Override
                    public String getName(Player player) {
                        return CC.translate("&cInactive Reports");
                    }

                    @Override
                    public List<String> getDescription(Player player) {
                        List<String> desc = new ArrayList<>();
                        desc.add(CC.translate("&7&oView inactive reports..."));
                        return desc;
                    }

                    @Override
                    public Material getMaterial(Player player) {
                        return Material.BOOK;
                    }

                    @Override
                    public void clicked(Player player, int slot, ClickType clickType) {
                        player.closeInventory();
                        DataSource.loadReportsMenu(player, true);
                    }
                });

                return buttons;
            }
        }.openMenu(player);

        return true;
    }
}
