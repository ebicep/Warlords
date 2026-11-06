package com.ebicep.warlords.player.general.armorcustomization;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class ArmorCustomizationProfile {

    private Set<ArmorPattern> unlockedPatterns = new HashSet<>();
    private Set<ArmorPatternMaterial> unlockedMaterials = new HashSet<>();
    private PieceSettings helmet = new PieceSettings();
    private PieceSettings chestplate = new PieceSettings();
    private PieceSettings leggings = new PieceSettings();
    private PieceSettings boots = new PieceSettings();

    public ArmorCustomizationProfile() {
        ensureDefaults();
    }

    public boolean unlock(ArmorPattern pattern) {
        ensureDefaults();
        return unlockedPatterns.add(pattern);
    }

    public boolean unlock(ArmorPatternMaterial material) {
        ensureDefaults();
        return unlockedMaterials.add(material);
    }

    public boolean isUnlocked(ArmorPattern pattern) {
        ensureDefaults();
        return unlockedPatterns.contains(pattern);
    }

    public boolean isUnlocked(ArmorPatternMaterial material) {
        ensureDefaults();
        return unlockedMaterials.contains(material);
    }

    @Nullable
    public ArmorPattern getSelectedPattern(ArmorPiece piece) {
        ensureDefaults();
        return settings(piece).pattern;
    }

    public boolean selectPattern(ArmorPiece piece, @Nullable ArmorPattern pattern, boolean hasSupporter) {
        ensureDefaults();
        if (!piece.supportsTrim()) {
            return false;
        }
        if (pattern != null && !canEquip(pattern, hasSupporter)) {
            return false;
        }
        PieceSettings settings = settings(piece);
        if (settings.pattern == pattern) {
            return false;
        }
        settings.pattern = pattern;
        return true;
    }

    @Nullable
    public ArmorPatternMaterial getSelectedMaterial(ArmorPiece piece) {
        ensureDefaults();
        return settings(piece).material;
    }

    public boolean selectMaterial(ArmorPiece piece, @Nullable ArmorPatternMaterial material, boolean hasSupporter) {
        ensureDefaults();
        if (!piece.supportsTrim()) {
            return false;
        }
        if (material != null && !canEquip(material, hasSupporter)) {
            return false;
        }
        PieceSettings settings = settings(piece);
        if (settings.material == material) {
            return false;
        }
        settings.material = material;
        return true;
    }

    public boolean isEnchantUnlocked(ArmorPiece piece) {
        ensureDefaults();
        return settings(piece).enchantUnlocked;
    }

    public boolean isEnchantEnabled(ArmorPiece piece) {
        ensureDefaults();
        return settings(piece).enchantUnlocked && settings(piece).enchantEnabled;
    }

    public boolean unlockEnchant(ArmorPiece piece) {
        ensureDefaults();
        PieceSettings settings = settings(piece);
        if (settings.enchantUnlocked) {
            return false;
        }
        settings.enchantUnlocked = true;
        settings.enchantEnabled = true;
        return true;
    }

    public boolean setEnchantEnabled(ArmorPiece piece, boolean enabled) {
        ensureDefaults();
        PieceSettings settings = settings(piece);
        if (settings.enchantEnabled == enabled) {
            return false;
        }
        settings.enchantEnabled = enabled;
        return true;
    }

    public boolean applySupporterDefaults(boolean hasSupporter) {
        ensureDefaults();
        boolean changed = unlockedPatterns.removeIf(ArmorPattern::isSupporterExclusive);
        changed |= unlockedMaterials.removeIf(ArmorPatternMaterial::isSupporterExclusive);
        if (hasSupporter) {
            return changed;
        }
        for (ArmorPiece piece : ArmorPiece.VALUES) {
            PieceSettings settings = settings(piece);
            if (settings.pattern != null && settings.pattern.isSupporterExclusive()) {
                settings.pattern = null;
                changed = true;
            }
            if (settings.material != null && settings.material.isSupporterExclusive()) {
                settings.material = null;
                changed = true;
            }
            if (settings.enchantEnabled) {
                settings.enchantEnabled = false;
                changed = true;
            }
        }
        return changed;
    }

    @Nullable
    public ArmorPattern getEffectivePattern(ArmorPiece piece, boolean hasSupporter) {
        ensureDefaults();
        if (!piece.supportsTrim()) {
            return null;
        }
        ArmorPattern pattern = settings(piece).pattern;
        if (!canEquip(pattern, hasSupporter)) {
            return null;
        }
        return pattern;
    }

    @Nullable
    public ArmorPatternMaterial getEffectiveMaterial(ArmorPiece piece, boolean hasSupporter) {
        ensureDefaults();
        if (!piece.supportsTrim()) {
            return null;
        }
        ArmorPatternMaterial material = settings(piece).material;
        if (!canEquip(material, hasSupporter)) {
            return null;
        }
        return material;
    }

    public boolean getEffectiveGlint(ArmorPiece piece, boolean hasSupporter) {
        ensureDefaults();
        return hasSupporter && settings(piece).enchantEnabled;
    }

    public void ensureDefaults() {
        if (unlockedPatterns == null) {
            unlockedPatterns = new HashSet<>();
        }
        if (unlockedMaterials == null) {
            unlockedMaterials = new HashSet<>();
        }
        if (helmet == null) {
            helmet = new PieceSettings();
        }
        if (chestplate == null) {
            chestplate = new PieceSettings();
        }
        if (leggings == null) {
            leggings = new PieceSettings();
        }
        if (boots == null) {
            boots = new PieceSettings();
        }
        for (ArmorPiece piece : ArmorPiece.VALUES) {
            PieceSettings settings = settings(piece);
            if (settings.pattern != null && !settings.pattern.isSupporterExclusive() && !unlockedPatterns.contains(settings.pattern)) {
                settings.pattern = null;
            }
            if (settings.material != null && !settings.material.isSupporterExclusive() && !unlockedMaterials.contains(settings.material)) {
                settings.material = null;
            }
            if (!settings.enchantUnlocked) {
                settings.enchantEnabled = false;
            }
        }
    }

    private boolean canEquip(@Nullable ArmorPattern pattern, boolean hasSupporter) {
        if (pattern == null) {
            return false;
        }
        if (pattern.isSupporterExclusive()) {
            return hasSupporter;
        }
        return unlockedPatterns.contains(pattern);
    }

    private boolean canEquip(@Nullable ArmorPatternMaterial material, boolean hasSupporter) {
        if (material == null) {
            return false;
        }
        if (material.isSupporterExclusive()) {
            return hasSupporter;
        }
        return unlockedMaterials.contains(material);
    }

    private PieceSettings settings(ArmorPiece piece) {
        return switch (piece) {
            case HELMET -> helmet;
            case CHESTPLATE -> chestplate;
            case LEGGINGS -> leggings;
            case BOOTS -> boots;
        };
    }

    public static class PieceSettings {

        @Nullable
        private ArmorPattern pattern;
        @Nullable
        private ArmorPatternMaterial material;
        private boolean enchantUnlocked;
        private boolean enchantEnabled;

    }

}
