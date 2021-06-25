package com.jmsgvn;

import com.jmsgvn.command.*;
import com.jmsgvn.staff.StaffMode;
import com.jmsgvn.staff.StaffPlayerEvents;
import com.jmsgvn.util.DataSource;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class OmniSCS extends JavaPlugin {

    private static OmniSCS instance;
    private LuckPerms api;

    @Override
    public void onEnable() {
        instance = this;
        api = LuckPermsProvider.get();

        this.saveDefaultConfig();

        registerEvents();
        registerCommands();
        new DataSource(this);
        getLogger().info("OmniSCS Loaded.");
    }

    @Override
    public void onDisable() {
        for (Player online : Bukkit.getServer().getOnlinePlayers()) {
            if (online.hasMetadata("staffmode")) {
                StaffMode staffMode = StaffMode.getStaffModeMap().get(online.getUniqueId());
                staffMode.destroy();
            }
        }
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new StaffPlayerEvents(this), this);
    }

    private void registerCommands() {
        getCommand("freeze").setExecutor(new FreezeCommand());
        getCommand("vanish").setExecutor(new VanishCommand());
        getCommand("mod").setExecutor(new ModCommand());
        getCommand("reports").setExecutor(new ReportsCommand());
        getCommand("invsee").setExecutor(new InvseeCommand());

    }

    public LuckPerms getApi() {
        return api;
    }

    public static OmniSCS getInstance() {
        return instance;
    }

}
