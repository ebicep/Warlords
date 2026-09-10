package com.ebicep.warlords.player.general;

import com.ebicep.warlords.abilities.internal.AbilityDescriptionBuilder;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.custom.ItemAdditiveCooldown;
import com.ebicep.warlords.player.ingame.motionsystem.MotionModifier;
import com.ebicep.warlords.pve.newitems.attributes.NewItemCooldown;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.ArrayList;
import java.util.List;

public final class SpecializationStatsLore {

    public static final float BASE_WALK_SPEED_OFFSET = 13f;
    private static final String[] ITEM_MODIFIER_LOGS = {"Item", "Item Special Stat Pool"};
    private static final TextColor THORNS_COLOR = TextColor.color(95, 120, 95);
    private static final TextColor BOSS_DAMAGE_COLOR = TextColor.color(65, 120, 255);

    private SpecializationStatsLore() {
    }

    public static List<Component> forPlayer(WarlordsPlayer warlordsPlayer) {
        List<Component> lore = new ArrayList<>(specializationStats(
                warlordsPlayer.getMaxHealth(),
                warlordsPlayer.getMaxEnergy(),
                warlordsPlayer.getEnergyPerSec().getCalculatedValue(),
                warlordsPlayer.getEnergyPerHit().getCalculatedValue(),
                warlordsPlayer.getSpec().getDamageResistance(),
                getCalculatedSpeedPercent(warlordsPlayer)
        ));
        List<Component> itemLore = itemStats(collectItemBonuses(warlordsPlayer));
        if (!itemLore.isEmpty()) {
            lore.add(Component.empty());
            lore.addAll(itemLore);
        }
        return lore;
    }

    public static List<Component> specializationStats(AbstractPlayerClass playerClass) {
        return specializationStats(
                playerClass.getMaxHealth(),
                playerClass.getMaxEnergy(),
                playerClass.getEnergyPerSec(),
                playerClass.getEnergyPerHit(),
                playerClass.getDamageResistance(),
                playerClass.getSpeed()
        );
    }

