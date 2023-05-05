package com.ebicep.warlords.pve.upgrades;

import com.ebicep.warlords.pve.DifficultyMode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class AutoUpgradeProfile {

    public static final Component AUTO_UPGRADE_PREFIX = Component.textOfChildren(
            Component.text("Auto Upgrade", NamedTextColor.AQUA),
            Component.text(" > ", NamedTextColor.GRAY)
    );

    private String name = "Default";
    @Field("profiles")
    private List<AutoUpgradeEntry> autoUpgradeEntries = new ArrayList<>();
    private DifficultyMode difficultyMode = DifficultyMode.ANY;

    public AutoUpgradeProfile() {
    }

    public AutoUpgradeProfile(String name) {
        this.name = name;
    }

    public AutoUpgradeProfile(AutoUpgradeProfile autoUpgradeProfile) {
        this.autoUpgradeEntries = new ArrayList<>(autoUpgradeProfile.autoUpgradeEntries);
    }

    public void addEntry(int branchIndex, int upgradeIndex, AutoUpgradeEntry.UpgradeType upgradeType) {
        autoUpgradeEntries.add(new AutoUpgradeEntry(branchIndex, upgradeIndex, upgradeType));
    }

    public List<Component> getLore(AbilityTree abilityTree) {
        List<Component> lore = new ArrayList<>();
        List<AbstractUpgradeBranch<?>> upgradeBranches = abilityTree.getUpgradeBranches();
        for (int i = 0, autoUpgradeEntriesSize = autoUpgradeEntries.size(); i < autoUpgradeEntriesSize; i++) {
            AutoUpgradeEntry entry = autoUpgradeEntries.get(i);
            AbstractUpgradeBranch<?> upgradeBranch = upgradeBranches.get(entry.getBranchIndex());
            Upgrade upgrade = entry.getUpgradeType().getUpgradeFunction.apply(upgradeBranch).get(entry.getUpgradeIndex());
            NamedTextColor textColor = upgrade.isUnlocked() ? NamedTextColor.GOLD : NamedTextColor.GRAY;
            Component position = Component.text((i + 1) + ". ", NamedTextColor.AQUA);
            if (entry.getUpgradeType() == AutoUpgradeEntry.UpgradeType.MASTER) {
                lore.add(position.append(Component.text(upgradeBranch.getAbility().getName() + " - ", textColor))
                                 .append(Component.text(upgrade.getName(), NamedTextColor.RED, TextDecoration.BOLD)));
            } else {
                lore.add(position.append(Component.text(upgradeBranch.getAbility().getName() + " - " + upgrade.getName(), textColor)));
            }
        }
        return lore;
    }

    public Component getPosition(AbilityTree abilityTree, Upgrade upgrade) {
        List<AbstractUpgradeBranch<?>> upgradeBranches = abilityTree.getUpgradeBranches();
        for (int i = 0, autoUpgradeEntriesSize = autoUpgradeEntries.size(); i < autoUpgradeEntriesSize; i++) {
            AutoUpgradeEntry entry = autoUpgradeEntries.get(i);
            AbstractUpgradeBranch<?> upgradeBranch = upgradeBranches.get(entry.getBranchIndex());
            Upgrade currentUpgrade = entry.getUpgradeType().getUpgradeFunction.apply(upgradeBranch).get(entry.getUpgradeIndex());
            if (currentUpgrade == upgrade) {
                return Component.text("#" + (i + 1) + "/" + autoUpgradeEntries.size(), NamedTextColor.AQUA);
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DifficultyMode getDifficultyMode() {
        return difficultyMode;
    }

    public void setDifficultyMode(DifficultyMode difficultyMode) {
        this.difficultyMode = difficultyMode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(autoUpgradeEntries);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AutoUpgradeProfile otherProfile = (AutoUpgradeProfile) o;
        if (!name.equals(otherProfile.name)) {
            return false;
        }
        if (!difficultyMode.equals(otherProfile.difficultyMode)) {
            return false;
        }
        List<AutoUpgradeEntry> otherEntries = otherProfile.getAutoUpgradeEntries();
        if (otherEntries.size() != autoUpgradeEntries.size()) {
            return false;
        }
        for (int i = 0; i < autoUpgradeEntries.size(); i++) {
            AutoUpgradeEntry entry = autoUpgradeEntries.get(i);
            AutoUpgradeEntry otherEntry = otherEntries.get(i);
            if (!entry.equals(otherEntry)) {
                return false;
            }
        }
        return true;
    }

    public List<AutoUpgradeEntry> getAutoUpgradeEntries() {
        return autoUpgradeEntries;
    }

    public static class AutoUpgradeEntry {

        @Field("branch_index")
        private int branchIndex;
        @Field("upgrade_index")
        private int upgradeIndex;
        @Field("type")
        private UpgradeType upgradeType;

        public AutoUpgradeEntry(int branchIndex, int upgradeIndex, UpgradeType upgradeType) {
            this.branchIndex = branchIndex;
            this.upgradeIndex = upgradeIndex;
            this.upgradeType = upgradeType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(branchIndex, upgradeIndex, upgradeType);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            AutoUpgradeEntry that = (AutoUpgradeEntry) o;
            return branchIndex == that.branchIndex && upgradeIndex == that.upgradeIndex && upgradeType == that.upgradeType;
        }

        public int getBranchIndex() {
            return branchIndex;
        }

        public int getUpgradeIndex() {
            return upgradeIndex;
        }

        public UpgradeType getUpgradeType() {
            return upgradeType;
        }

        public enum UpgradeType {
            A(AbstractUpgradeBranch::getTreeA),
            B(AbstractUpgradeBranch::getTreeB),
            MASTER(upgradeBranch -> Collections.singletonList(upgradeBranch.getMasterUpgrade()));

            public final Function<AbstractUpgradeBranch<?>, List<Upgrade>> getUpgradeFunction;

            UpgradeType(Function<AbstractUpgradeBranch<?>, List<Upgrade>> getUpgradeFunction) {
                this.getUpgradeFunction = getUpgradeFunction;
            }
        }
    }
}
