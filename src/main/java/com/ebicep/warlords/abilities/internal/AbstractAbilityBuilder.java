package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.database.repositories.config.ConfigManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbstractAbilityBuilder {

    public static AbstractAbilityBuilder create(String fieldName) {
        return new AbstractAbilityBuilder(fieldName);
    }

    private final List<String> namespaces = new ArrayList<>(ConfigManager.DEFAULT_NAMESPACES);
    private String fieldName;
    private String name;
    private Float cooldown;
    private Float energyCost;
    private Float startCooldown = 0f;

    public AbstractAbilityBuilder(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String toString() {
        return "AbstractAbilityBuilder{" +
                "namespaces=" + namespaces +
                ", fieldName='" + fieldName + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public AbstractAbilityBuilder pvp() {
        this.namespaces.remove("pvp");
        this.namespaces.addFirst("pvp");
        return this;
    }

    public AbstractAbilityBuilder pve() {
        this.namespaces.remove("pve");
        this.namespaces.addFirst("pve");
        return this;
    }

    public AbstractAbilityBuilder td() {
        this.namespaces.remove("td");
        this.namespaces.addFirst("td");
        return this;
    }

    public AbstractAbilityBuilder weapon() {
        this.namespaces.remove("weapon");
        this.namespaces.addFirst("weapon");
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

    public List<String> getNamespaces() {
        return namespaces;
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

    public String getAppendedFieldNameDamage(String... elements) {
        List<String> e = new ArrayList<>();
        e.addFirst("damageValues");
        e.addAll(Arrays.asList(elements));
        return getAppendedFieldName(e.toArray(new String[0]));
    }

    public String getAppendedFieldNameHealing(String... elements) {
        List<String> e = new ArrayList<>();
        e.addFirst("healingValues");
        e.addAll(Arrays.asList(elements));
        return getAppendedFieldName(e.toArray(new String[0]));
    }

}