    public static List<Component> specializationStats(
            float health,
            float maxEnergy,
            float energyPerSec,
            float energyPerHit,
            float damageResistance,
            float speed
    ) {
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("Specialization Stats:", NamedTextColor.GOLD));
        lore.add(Component.empty());
        lore.add(Component.text("Health: ", NamedTextColor.GRAY)
                          .append(Component.text(NumberFormat.formatOptionalHundredths(health), NamedTextColor.GREEN)));
        lore.add(Component.empty());
        lore.add(Component.text("Energy: ", NamedTextColor.GRAY)
                          .append(Component.text(NumberFormat.formatOptionalHundredths(maxEnergy), NamedTextColor.YELLOW))
                          .append(Component.text(" / "))
                          .append(Component.text("+" + NumberFormat.formatOptionalHundredths(energyPerSec), NamedTextColor.YELLOW))
                          .append(Component.text(" per sec / "))
                          .append(Component.text("+" + NumberFormat.formatOptionalHundredths(energyPerHit), NamedTextColor.YELLOW))
                          .append(Component.text(" per hit")));
        boolean noDamageResistance = damageResistance == 0;
        lore.add(Component.text("Damage Reduction: ", NamedTextColor.GRAY)
                          .append(Component.text(noDamageResistance ? "None" : NumberFormat.formatOptionalTenths(damageResistance) + "%",
                                  noDamageResistance ? NamedTextColor.RED : AbilityDescriptionBuilder.COLOR_BROWN
                          )));
        boolean noSpeed = speed == 0;
        lore.add(Component.text("Speed: ", NamedTextColor.GRAY)
                          .append(Component.text(noSpeed ? "None" : NumberFormat.formatOptionalTenths(speed) + "%",
                                  noSpeed ? NamedTextColor.RED : NamedTextColor.WHITE
                          )));
        return lore;
    }

    public static List<Component> itemStats(ItemBonuses bonuses) {
        List<Component> lore = new ArrayList<>();
        if (hasValue(bonuses.cooldownReduction())) {
            lore.add(labeledStat("Cooldown Reduction",
                    "-" + NumberFormat.formatOptionalTenths(bonuses.cooldownReduction()) + "%",
                    NamedTextColor.AQUA));
        }
        if (hasValue(bonuses.skillEnergyCostReduction())) {
            lore.add(labeledStat("Skill Energy Cost Reduction",
                    "-" + NumberFormat.formatOptionalTenths(bonuses.skillEnergyCostReduction()),
                    NamedTextColor.YELLOW));
        }
        if (hasValue(bonuses.thorns())) {
            String thornsValue = NumberFormat.formatOptionalTenths(bonuses.thorns()) + "%";
            if (bonuses.maxThornsDamage() > 0) {
                thornsValue += " (max " + bonuses.maxThornsDamage() + ")";
            }
            lore.add(labeledStat("Thorns", thornsValue, THORNS_COLOR));
        }
        addPercentStat(lore, "Damage", bonuses.damage(), NamedTextColor.RED);
        addPercentStat(lore, "Healing", bonuses.healing(), NamedTextColor.GREEN);
        addPercentStat(lore, "Crit Chance", bonuses.critChance(), NamedTextColor.LIGHT_PURPLE);
        addPercentStat(lore, "Crit Multiplier", bonuses.critMultiplier(), NamedTextColor.LIGHT_PURPLE);
        addPercentStat(lore, "Damage to Boss Enemies", bonuses.damageToBosses(), BOSS_DAMAGE_COLOR);
        addPercentStat(lore, "Knockback Resistance", bonuses.knockbackResistance(), AbilityDescriptionBuilder.COLOR_BROWN);
        if (hasValue(bonuses.healthRegen())) {
            lore.add(labeledStat("Health Regen",
                    NumberFormat.formatOptionalTenths(bonuses.healthRegen()) + "/s",
                    NamedTextColor.RED));
        }
        addPercentStat(lore, "Attack Speed", bonuses.attackSpeed(), NamedTextColor.RED);
        if (lore.isEmpty()) {
            return lore;
        }
        List<Component> withHeader = new ArrayList<>(lore.size() + 2);
        withHeader.add(Component.text("Item Stats:", NamedTextColor.GOLD));
        withHeader.add(Component.empty());
        withHeader.addAll(lore);
        return withHeader;
    }

    public static float getCalculatedSpeedPercent(WarlordsPlayer warlordsPlayer) {
        if (warlordsPlayer.getSpeed() != null) {
            for (MotionModifier modifier : warlordsPlayer.getSpeed().getModifiers()) {
                if ("BASE".equals(modifier.getName())) {
                    return modifier.getModifier() - BASE_WALK_SPEED_OFFSET;
                }
            }
        }
        if (warlordsPlayer.getSpec() != null) {
            return warlordsPlayer.getSpec().getSpeed();
        }
        return 0;
    }

    public static ItemBonuses collectItemBonuses(WarlordsPlayer warlordsPlayer) {
        float thorns = 0;
        int maxThornsDamage = 0;
        float damage = 0;
        float healing = 0;
        float critChance = 0;
        float critMultiplier = 0;
        float damageToBosses = 0;
        float knockbackResistance = 0;

        NewItemCooldown newItemCooldown = findCooldown(warlordsPlayer, "Item", NewItemCooldown.class);
        if (newItemCooldown != null) {
            thorns += newItemCooldown.getThorns() * 100;
            maxThornsDamage = Math.max(maxThornsDamage, newItemCooldown.getMaxThornsDamage());
            damage += (newItemCooldown.getDamageMultiplier() - 1) * 100;
            healing += (newItemCooldown.getHealMultiplier() - 1) * 100;
            critChance += newItemCooldown.getAdditionalCritChance();
            critMultiplier += newItemCooldown.getAdditionalCritMultiplier();
            damageToBosses += (newItemCooldown.getDamageBossMultiplier() - 1) * 100;
            knockbackResistance += newItemCooldown.getKbMultiplier() * 2;
        }
        ItemAdditiveCooldown itemAdditiveCooldown = findCooldown(warlordsPlayer, "Item Additive", ItemAdditiveCooldown.class);
        if (itemAdditiveCooldown != null) {
            thorns += itemAdditiveCooldown.getThorns() * 100;
            maxThornsDamage = Math.max(maxThornsDamage, itemAdditiveCooldown.getMaxThornsDamage());
            damage += (itemAdditiveCooldown.getDamageMultiplier() - 1) * 100;
            healing += (itemAdditiveCooldown.getHealMultiplier() - 1) * 100;
            critChance += itemAdditiveCooldown.getAdditionalCritChance();
            critMultiplier += itemAdditiveCooldown.getAdditionalCritMultiplier();
            knockbackResistance += itemAdditiveCooldown.getKbMultiplier() * 2;
        }

        return new ItemBonuses(
                getItemCooldownReductionPercent(warlordsPlayer),
                getItemEnergyCostReduction(warlordsPlayer),
                thorns,
                maxThornsDamage,
                damage,
                healing,
                critChance,
                critMultiplier,
                damageToBosses,
                knockbackResistance,
                warlordsPlayer.getRegenPerSecond().getCalculatedValue(),
                getItemAttackSpeedPercent(warlordsPlayer)
        );
    }

    private static float getItemCooldownReductionPercent(WarlordsPlayer warlordsPlayer) {
        for (AbstractAbility ability : warlordsPlayer.getAbilities()) {
            float product = combinedMultiplicativeProduct(ability.getCooldown(), ITEM_MODIFIER_LOGS);
            if (product != 1f) {
                return (1 - product) * 100f;
            }
        }
        return 0;
    }

    private static float getItemEnergyCostReduction(WarlordsPlayer warlordsPlayer) {
        for (AbstractAbility ability : warlordsPlayer.getAbilities()) {
            float reduction = -sumAdditiveModifiers(ability.getEnergyCost(), ITEM_MODIFIER_LOGS);
            if (reduction != 0) {
                return reduction;
            }
        }
        return 0;
    }

    private static float getItemAttackSpeedPercent(WarlordsPlayer warlordsPlayer) {
        FloatModifiable.FloatModifier modifier = warlordsPlayer.getPveHitCooldown()
                                                               .getModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "Item");
        if (modifier == null || modifier.isDisabled()) {
            return 0;
        }
        return -modifier.getModifier() * 100f;
    }

    private static float combinedMultiplicativeProduct(FloatModifiable modifiable, String... logs) {
        float product = 1f;
        for (String log : logs) {
            FloatModifiable.FloatModifier modifier = modifiable.getModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, log);
            if (modifier != null && !modifier.isDisabled()) {
                product *= modifier.getModifier();
            }
        }
        return product;
    }

    private static float sumAdditiveModifiers(FloatModifiable modifiable, String... logs) {
        float sum = 0;
        for (String log : logs) {
            FloatModifiable.FloatModifier modifier = modifiable.getModifier(FloatModifiable.ModifierType.ADDITIVE, log);
            if (modifier != null && !modifier.isDisabled()) {
                sum += modifier.getModifier();
            }
        }
        return sum;
    }

    private static <T extends PermanentCooldown<?>> T findCooldown(WarlordsPlayer warlordsPlayer, String name, Class<T> type) {
        return new CooldownFilter<>(warlordsPlayer, PermanentCooldown.class)
                .filterCooldownName(name)
                .findAny()
                .filter(type::isInstance)
                .map(type::cast)
                .orElse(null);
    }

    private static void addPercentStat(List<Component> lore, String label, float value, TextColor valueColor) {
        if (hasValue(value)) {
            lore.add(labeledStat(label, NumberFormat.formatOptionalTenths(value) + "%", valueColor));
        }
    }

    private static Component labeledStat(String label, String value, TextColor valueColor) {
        return Component.text(label + ": ", NamedTextColor.GRAY).append(Component.text(value, valueColor));
    }

    private static boolean hasValue(float value) {
        return Math.abs(value) > 0.0001f;
    }

    public record ItemBonuses(
            float cooldownReduction,
            float skillEnergyCostReduction,
            float thorns,
            int maxThornsDamage,
            float damage,
            float healing,
            float critChance,
            float critMultiplier,
            float damageToBosses,
            float knockbackResistance,
            float healthRegen,
            float attackSpeed
    ) {
        public static ItemBonuses none() {
            return new ItemBonuses(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        }
    }

}
