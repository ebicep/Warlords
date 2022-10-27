package com.ebicep.warlords.guilds;

import com.ebicep.warlords.player.general.CustomScoreboard;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

public class GuildTag {

    private String name;
    @Field("name_color")
    private String nameColor = ChatColor.GOLD.toString();
    @Field("bracket_color")
    private String bracketColor = ChatColor.GOLD.toString();

    public GuildTag(String name) {
        this.name = name;

    }

    public String getTag() {
        return bracketColor + "[" + nameColor + name + bracketColor + "]";
    }

    public void setInfo(String name, String nameColor, String bracketColor) {
        this.name = name;
        this.nameColor = nameColor;
        this.bracketColor = bracketColor;
        CustomScoreboard.giveMainLobbyScoreboardToAll();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameColor() {
        return nameColor;
    }

    public void setNameColor(String nameColor) {
        this.nameColor = nameColor;
    }

    public void setNameColor(ChatColor nameColor) {
        this.nameColor = nameColor.toString();
    }

    public String getBracketColor() {
        return bracketColor;
    }

    public void setBracketColor(String bracketColor) {
        this.bracketColor = bracketColor;
    }

    public void setBracketColor(ChatColor bracketColor) {
        this.bracketColor = bracketColor.toString();
    }
}
