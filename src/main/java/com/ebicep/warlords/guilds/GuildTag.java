package com.ebicep.warlords.guilds;

import com.ebicep.warlords.player.general.CustomScoreboard;
import com.ebicep.warlords.util.bukkit.Colors;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

public class GuildTag {

    public static final Colors[] COLORS = new Colors[]{
            Colors.RED,
            Colors.GOLD, Colors.YELLOW,
            Colors.GREEN, Colors.DARK_GREEN,
            Colors.BLUE, Colors.DARK_AQUA, Colors.DARK_BLUE,
            Colors.LIGHT_PURPLE, Colors.DARK_PURPLE,
            Colors.WHITE, Colors.GRAY, Colors.DARK_GRAY, Colors.BLACK
    };

    private String name;
    @Field("name_color")
    private String nameColor = ChatColor.GRAY.toString();
    @Field("bracket_color")
    private String bracketColor = ChatColor.GRAY.toString();

    public GuildTag() {
    }

    public GuildTag(String name) {
        this.name = name;
    }

    public GuildTag(String name, String nameColor, String bracketColor) {
        this.name = name;
        this.nameColor = nameColor;
        this.bracketColor = bracketColor;
    }

    public String getTag() {
        return bracketColor + "[" + nameColor + name + bracketColor + "]";
    }

    public void setInfo(String name, String nameColor, String bracketColor) {
        this.name = name;
        this.nameColor = nameColor;
        this.bracketColor = bracketColor;
        CustomScoreboard.updateLobbyPlayerNames();
    }

    public String getColoredName() {
        return nameColor + name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.toUpperCase();
        CustomScoreboard.updateLobbyPlayerNames();
    }

    public String getNameColor() {
        return nameColor;
    }

    public void setNameColor(String nameColor) {
        this.nameColor = nameColor;
        CustomScoreboard.updateLobbyPlayerNames();
    }

    public void setNameColor(ChatColor nameColor) {
        setNameColor(nameColor.toString());
    }

    public String getBracketColor() {
        return bracketColor;
    }

    public void setBracketColor(String bracketColor) {
        this.bracketColor = bracketColor;
        CustomScoreboard.updateLobbyPlayerNames();
    }

    public void setBracketColor(ChatColor bracketColor) {
        setBracketColor(bracketColor.toString());
    }
}
