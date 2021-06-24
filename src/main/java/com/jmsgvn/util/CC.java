package com.jmsgvn.util;

import net.md_5.bungee.api.ChatColor;

public class CC {

    public static String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
