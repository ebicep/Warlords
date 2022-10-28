package com.ebicep.warlords.guilds.logs.types.oneplayer.tag;

import com.ebicep.warlords.guilds.logs.types.oneplayer.AbstractGuildLogOnePlayer;
import org.bukkit.ChatColor;

import java.util.UUID;

public class GuildLogTagCreateName extends AbstractGuildLogOnePlayer {

    private String name;

    public GuildLogTagCreateName(UUID sender, String name) {
        super(sender);
        this.name = name;
    }

    @Override
    public String getAction() {
        return "created guild tag name " + ChatColor.GREEN + name;
    }
}
