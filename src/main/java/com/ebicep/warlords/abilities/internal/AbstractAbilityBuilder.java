package com.ebicep.warlords.abilities.internal;

public class AbstractAbilityBuilder {

    public static AbstractAbilityBuilder create(String fieldName) {
        return new AbstractAbilityBuilder(fieldName);
    }

    private String nameSpace = "";
    private String fieldName;
    private String name;
    private Float cooldown;
    private Float energyCost;
    private Float startCooldown = 0f;

    public AbstractAbilityBuilder(String fieldName) {
        this.fieldName = fieldName;
    }

    public AbstractAbilityBuilder pvp() {
        this.nameSpace = "pvp";
        return this;
    }

    public AbstractAbilityBuilder pve() {
        this.nameSpace = "pve";
        return this;
    }

    public AbstractAbilityBuilder td() {
        this.nameSpace = "towerDefense";
        return this;
    }

    public AbstractAbilityBuilder fieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public AbstractAbilityBuilder name(String name) {
        this.name = name;
        return this;
    }

    public AbstractAbilityBuilder cooldown(float cooldown) {
        this.cooldown = cooldown;
        return this;
    }

    public AbstractAbilityBuilder energyCost(float energyCost) {
        this.energyCost = energyCost;
        return this;
    }

    public AbstractAbilityBuilder startCooldown(float startCooldown) {
        this.startCooldown = startCooldown;
        return this;
    }

    public AbstractAbilityBuilder startNoCooldown(boolean startNoCooldown) {
        this.startCooldown = startNoCooldown ? 0f : this.cooldown;
        return this;
    }

    public AbstractAbilityBuilder startNoCooldown() {
        this.startCooldown = 0f;
        return this;
    }

    public AbstractAbilityBuilder startFullCooldown() {
        this.startCooldown = this.cooldown;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public Float getCooldown() {
        return cooldown;
    }

    public Float getEnergyCost() {
        return energyCost;
    }

    public Float getStartCooldown() {
        return startCooldown;
    }

    public String getAppendedFieldName(String... elements) {
        StringBuilder sb = new StringBuilder(fieldName);
        for (String element : elements) {
            sb.append(".");
            sb.append(element);
        }
        return sb.toString();
    }
} 
