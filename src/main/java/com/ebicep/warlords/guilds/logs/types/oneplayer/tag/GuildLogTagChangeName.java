package com.ebicep.warlords.guilds.logs.types.oneplayer.tag;

import com.ebicep.warlords.guilds.logs.types.oneplayer.AbstractGuildLogOnePlayer;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

public class GuildLogTagChangeName extends AbstractGuildLogOnePlayer {

    @Field("old_name")
    private String oldName;
    @Field("new_name")
    private String newName;

    public GuildLogTagChangeName(UUID sender, String oldName, String newName) {
        super(sender);
        this.oldName = oldName;
        this.newName = newName;
    }

    @Override
    public String getAction() {
        return "changed guild tag name from " + ChatColor.GREEN + oldName + ChatColor.YELLOW + " to " + ChatColor.GREEN + newName;
    }
}
