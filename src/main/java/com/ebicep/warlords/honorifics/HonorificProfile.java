package com.ebicep.warlords.honorifics;

import javax.annotation.Nullable;
import java.util.HashSet;
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

    public boolean unlock(Honorific honorific) {
        return unlockedHonorifics.add(honorific);
    }

    public boolean unlock(HonorificColor color) {
        if (color.isPatreonExclusive()) {
            return false;
        }
        return unlockedColors.add(color);
    }

    public boolean unlock(HonorificFont font) {
        if (font.isPatreonExclusive()) {
            return false;
        }
        return unlockedFonts.add(font);
    }

    public boolean isUnlocked(Honorific honorific) {
        return unlockedHonorifics.contains(honorific);
    }

    public boolean isUnlocked(HonorificColor color) {
        ensureDefaults();
        return !color.isPatreonExclusive() && unlockedColors.contains(color);
    }

    public boolean isUnlocked(HonorificFont font) {
        ensureDefaults();
        return !font.isPatreonExclusive() && unlockedFonts.contains(font);
    }

    public Set<Honorific> getUnlockedHonorifics() {
        return Set.copyOf(unlockedHonorifics);
    }

    @Nullable
    public Honorific getEquippedHonorific() {
        if (equippedHonorific != null && !unlockedHonorifics.contains(equippedHonorific)) {
            equippedHonorific = null;
        }
        return equippedHonorific;
    }

    public boolean equip(@Nullable Honorific honorific) {
        if (honorific != null && !unlockedHonorifics.contains(honorific)) {
            return false;
        }
        if (equippedHonorific == honorific) {
            return false;
        }
        equippedHonorific = honorific;
        return true;
    }

    public HonorificColor getSelectedColor() {
        ensureDefaults();
        return selectedColor;
    }

    public boolean selectColor(HonorificColor color) {
        return selectColor(color, false);
    }

    public boolean selectColor(HonorificColor color, boolean hasPatreon) {
        ensureDefaults();
        boolean available = color.isPatreonExclusive() ? hasPatreon : unlockedColors.contains(color);
        if (!available || selectedColor == color) {
            return false;
        }
        selectedColor = color;
        return true;
    }

    public HonorificFont getSelectedFont() {
        ensureDefaults();
        return selectedFont;
    }

    public boolean selectFont(HonorificFont font) {
        return selectFont(font, false);
    }

    public boolean selectFont(HonorificFont font, boolean hasPatreon) {
        ensureDefaults();
        boolean available = font.isPatreonExclusive() ? hasPatreon : unlockedFonts.contains(font);
        if (!available || selectedFont == font) {
            return false;
        }
        selectedFont = font;
        return true;
    }

    public boolean validatePatreonAccess(boolean hasPatreon) {
        ensureDefaults();
        boolean changed = unlockedColors.removeIf(HonorificColor::isPatreonExclusive);
        changed |= unlockedFonts.removeIf(HonorificFont::isPatreonExclusive);
        if (!hasPatreon && selectedColor.isPatreonExclusive()) {
            selectedColor = HonorificColor.AQUA;
            changed = true;
        }
        if (!hasPatreon && selectedFont.isPatreonExclusive()) {
            selectedFont = HonorificFont.STANDARD;
            changed = true;
        }
        return changed;
    }

    public long getItemRerolls() {
        return itemRerolls;
    }

    public void setMinimumItemRerolls(long amount) {
        itemRerolls = Math.max(itemRerolls, amount);
    }

    public long getStarPiecesSynthesized() {
        return starPiecesSynthesized;
    }

    public void addStarPiecesSynthesized(long amount) {
        starPiecesSynthesized = Math.max(0, starPiecesSynthesized + amount);
    }

    public long getStarPiecesUsed() {
        return starPiecesUsed;
    }

    public void addStarPiecesUsed(long amount) {
        starPiecesUsed = Math.max(0, starPiecesUsed + amount);
    }

    public long getSupplyDropsRolled() {
        return supplyDropsRolled;
    }

    public void addSupplyDropsRolled(long amount) {
        supplyDropsRolled = Math.max(0, supplyDropsRolled + amount);
    }

    public void setMinimumSupplyDropsRolled(long amount) {
        supplyDropsRolled = Math.max(supplyDropsRolled, amount);
    }

    public int getHighestAncientRenegadesFloor() {
        return highestAncientRenegadesFloor;
    }

    public void recordAncientRenegadesFloor(int floor) {
        highestAncientRenegadesFloor = Math.max(highestAncientRenegadesFloor, floor);
    }

    public long getHighestSingleGameDamage() {
        return highestSingleGameDamage;
    }

    public void recordSingleGameDamage(long damage) {
        highestSingleGameDamage = Math.max(highestSingleGameDamage, damage);
    }

    public boolean hasCompletedRegnumOfTwoCrowns() {
        return completedRegnumOfTwoCrowns;
    }

    public boolean hasCompletedRegnumOblivionWithFourPlayers() {
        return completedRegnumOblivionWithFourPlayers;
    }

    public void recordRegnumCompletion(boolean oblivion, int playerCount) {
        completedRegnumOfTwoCrowns = true;
        if (oblivion && playerCount == 4) {
            completedRegnumOblivionWithFourPlayers = true;
        }
    }

    private void ensureDefaults() {
        if (unlockedHonorifics == null) {
            unlockedHonorifics = new HashSet<>();
        }
        if (unlockedColors == null) {
            unlockedColors = new HashSet<>();
        }
        if (unlockedFonts == null) {
            unlockedFonts = new HashSet<>();
        }
        unlockedColors.removeIf(HonorificColor::isPatreonExclusive);
        unlockedFonts.removeIf(HonorificFont::isPatreonExclusive);
        unlockedColors.add(HonorificColor.AQUA);
        unlockedFonts.add(HonorificFont.STANDARD);
        if (selectedColor == null || (!selectedColor.isPatreonExclusive() && !unlockedColors.contains(selectedColor))) {
            selectedColor = HonorificColor.AQUA;
        }
        if (selectedFont == null || (!selectedFont.isPatreonExclusive() && !unlockedFonts.contains(selectedFont))) {
            selectedFont = HonorificFont.STANDARD;
        }
    }
}
