package com.ebicep.warlords.maps;

import com.ebicep.warlords.Warlords;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public enum Team {
    RED(ChatColor.RED, "Red", Color.fromRGB(153, 51, 51), new ItemStack(Material.WOOL, 1, (short) 14)),
    BLUE(ChatColor.BLUE, "Blue", Color.fromRGB(51, 76, 178), new ItemStack(Material.WOOL, 1, (short) 11)),
    ;
    private final static Team[] inverseMapping;

    static {
        inverseMapping = values();
        Collections.reverse(Arrays.asList(inverseMapping));
    }

    private final ChatColor teamColor;
    private final String chatTag;
    public ItemStack item;
    private final String chatTagColored;
    private final String chatTagBoldColored;
    private final Color armorColor;

    Team(ChatColor teamColor, String chatTag, Color armorColor, ItemStack item) {
        this.teamColor = teamColor;
        this.chatTag = chatTag;
        this.item = item;
        this.chatTagColored = teamColor + chatTag;
        this.chatTagBoldColored = teamColor.toString() + ChatColor.BOLD + chatTag;
        this.armorColor = armorColor;
    }

    @Nonnull
    public ChatColor teamColor() {
        return teamColor;
    }

    @Nonnull
    public Color armorColor() {
        return armorColor;
    }

    @Nonnull
    public String prefix() {
        return chatTag;
    }

    @Nonnull
    public String coloredPrefix() {
        return chatTagColored;
    }

    @Nonnull
    public String boldColoredPrefix() {
        return chatTagBoldColored;
    }

    @Nonnull
    public Team enemy() {
        return inverseMapping[ordinal()];
    }

    public static Team getSelected(Player player) {
        return player.getMetadata("selected-team").stream()
                .map(v -> v.value() instanceof Team ? (Team) v.value() : null)
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);
    }

    public static void setSelected(Player player, Team selectedTeam) {
        player.removeMetadata("selected-team", Warlords.getInstance());
        player.setMetadata("selected-team", new FixedMetadataValue(Warlords.getInstance(), selectedTeam));
    }

}