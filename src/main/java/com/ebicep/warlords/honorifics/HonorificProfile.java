package com.ebicep.warlords.honorifics;

import org.bson.Document;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HonorificProfile {

    private Set<Honorific> unlockedHonorifics = new HashSet<>();
    private Set<HonorificColor> unlockedColors = new HashSet<>();
    private Set<HonorificFont> unlockedFonts = new HashSet<>();

    @Nullable
    private Honorific equippedHonorific;
    private HonorificColor selectedColor = HonorificColor.AQUA;
    private HonorificFont selectedFont = HonorificFont.STANDARD;

    private long itemRerolls;
    private long starPiecesSynthesized;
    private long starPiecesUsed;
    private long supplyDropsRolled;
    private int highestAncientRenegadesFloor;
    private long highestSingleGameDamage;
    private boolean completedRegnumOfTwoCrowns;
    private boolean completedRegnumOblivionWithFourPlayers;

    public HonorificProfile() {
        ensureDefaults();
    }

    public static HonorificProfile fromDocument(@Nullable Document document) {
        HonorificProfile profile = new HonorificProfile();
        if (document == null) {
            return profile;
        }
        readEnumSet(document.get("unlockedHonorifics"), Honorific.class, profile.unlockedHonorifics);
        readEnumSet(document.get("unlockedColors"), HonorificColor.class, profile.unlockedColors);
        readEnumSet(document.get("unlockedFonts"), HonorificFont.class, profile.unlockedFonts);
        profile.equippedHonorific = readEnum(document.getString("equippedHonorific"), Honorific.class, null);
        profile.selectedColor = readEnum(document.getString("selectedColor"), HonorificColor.class, HonorificColor.AQUA);
        profile.selectedFont = readEnum(document.getString("selectedFont"), HonorificFont.class, HonorificFont.STANDARD);
        profile.itemRerolls = getLong(document, "itemRerolls");
        profile.starPiecesSynthesized = getLong(document, "starPiecesSynthesized");
        profile.starPiecesUsed = getLong(document, "starPiecesUsed");
        profile.supplyDropsRolled = getLong(document, "supplyDropsRolled");
        profile.highestAncientRenegadesFloor = (int) getLong(document, "highestAncientRenegadesFloor");
        profile.highestSingleGameDamage = getLong(document, "highestSingleGameDamage");
        profile.completedRegnumOfTwoCrowns = document.getBoolean("completedRegnumOfTwoCrowns", false);
        profile.completedRegnumOblivionWithFourPlayers = document.getBoolean("completedRegnumOblivionWithFourPlayers", false);
        profile.ensureDefaults();
        return profile;
    }

    public synchronized Document toEmbeddedDocument() {
        ensureDefaults();
        return new Document("unlockedHonorifics", enumNames(unlockedHonorifics))
                .append("unlockedColors", enumNames(unlockedColors))
                .append("unlockedFonts", enumNames(unlockedFonts))
                .append("equippedHonorific", equippedHonorific == null ? null : equippedHonorific.name())
                .append("selectedColor", selectedColor.name())
                .append("selectedFont", selectedFont.name())
                .append("itemRerolls", itemRerolls)
                .append("starPiecesSynthesized", starPiecesSynthesized)
                .append("starPiecesUsed", starPiecesUsed)
                .append("supplyDropsRolled", supplyDropsRolled)
                .append("highestAncientRenegadesFloor", highestAncientRenegadesFloor)
                .append("highestSingleGameDamage", highestSingleGameDamage)
                .append("completedRegnumOfTwoCrowns", completedRegnumOfTwoCrowns)
                .append("completedRegnumOblivionWithFourPlayers", completedRegnumOblivionWithFourPlayers);
    }

    public synchronized boolean isEmptyExceptDefaults() {
        ensureDefaults();
        return unlockedHonorifics.isEmpty()
                && unlockedColors.size() == 1
                && unlockedColors.contains(HonorificColor.AQUA)
                && unlockedFonts.size() == 1
                && unlockedFonts.contains(HonorificFont.STANDARD)
                && equippedHonorific == null
                && selectedColor == HonorificColor.AQUA
                && selectedFont == HonorificFont.STANDARD
                && itemRerolls == 0
                && starPiecesSynthesized == 0
                && starPiecesUsed == 0
                && supplyDropsRolled == 0
                && highestAncientRenegadesFloor == 0
                && highestSingleGameDamage == 0
                && !completedRegnumOfTwoCrowns
                && !completedRegnumOblivionWithFourPlayers;
    }

    public synchronized boolean unlock(Honorific honorific) {
        return unlockedHonorifics.add(honorific);
    }

    public synchronized boolean unlock(HonorificColor color) {
        return unlockedColors.add(color);
    }

    public synchronized boolean unlock(HonorificFont font) {
        return unlockedFonts.add(font);
    }

    public synchronized boolean isUnlocked(Honorific honorific) {
        return unlockedHonorifics.contains(honorific);
    }

    public synchronized boolean isUnlocked(HonorificColor color) {
        ensureDefaults();
        return unlockedColors.contains(color);
    }

    public synchronized boolean isUnlocked(HonorificFont font) {
        ensureDefaults();
        return unlockedFonts.contains(font);
    }

    public synchronized Set<Honorific> getUnlockedHonorifics() {
        return Set.copyOf(unlockedHonorifics);
    }

    @Nullable
    public synchronized Honorific getEquippedHonorific() {
        if (equippedHonorific != null && !unlockedHonorifics.contains(equippedHonorific)) {
            equippedHonorific = null;
        }
        return equippedHonorific;
    }

    public synchronized boolean equip(@Nullable Honorific honorific) {
        if (honorific != null && !unlockedHonorifics.contains(honorific)) {
            return false;
        }
        if (equippedHonorific == honorific) {
            return false;
        }
        equippedHonorific = honorific;
        return true;
    }

    public synchronized HonorificColor getSelectedColor() {
        ensureDefaults();
        return selectedColor;
    }

    public synchronized boolean selectColor(HonorificColor color) {
        ensureDefaults();
        if (!unlockedColors.contains(color) || selectedColor == color) {
            return false;
        }
        selectedColor = color;
        return true;
    }

    public synchronized HonorificFont getSelectedFont() {
        ensureDefaults();
        return selectedFont;
    }

    public synchronized boolean selectFont(HonorificFont font) {
        ensureDefaults();
        if (!unlockedFonts.contains(font) || selectedFont == font) {
            return false;
        }
        selectedFont = font;
        return true;
    }

    public synchronized long getItemRerolls() {
        return itemRerolls;
    }

    public synchronized void setMinimumItemRerolls(long amount) {
        itemRerolls = Math.max(itemRerolls, amount);
    }

    public synchronized long getStarPiecesSynthesized() {
        return starPiecesSynthesized;
    }

    public synchronized void addStarPiecesSynthesized(long amount) {
        starPiecesSynthesized = Math.max(0, starPiecesSynthesized + amount);
    }

    public synchronized long getStarPiecesUsed() {
        return starPiecesUsed;
    }

    public synchronized void addStarPiecesUsed(long amount) {
        starPiecesUsed = Math.max(0, starPiecesUsed + amount);
    }

    public synchronized long getSupplyDropsRolled() {
        return supplyDropsRolled;
    }

    public synchronized void addSupplyDropsRolled(long amount) {
        supplyDropsRolled = Math.max(0, supplyDropsRolled + amount);
    }

    public synchronized void setMinimumSupplyDropsRolled(long amount) {
        supplyDropsRolled = Math.max(supplyDropsRolled, amount);
    }

    public synchronized int getHighestAncientRenegadesFloor() {
        return highestAncientRenegadesFloor;
    }

    public synchronized void recordAncientRenegadesFloor(int floor) {
        highestAncientRenegadesFloor = Math.max(highestAncientRenegadesFloor, floor);
    }

    public synchronized long getHighestSingleGameDamage() {
        return highestSingleGameDamage;
    }

    public synchronized void recordSingleGameDamage(long damage) {
        highestSingleGameDamage = Math.max(highestSingleGameDamage, damage);
    }

    public synchronized boolean hasCompletedRegnumOfTwoCrowns() {
        return completedRegnumOfTwoCrowns;
    }

    public synchronized boolean hasCompletedRegnumOblivionWithFourPlayers() {
        return completedRegnumOblivionWithFourPlayers;
    }

    public synchronized void recordRegnumCompletion(boolean oblivion, int playerCount) {
        completedRegnumOfTwoCrowns = true;
        if (oblivion && playerCount == 4) {
            completedRegnumOblivionWithFourPlayers = true;
        }
    }

    private synchronized void ensureDefaults() {
        if (unlockedHonorifics == null) {
            unlockedHonorifics = new HashSet<>();
        }
        if (unlockedColors == null) {
            unlockedColors = new HashSet<>();
        }
        if (unlockedFonts == null) {
            unlockedFonts = new HashSet<>();
        }
        unlockedColors.add(HonorificColor.AQUA);
        unlockedFonts.add(HonorificFont.STANDARD);
        if (selectedColor == null || !unlockedColors.contains(selectedColor)) {
            selectedColor = HonorificColor.AQUA;
        }
        if (selectedFont == null || !unlockedFonts.contains(selectedFont)) {
            selectedFont = HonorificFont.STANDARD;
        }
    }

    private static long getLong(Document document, String key) {
        Object value = document.get(key);
        return value instanceof Number number ? number.longValue() : 0;
    }

    private static List<String> enumNames(Set<? extends Enum<?>> values) {
        return values.stream().map(Enum::name).sorted().toList();
    }

    private static <E extends Enum<E>> void readEnumSet(Object value, Class<E> enumClass, Set<E> output) {
        if (!(value instanceof List<?> list)) {
            return;
        }
        for (Object entry : list) {
            if (entry instanceof String name) {
                E parsed = readEnum(name, enumClass, null);
                if (parsed != null) {
                    output.add(parsed);
                }
            }
        }
    }

    @Nullable
    private static <E extends Enum<E>> E readEnum(@Nullable String value, Class<E> enumClass, @Nullable E fallback) {
        if (value == null) {
            return fallback;
        }
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }
}
