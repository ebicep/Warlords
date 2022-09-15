package com.ebicep.warlords.guilds.logs.types.oneplayer;

import com.ebicep.warlords.guilds.logs.AbstractGuildLog;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.UUID;

public abstract class AbstractGuildLogOnePlayer extends AbstractGuildLog {

    protected UUID sender;

    public AbstractGuildLogOnePlayer() {
    }

    public AbstractGuildLogOnePlayer(UUID sender) {
        this.sender = sender;
    }

    @Override
    public String getLog() {
        return ChatColor.GRAY + prepend() + " " + getSenderName() + " " + ChatColor.YELLOW + getAction() + " " + ChatColor.GRAY + append();
    }

    protected String getSenderName() {
        return ChatColor.AQUA + (sender == null ? "UNKNOWN" : Bukkit.getOfflinePlayer(sender).getName());
    }


}
