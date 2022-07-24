package com.ebicep.warlords.util.chat;

import org.bukkit.ChatColor;

public enum ChatChannels {

    ALL("All", null),
    PARTY("Party", ChatColor.BLUE),
    GUILD("Guild", ChatColor.GREEN),

    ;

    public final String name;
    public final ChatColor chatColor;

    ChatChannels(String name, ChatColor chatColor) {
        this.name = name;
        this.chatColor = chatColor;
    }

    public String getColoredName() {
        return chatColor + name;
    }
}
