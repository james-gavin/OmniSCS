package com.jmsgvn.command;

import com.jmsgvn.OmniSCS;
import com.jmsgvn.util.CC;
import com.jmsgvn.util.gui.Button;
import com.jmsgvn.util.gui.Menu;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class InvseeCommand implements CommandExecutor {
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

        if (!user.getCachedData().getPermissionData().checkPermission("omni.invsee").asBoolean()) {
            player.sendMessage(CC.translate("&cNo permission."));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(CC.translate("&cUsage: /invsee <player>"));
            return true;
        }

        Player rightClicked = Bukkit.getPlayer(args[0]);

        if (rightClicked == null) {
            player.sendMessage(CC.translate("&cNo online player with the name '" + args[0] + "'"));
            return true;
        }

        if (rightClicked == player) {
            player.sendMessage(CC.translate("&cYou cannot invsee yourself."));
            return true;
        }

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
        return true;
    }
}
