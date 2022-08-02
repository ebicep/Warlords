package com.ebicep.warlords.guilds.logs.types.oneplayer.roles;

import com.ebicep.warlords.guilds.logs.types.oneplayer.AbstractGuildLogOnePlayer;
import org.bukkit.ChatColor;

import java.util.UUID;

public abstract class AbstractGuildLogRole extends AbstractGuildLogOnePlayer {

    protected String role;

    public AbstractGuildLogRole(UUID sender, String role) {
        super(sender);
        this.role = role;
    }

    @Override
    public String getLog() {
        return ChatColor.GRAY + prepend() + " " + getSenderName() + " " + ChatColor.YELLOW + getAction() + " " + ChatColor.GREEN + role + " " + ChatColor.GRAY + append();
    }
}
