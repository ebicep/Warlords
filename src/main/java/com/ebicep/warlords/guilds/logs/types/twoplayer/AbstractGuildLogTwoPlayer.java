package com.ebicep.warlords.guilds.logs.types.twoplayer;

import com.ebicep.warlords.guilds.logs.AbstractGuildLog;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.UUID;

public abstract class AbstractGuildLogTwoPlayer extends AbstractGuildLog {

    protected UUID sender;
    protected UUID receiver;

    public AbstractGuildLogTwoPlayer() {
    }

    public AbstractGuildLogTwoPlayer(UUID sender, UUID receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    @Override
    public String getLog() {
        return ChatColor.GRAY + prepend() + " " + getSenderName() + " " + ChatColor.YELLOW + getAction() + " " + getReceiverName() + " " + ChatColor.GRAY + append();
    }

    protected String getSenderName() {
        return ChatColor.AQUA + Bukkit.getOfflinePlayer(sender).getName();
    }

    protected String getReceiverName() {
        return ChatColor.AQUA + Bukkit.getOfflinePlayer(receiver).getName();
    }

}
