package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.springframework.data.annotation.Transient;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class LegendaryVigorous extends AbstractLegendaryWeapon {

    public static final int EPS = 25;
    public static final int EPS_PER_UPGRADE = 3;
    public static final int DURATION = 10;
    public static final int DURATION_PER_UPGRADE = 1;

    @Transient
    private LegendaryVigorousAbility ability;

    public LegendaryVigorous() {
    }

    public LegendaryVigorous(UUID uuid) {
        super(uuid);
    }

    public LegendaryVigorous(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        ability = null;
    }

    @Override
    public TextComponent getPassiveEffect() {
        return ComponentBuilder.create("", NamedTextColor.GRAY)
                               .append(formatTitleUpgrade("+", EPS + EPS_PER_UPGRADE * getTitleLevel()))
                               .text(" energy per second for ")
                               .append(formatTitleUpgrade(DURATION + DURATION_PER_UPGRADE * getTitleLevel(), "s"))
                               .text(". Can be triggered every 30 seconds.")
                               .build();
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.VIGOROUS;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 140;
    }

    @Override
    protected float getHealthBonusValue() {
        return 600;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 10;
    }

    @Override
    protected float getEnergyPerSecondBonusValue() {
        return 3;
    }

    @Override
    public void resetAbility() {
        ability = new LegendaryVigorousAbility(EPS + EPS_PER_UPGRADE * getTitleLevel(), DURATION + DURATION_PER_UPGRADE * getTitleLevel());
    }

    @Override
    public LegendaryVigorousAbility getAbility() {
        return ability;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 170;
    }

    @Override
    protected float getCritChanceValue() {
        return 20;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 180;
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(
                new Pair<>(
                        formatTitleUpgrade("+", EPS + EPS_PER_UPGRADE * getTitleLevel()),
                        formatTitleUpgrade("+", EPS + EPS_PER_UPGRADE * getTitleLevelUpgraded())
                ),
                new Pair<>(
                        formatTitleUpgrade(DURATION + DURATION_PER_UPGRADE * getTitleLevel(), "s"),
                        formatTitleUpgrade(DURATION + DURATION_PER_UPGRADE * getTitleLevelUpgraded(), "s")
                )
        );
    }

    static class LegendaryVigorousAbility extends AbstractAbility {

        private final float energyPerSecond;
        private final int duration;

        public LegendaryVigorousAbility(float energyPerSecond, int duration) {
            super(AbstractAbilityBuilder.create("vigorous").weapon());
            this.energyPerSecond = energyPerSecond;
            this.duration = duration;
        }

        @Override
        protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
            wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                    name,
                    "VIGOR",
                    LegendaryVigorous.class,
                    null,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    duration * 20
            ).addModifier(Modifier.ENERGY_GAIN_PER_TICK, energyGainPerTick -> energyGainPerTick.addAdditiveModifier(name, energyPerSecond / 20f)));
            return true;
        }

        @Override
        public void updateDescription(Player player) {
            description = Component.text("+" + DECIMAL_FORMAT_TITLE.format(energyPerSecond), NamedTextColor.YELLOW)
                                   .append(Component.text(" energy per second for "))
                                   .append(Component.text("10", NamedTextColor.GOLD))
                                   .append(Component.text(" seconds."));
        }

    }

}

