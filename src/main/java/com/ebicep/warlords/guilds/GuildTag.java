package com.ebicep.warlords.guilds;

import com.ebicep.warlords.player.general.CustomScoreboard;
import com.ebicep.warlords.util.bukkit.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;

public class GuildTag {

    public static final Colors[] COLORS = new Colors[]{
            Colors.RED,
            Colors.GOLD, Colors.YELLOW,
            Colors.GREEN, Colors.DARK_GREEN,
            Colors.BLUE, Colors.DARK_AQUA, Colors.DARK_BLUE,
            Colors.LIGHT_PURPLE, Colors.DARK_PURPLE,
            Colors.WHITE, Colors.GRAY, Colors.DARK_GRAY, Colors.BLACK
    };

    @Nonnull
    private String name = "";
    @Field("name_color")
    @Nonnull
    private String nameColor = NamedTextColor.GRAY.toString();
    @Field("bracket_color")
    @Nonnull
    private String bracketColor = NamedTextColor.GRAY.toString();

    public GuildTag() {
    }

    public GuildTag(@Nonnull String name) {
        this.name = name;
    }

    public GuildTag(@Nonnull String name, @Nonnull String nameColor, @Nonnull String bracketColor) {
        this.name = name;
        this.nameColor = nameColor;
        this.bracketColor = bracketColor;
    }

    public Component getTag(boolean bold) {
        return Component.text("[", getBracketTextColor())
                        .decoration(TextDecoration.BOLD, bold)
                        .append(Component.text(name, getNameTextColor()))
                        .append(Component.text("]"));
    }

    public NamedTextColor getBracketTextColor() {
        return NamedTextColor.NAMES.value(bracketColor);
    }

    public NamedTextColor getNameTextColor() {
        return NamedTextColor.NAMES.value(nameColor);
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

    @Nonnull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.toUpperCase();
        CustomScoreboard.updateLobbyPlayerNames();
    }

    @Nonnull
    public String getNameColor() {
        return nameColor;
    }

    public void setNameColor(NamedTextColor nameColor) {
        this.nameColor = nameColor.toString();
        CustomScoreboard.updateLobbyPlayerNames();
    }

    @Nonnull
    public String getBracketColor() {
        return bracketColor;
    }

    public void setBracketColor(NamedTextColor bracketColor) {
        this.bracketColor = bracketColor.toString();
        CustomScoreboard.updateLobbyPlayerNames();
    }
}
