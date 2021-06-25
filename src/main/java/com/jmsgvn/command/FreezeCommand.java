package com.jmsgvn.command;

import com.jmsgvn.OmniSCS;
import com.jmsgvn.staff.StaffMode;
import com.jmsgvn.util.CC;
import com.jmsgvn.util.gui.Menu;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FreezeCommand implements CommandExecutor {

    private static Map<UUID, BukkitRunnable> frozenPlayers = new HashMap<>();

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

        if (!user.getCachedData().getPermissionData().checkPermission("omni.freeze").asBoolean()) {
            player.sendMessage(CC.translate("&cNo permission."));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(CC.translate("&cUsage: /freeze <player>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(CC.translate("&cNo online player with the name '" + args[0] + "'"));
            return true;
        }

        if (target == player) {
            player.sendMessage(CC.translate("&cYou cannot freeze yourself."));
            return true;
        }

        if (!frozenPlayers.containsKey(target.getUniqueId())) {

            if (StaffMode.getStaffModeMap().containsKey(target.getUniqueId())) {
                player.sendMessage(CC.translate("&cYou cannot freeze other staff members."));
                return true;
            }

            target.playSound(target.getLocation(), Sound.AMBIENCE_THUNDER, 60F, 60F);
            target.setMetadata("frozen", new FixedMetadataValue(OmniSCS.getInstance(), "frozen"));
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (target.isOnline()) {
                        target.sendMessage(CC.translate("&7&m--------------------"));
                        target.sendMessage(CC.translate("&8<&4&lWARNING&8> &8<&4&lWARNING&8>"));
                        target.sendMessage(CC.translate("&7You have been frozen!"));
                        target.sendMessage(CC.translate("&7You have 3 minutes to join our discord:"));
                        target.sendMessage(CC.translate("&7https://omnipvp.net/discord"));
                        target.sendMessage(CC.translate("&8<&4&lWARNING&8> &8<&4&lWARNING&8>"));
                        target.sendMessage(CC.translate("&7&m--------------------"));
                    }

                }
            };

            runnable.runTaskTimerAsynchronously(OmniSCS.getInstance(), 0L, 100L);

            frozenPlayers.put(target.getUniqueId(), runnable);
            player.sendMessage(CC.translate("&eYou have frozen " + target.getName()));
        } else {
            frozenPlayers.get(target.getUniqueId()).cancel();
            frozenPlayers.remove(target.getUniqueId());
            target.removeMetadata("frozen", OmniSCS.getInstance());

            target.sendMessage(CC.translate("&eYou have been unfrozen."));
            player.sendMessage(CC.translate("&eYou have unfrozen " + target.getName()));
        }


        return true;
    }
}
