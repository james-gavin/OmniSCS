package com.jmsgvn.command;

import com.jmsgvn.OmniSCS;
import com.jmsgvn.staff.StaffMode;
import com.jmsgvn.util.CC;
import net.luckperms.api.model.user.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModCommand implements CommandExecutor {
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

        if (!user.getCachedData().getPermissionData().checkPermission("omni.staff").asBoolean()) {
            player.sendMessage(CC.translate("&cNo permission."));
            return true;
        }

        StaffMode staffMode = StaffMode.getStaffModeMap().get(player.getUniqueId());

        if (staffMode == null) {
            new StaffMode(player);
        } else {
            staffMode.destroy();
        }

        return true;
    }
}
