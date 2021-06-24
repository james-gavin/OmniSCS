package com.jmsgvn;

import com.jmsgvn.event.StaffPlayerEvents;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class OmniSCS extends JavaPlugin {

    private static OmniSCS instance;
    private LuckPerms api;

    @Override
    public void onEnable() {
        instance = this;
        api = LuckPermsProvider.get();

        getServer().getPluginManager().registerEvents(new StaffPlayerEvents(this), this);

        getLogger().info("OmniSCS Loaded.");
    }

    public LuckPerms getApi() {
        return api;
    }

    public static OmniSCS getInstance() {
        return instance;
    }

}
