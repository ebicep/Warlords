package com.ebicep.warlords.pve.newitems.attributes;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.attributes.basic.*;
import com.ebicep.warlords.pve.newitems.attributes.bonus.*;
import com.ebicep.warlords.util.java.NamedEnum;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum NewItemAttribute implements Attribute, NamedEnum {

    HEALTH(new Health()),
    HEALTH_REGEN(new HealthRegen()),
    KNOCKBACK_RESISTANCE(new KnockbackResistance()),
    COOLDOWN_REDUCTION(new CooldownReduction()),
    SKILL_ENERGY_COST_REDUCTION(new SkillEnergyCostReduction()),
    DAMAGE_TO_BOSS_ENEMIES(new DamageToBossEnemies()),
    SPEED(new Speed()),
    DAMAGE(new Damage()),
    HEALING(new Healing()),
    CRIT_CHANCE(new CritChance()),
    CRIT_MULTIPLIER(new CritMultiplier()),
    MAX_ENERGY(new MaxEnergy()),
    ENERGY_PER_HIT(new EnergyPerHit()),
    THORNS(new Thorns()),
    ATTACK_SPEED(new AttackSpeed()),

    ;

    public static final NewItemAttribute[] VALUES = values();
    public static final NewItemAttribute[] BASIC_ATTRIBUTES = {
            HEALTH,
            HEALTH_REGEN,
            KNOCKBACK_RESISTANCE,
            COOLDOWN_REDUCTION,
            SKILL_ENERGY_COST_REDUCTION,
    };
    public static final Set<NewItemAttribute> BASIC_ATTRIBUTE_SET = Set.of(BASIC_ATTRIBUTES);
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
    public static final Set<NewItemAttribute> BONUS_ATTRIBUTE_SET = Set.of(BONUS_ATTRIBUTES);
    /**
     * Attributes that cannot be rolled onto an item and are only granted by gems. Kept out of {@link #BONUS_ATTRIBUTES}
     * so item generation never picks an attribute that has no configured range.
     */
    public static final NewItemAttribute[] GEM_ONLY_ATTRIBUTES = {
            ATTACK_SPEED,
    };
    /**
     * Every attribute that can show up on an item, in the order it should be displayed.
     */
    public static final NewItemAttribute[] DISPLAY_ORDER = Stream
            .of(BONUS_ATTRIBUTES, GEM_ONLY_ATTRIBUTES)
            .flatMap(Arrays::stream)
            .toArray(NewItemAttribute[]::new);
    public static final Set<NewItemAttribute> DISPLAY_ORDER_SET = Set.of(DISPLAY_ORDER);
    private static final Map<String, NewItemAttribute> BY_DB_NAME = Arrays
            .stream(VALUES)
            .collect(Collectors.toUnmodifiableMap(
                    NewItemAttribute::getDatabaseName,
                    Function.identity()
            ));

    public static NewItemAttribute getByDatabaseName(String databaseName) {
        return BY_DB_NAME.get(databaseName);
    }

    private final Attribute attribute;

    NewItemAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getDatabaseName() {
        return attribute.getDatabaseName();
    }

    @Override
    public String getName() {
        return attribute.getName();
    }

    @Override
    public TextColor getTextColor() {
        return attribute.getTextColor();
    }

    @Override
    public ItemStack getItemStack() {
        return attribute.getItemStack();
    }

    @Override
    public Component formatValue(float value, String prefix) {
        return attribute.formatValue(value, prefix);
    }

    @Override
    public void apply(WarlordsPlayer warlordsPlayer, float value) {
        attribute.apply(warlordsPlayer, value);
    }

}
