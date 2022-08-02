package com.ebicep.warlords.pve.upgrades;

public class Upgrade {

    private String name;
    private String subName;
    private String description;
    private int currencyCost;
    private Runnable onUpgrade;
    private boolean isUnlocked = false;

    public Upgrade(String name, String description, int currencyCost) {
        this.name = name;
        this.description = description;
        this.currencyCost = currencyCost;
    }

    public Upgrade(String name, String description, int currencyCost, Runnable onUpgrade) {
        this.name = name;
        this.description = description;
        this.currencyCost = currencyCost;
        this.onUpgrade = onUpgrade;
    }

    public Upgrade(String name, String subName, String description, int currencyCost) {
        this.name = name;
        this.subName = subName;
        this.description = description;
        this.currencyCost = currencyCost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
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
