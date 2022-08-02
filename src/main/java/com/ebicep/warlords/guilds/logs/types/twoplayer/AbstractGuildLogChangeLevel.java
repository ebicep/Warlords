package com.ebicep.warlords.guilds.logs.types.twoplayer;

import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

public abstract class AbstractGuildLogChangeLevel extends AbstractGuildLogTwoPlayer {

    private String before;
    private String after;
    @Field("old_level")
    private int oldLevel;
    @Field("new_level")
    private int newLevel;

    public AbstractGuildLogChangeLevel(UUID sender, UUID receiver, String before, String after, int oldLevel, int newLevel) {
        super(sender, receiver);
        this.before = before;
        this.after = after;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    @Override
    public String append() {
        return "from " + ChatColor.GREEN + before + "(" + oldLevel + ")" + ChatColor.GRAY + " to " + ChatColor.GREEN + after + "(" + newLevel + ")";
    }

}
