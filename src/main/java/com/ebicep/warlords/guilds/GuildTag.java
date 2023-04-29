package com.ebicep.warlords.guilds;

import com.ebicep.warlords.player.general.CustomScoreboard;
import com.ebicep.warlords.util.bukkit.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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

    private String name = "";
    @Field("name_color")
    private String nameColor = NamedTextColor.GRAY.toString();
    @Field("bracket_color")
    private String bracketColor = NamedTextColor.GRAY.toString();

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

    public Component getTag(boolean bold) {
        return Component.textOfChildren(
                Component.text("["),
                Component.text(name, getNameTextColor()),
                Component.text("]")
        ).color(getBracketTextColor()).decoration(TextDecoration.BOLD, bold);
    }

    public void setInfo(String name, String nameColor, String bracketColor) {
        this.name = name;
        this.nameColor = nameColor;
        this.bracketColor = bracketColor;
        CustomScoreboard.updateLobbyPlayerNames();
    }

    public Component getColoredName() {
        return Component.text(name, getNameTextColor());
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

    public NamedTextColor getNameTextColor() {
        return NamedTextColor.NAMES.value(nameColor);
    }

    public void setNameColor(NamedTextColor nameColor) {
        this.nameColor = nameColor.toString();
        CustomScoreboard.updateLobbyPlayerNames();
    }

    public String getBracketColor() {
        return bracketColor;
    }

    public NamedTextColor getBracketTextColor() {
        return NamedTextColor.NAMES.value(bracketColor);
    }

    public void setBracketColor(NamedTextColor bracketColor) {
        this.bracketColor = bracketColor.toString();
        CustomScoreboard.updateLobbyPlayerNames();
    }
}
