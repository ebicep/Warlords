package com.ebicep.warlords.guilds.logs.types.oneplayer.tag;

import com.ebicep.warlords.guilds.logs.types.oneplayer.AbstractGuildLogOnePlayer;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

public class GuildLogTagNameColor extends AbstractGuildLogOnePlayer {

    @Field("old_name")
    private String oldColor;
    @Field("new_name")
    private String newColor;

    public GuildLogTagNameColor(UUID sender, String oldColor, String newColor) {
        super(sender);
        this.oldColor = oldColor;
        this.newColor = newColor;
    }

    @Override
    public String getAction() {
        ChatColor oldChatColor = ChatColor.getByChar(oldColor.charAt(1));
        ChatColor newChatColor = ChatColor.getByChar(newColor.charAt(1));
        return "changed guild tag name color from " + oldChatColor + oldChatColor.name() + ChatColor.YELLOW + " to " + newChatColor + newChatColor.name();
    }
}
