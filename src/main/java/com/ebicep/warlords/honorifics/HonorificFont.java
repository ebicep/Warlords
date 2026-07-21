package com.ebicep.warlords.honorifics;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public enum HonorificFont {

    STANDARD("Standard", Material.PAPER, false, false, false, false),
    ITALIC("Italic", Material.FEATHER, false, true, false, false),
    BOLD("Bold", Material.ANVIL, true, false, false, true),
    SMALL_CAPS("Small Caps", Material.WRITABLE_BOOK, false, false, true, true);

    public static final HonorificFont[] VALUES = values();

    private static final Map<Character, Character> SMALL_CAPS_MAP = createSmallCapsMap();

    private final String displayName;
    private final Material icon;
    private final boolean bold;
    private final boolean italic;
    private final boolean smallCaps;
    private final boolean patreonExclusive;

    HonorificFont(String displayName, Material icon, boolean bold, boolean italic, boolean smallCaps, boolean patreonExclusive) {
        this.displayName = displayName;
        this.icon = icon;
        this.bold = bold;
        this.italic = italic;
        this.smallCaps = smallCaps;
        this.patreonExclusive = patreonExclusive;
    }

    public Component createComponent(String text, TextColor color) {
        String renderedText = smallCaps ? toSmallCaps(text) : text;
        TextComponent.Builder builder = Component.text(renderedText, color).toBuilder();
        builder.decoration(TextDecoration.BOLD, bold);
        builder.decoration(TextDecoration.ITALIC, italic);
        return builder.build();
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getIcon() {
        return icon;
    }

    public boolean isPatreonExclusive() {
        return patreonExclusive;
    }

    public HonorificCost getCost() {
        return HonorificShopCosts.getFontCost(this);
    }

    private static String toSmallCaps(String input) {
        StringBuilder output = new StringBuilder(input.length());
        for (char character : input.toCharArray()) {
            char lower = Character.toLowerCase(character);
            output.append(SMALL_CAPS_MAP.getOrDefault(lower, character));
        }
        return output.toString();
    }

    private static Map<Character, Character> createSmallCapsMap() {
        Map<Character, Character> map = new HashMap<>();
        String normal = "abcdefghijklmnopqrstuvwxyz";
        String smallCaps = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀꜱᴛᴜᴠᴡxʏᴢ";
        for (int i = 0; i < normal.length(); i++) {
            map.put(normal.charAt(i), smallCaps.charAt(i));
        }
        return map;
    }
}
