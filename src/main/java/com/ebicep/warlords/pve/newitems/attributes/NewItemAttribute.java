package com.ebicep.warlords.pve.newitems.attributes;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.attributes.basic.*;
import com.ebicep.warlords.pve.newitems.attributes.bonus.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum NewItemAttribute implements Attribute {

    HEALTH(new Health()),
    COOLDOWN_REDUCTION(new CooldownReduction()),
    HEALTH_REGEN(new HealthRegen()),
    KNOCKBACK_RESISTANCE(new KnockbackResistance()),
    SKILL_ENERGY_COST_REDUCTION(new SkillEnergyCostReduction()),
    CRIT_CHANCE(new CritChance()),
    CRIT_MULTIPLIER(new CritMultiplier()),
    DAMAGE(new Damage()),
    DAMAGE_TO_BOSS_ENEMIES(new DamageToBossEnemies()),
    ENERGY_PER_HIT(new EnergyPerHit()),
    HEALING(new Healing()),
    MAX_ENERGY(new MaxEnergy()),
    SPEED(new Speed()),
    THORNS(new Thorns()),

    ;

    public static final NewItemAttribute[] VALUES = values();
    public static final NewItemAttribute[] BASIC_ATTRIBUTES = {
            HEALTH,
            HEALTH_REGEN,
            KNOCKBACK_RESISTANCE,
            COOLDOWN_REDUCTION,
            SKILL_ENERGY_COST_REDUCTION,
    };
    public static final NewItemAttribute[] BONUS_ATTRIBUTES = {
            DAMAGE_TO_BOSS_ENEMIES,
            SPEED,
            DAMAGE,
            HEALING,
            CRIT_CHANCE,
            CRIT_MULTIPLIER,
            MAX_ENERGY,
            ENERGY_PER_HIT,
            THORNS,
    };
    private static final Map<String, NewItemAttribute> BY_DB_NAME = Arrays
            .stream(VALUES)
            .collect(Collectors.toUnmodifiableMap(
                    NewItemAttribute::getDatabaseName,
                    Function.identity()
            ));

    public static NewItemAttribute getByDatabaseName(String databaseName) {
        return BY_DB_NAME.get(databaseName);
    }

    public final Attribute attribute;

    NewItemAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getDatabaseName() {
        return attribute.getDatabaseName();
    }

    @Override
    public TextColor getTextColor() {
        return attribute.getTextColor();
    }

    @Override
    public Component formatValue(int value) {
        return attribute.formatValue(value);
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer) {
        attribute.applyToWarlordsPlayer(warlordsPlayer);
    }

}
