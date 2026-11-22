package com.ebicep.warlords.player.general.mounts;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class MountProfile {

    private Set<MountType> unlockedTypes = new HashSet<>();
    private Set<MountArmor> unlockedArmors = new HashSet<>();
    private MountType selectedType = MountType.BROWN;
    @Nullable
    private MountArmor selectedArmor;

    public MountProfile() {
        ensureDefaults();
    }

    public boolean unlock(MountArmor armor) {
        ensureDefaults();
        return unlockedArmors.add(armor);
    }

    public boolean isUnlocked(MountType type) {
        ensureDefaults();
        return type == MountType.BROWN || unlockedTypes.contains(type);
    }

    public boolean isUnlocked(MountArmor armor) {
        ensureDefaults();
        return unlockedArmors.contains(armor);
    }

    public MountType getSelectedType() {
        ensureDefaults();
        return selectedType;
    }

    public boolean selectType(MountType type, boolean hasSupporter) {
        ensureDefaults();
        if (!canEquip(type, hasSupporter) || selectedType == type) {
            return false;
        }
        selectedType = type;
        return true;
    }

    @Nullable
    public MountArmor getSelectedArmor() {
        ensureDefaults();
        return selectedArmor;
    }

    public boolean selectArmor(@Nullable MountArmor armor, boolean hasSupporter) {
        ensureDefaults();
        if (armor != null && !canEquip(armor, hasSupporter)) {
            return false;
        }
        if (selectedArmor == armor) {
            return false;
        }
        selectedArmor = armor;
        return true;
    }

    public MountType getEffectiveType(boolean hasSupporter) {
        ensureDefaults();
        return canEquip(selectedType, hasSupporter) ? selectedType : MountType.BROWN;
    }

    @Nullable
    public MountArmor getEffectiveArmor(boolean hasSupporter) {
        ensureDefaults();
        return canEquip(selectedArmor, hasSupporter) ? selectedArmor : null;
    }

    public boolean applySupporterDefaults(boolean hasSupporter) {
        ensureDefaults();
        if (hasSupporter) {
            return false;
        }
        boolean changed = false;
        if (selectedType.isSupporterExclusive()) {
            selectedType = MountType.BROWN;
            changed = true;
        }
        if (selectedArmor != null && selectedArmor.isSupporterExclusive()) {
            selectedArmor = null;
            changed = true;
        }
        return changed;
    }

    public void ensureDefaults() {
        if (unlockedTypes == null) {
            unlockedTypes = new HashSet<>();
        }
        if (unlockedArmors == null) {
            unlockedArmors = new HashSet<>();
        }
        unlockedTypes.add(MountType.BROWN);
        if (selectedType == null) {
            selectedType = MountType.BROWN;
        }
        if (selectedArmor != null && !selectedArmor.isSupporterExclusive() && !unlockedArmors.contains(selectedArmor)) {
            selectedArmor = null;
        }
    }

    private boolean canEquip(@Nullable MountType type, boolean hasSupporter) {
        if (type == null) {
            return false;
        }
        if (type.isSupporterExclusive()) {
            return hasSupporter;
        }
        return true;
    }

    private boolean canEquip(@Nullable MountArmor armor, boolean hasSupporter) {
        if (armor == null) {
            return false;
        }
        if (armor.isSupporterExclusive()) {
            return hasSupporter && unlockedArmors.contains(armor);
        }
        return unlockedArmors.contains(armor);
    }

}
