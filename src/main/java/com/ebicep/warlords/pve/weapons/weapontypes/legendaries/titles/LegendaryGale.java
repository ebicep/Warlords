package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.springframework.data.annotation.Transient;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class LegendaryGale extends AbstractLegendaryWeapon {

    private static final int ABILITY_ENERGY_DECREASE = 10;
    private static final float ABILITY_ENERGY_DECREASE_PER_UPGRADE = 2.5f;
    private static final int DURATION = 10;
    private static final int DURATION_PER_UPGRADE = 1;

    @Transient
    private LegendaryGaleAbility ability;

    public LegendaryGale() {
    }

    public LegendaryGale(UUID uuid) {
        super(uuid);
    }

    public LegendaryGale(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        ability = null;
    }

    @Override
    public TextComponent getPassiveEffect() {
        return ComponentBuilder.create("Increase movement speed by 50%, decrease energy consumption of all abilities by ", NamedTextColor.GRAY)
                               .append(formatTitleUpgrade(ABILITY_ENERGY_DECREASE + ABILITY_ENERGY_DECREASE_PER_UPGRADE * getTitleLevel()))
                               .text(", and gain 35% knockback resistance for ")
                               .append(formatTitleUpgrade(DURATION + DURATION_PER_UPGRADE * getTitleLevel(), "s"))
                               .text(". Can be triggered every 30 seconds.")
                               .build();
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.GALE;
    }

    @Override
    public LegendaryGaleAbility getAbility() {
        return ability;
    }

    @Override
    public void resetAbility() {
        ability = new LegendaryGaleAbility(
                ABILITY_ENERGY_DECREASE + ABILITY_ENERGY_DECREASE_PER_UPGRADE * getTitleLevel(),
                35,
                DURATION + DURATION_PER_UPGRADE * getTitleLevel()
        );
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 150;
    }

    @Override
    protected float getHealthBonusValue() {
        return 250;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 20;
    }

    @Override
    protected float getEnergyPerHitBonusValue() {
        return 2;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 170;
    }

    @Override
    protected float getCritChanceValue() {
        return 10;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 185;
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(
                new Pair<>(
                        formatTitleUpgrade(ABILITY_ENERGY_DECREASE + ABILITY_ENERGY_DECREASE_PER_UPGRADE * getTitleLevel()),
                        formatTitleUpgrade(ABILITY_ENERGY_DECREASE + ABILITY_ENERGY_DECREASE_PER_UPGRADE * getTitleLevelUpgraded())
                ),
                new Pair<>(
                        formatTitleUpgrade(DURATION + DURATION_PER_UPGRADE * getTitleLevel(), "s"),
                        formatTitleUpgrade(DURATION + DURATION_PER_UPGRADE * getTitleLevelUpgraded(), "s"
                        )
                )
        );
    }

    static class LegendaryGaleAbility extends AbstractAbility {

        private final float abilityEnergyDecrease;
        private final float knockbackResistance;
        private final int duration;

        public LegendaryGaleAbility(float abilityEnergyDecrease, float knockbackResistance, int duration) {
            super(AbstractAbilityBuilder.create("gale").weapon());
            this.abilityEnergyDecrease = abilityEnergyDecrease;
            this.knockbackResistance = knockbackResistance;
            this.duration = duration;
        }

        @Override
        public void updateDescription(Player player) {
            description = Component.text("Increase movement speed by ")
                                   .append(Component.text("50%", NamedTextColor.YELLOW))
                                   .append(Component.text(", decrease energy consumption of all abilities by "))
                                   .append(Component.text(DECIMAL_FORMAT_TITLE.format(abilityEnergyDecrease), NamedTextColor.YELLOW))
                                   .append(Component.text(" and gain "))
                                   .append(Component.text(DECIMAL_FORMAT_TITLE.format(knockbackResistance) + "%", NamedTextColor.YELLOW))
                                   .append(Component.text(" knockback resistance for "))
                                   .append(Component.text(duration, NamedTextColor.GOLD))
                                   .append(Component.text(" seconds."));
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            Runnable cancelSpeed = wp.addSpeedModifier(wp, name, 50, 10 * 20, "BASE");
            List<FloatModifiable.FloatModifier> modifiers = wp
                    .getAbilities()
                    .stream()
                    .map(a -> a.getEnergyCost().addAdditiveModifier("Gale", -abilityEnergyDecrease))
                    .toList();
            wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                    name,
                    "GALE",
                    LegendaryGale.class,
                    new LegendaryGale(),
                    wp,
                    CooldownTypes.WEAPON,
                    cooldownManager -> {
                    },
                    cooldownManager -> {
                        modifiers.forEach(FloatModifiable.FloatModifier::forceEnd);
                        cancelSpeed.run();
                    },
                    duration * 20
            ) {
                @Override
                public void multiplyKB(Vector currentVector) {
                    currentVector.multiply(1 - knockbackResistance / 100);
                }
            });

            return true;
        }
    }
}
