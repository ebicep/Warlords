package com.ebicep.warlords.pve.upgrades;

import com.ebicep.warlords.util.bukkit.WordWrap;

public class Upgrade {

    private String name;
    private String subName;
    private String description;
    private int currencyCost;
    private Runnable onUpgrade;
    private boolean isUnlocked = false;

    /**
     * @param name name of the upgrade.
     * @param description description of the upgrade.
     * @param currencyCost how much does the upgrade cost to unlock.
     * @param onUpgrade runnable to execute when the upgrade is unlocked.
     */
    public Upgrade(String name, String description, int currencyCost, Runnable onUpgrade) {
        this.name = name;
        this.description = description;
        this.currencyCost = currencyCost;
        this.onUpgrade = onUpgrade;
    }

    /**
     * @param name name of the master upgrade.
     * @param subName sub name of the master upgrade (e.g. old ability name) should always include "Master Upgrade"
     * @param description description of the upgrade.
     * @param currencyCost how much does the upgrade cost to unlock.
     * @param onUpgrade runnable to execute when the upgrade is unlocked.
     */
    public Upgrade(String name, String subName, String description, int currencyCost, Runnable onUpgrade) {
        this.name = name;
        this.subName = subName;
        this.description = description;
        this.currencyCost = currencyCost;
        this.onUpgrade = onUpgrade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return WordWrap.wrapWithNewline(description, 150);
    }

    public String getDescriptionRaw() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }

    public int getCurrencyCost() {
        return currencyCost;
    }

    public void setCurrencyCost(int currencyCost) {
        this.currencyCost = currencyCost;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public Runnable getOnUpgrade() {
        return onUpgrade;
    }

    public void setOnUpgrade(Runnable onUpgrade) {
        this.onUpgrade = onUpgrade;
    }
}
