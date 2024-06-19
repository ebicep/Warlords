package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.springframework.data.annotation.Transient;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class LegendaryVigorous extends AbstractLegendaryWeapon {

    public static final int EPS = 25;
    public static final int EPS_PER_UPGRADE = 4;
    public static final int DURATION = 10;

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
    public TextComponent getPassiveEffect() {
        return Component.text("", NamedTextColor.GRAY)
                        .append(formatTitleUpgrade("+", EPS + EPS_PER_UPGRADE * getTitleLevel()))
                        .append(Component.text(" energy per second for " + DURATION + " seconds. Can be triggered every 30 seconds."));
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.VIGOROUS;
    }

    @Override
    public LegendaryVigorousAbility getAbility() {
        return ability;
    }

    @Override
    public void resetAbility() {
        ability = new LegendaryVigorousAbility(EPS + EPS_PER_UPGRADE * getTitleLevel());
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
        return 2;
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
        return Collections.singletonList(new Pair<>(
                formatTitleUpgrade("+", EPS + EPS_PER_UPGRADE * getTitleLevel()),
                formatTitleUpgrade("+", EPS + EPS_PER_UPGRADE * getTitleLevelUpgraded())
        ));
    }

    static class LegendaryVigorousAbility extends AbstractAbility {

        private final float energyPerSecond;

        public LegendaryVigorousAbility(float energyPerSecond) {
            super("Vigorous", 0, 0, 30, 0);
            this.energyPerSecond = energyPerSecond;
        }

        @Override
        public void updateDescription(Player player) {
            description = Component.text("+" + DECIMAL_FORMAT_TITLE.format(energyPerSecond), NamedTextColor.YELLOW)
                                   .append(Component.text(" energy per second for "))
                                   .append(Component.text("10", NamedTextColor.GOLD))
                                   .append(Component.text(" seconds."));
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                    "LegendaryVigorous",
                    "VIGOR",
                    LegendaryVigorous.class,
                    null,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    DURATION * 20
            ) {
                @Override
                public float addEnergyGainPerTick(float energyGainPerTick) {
                    return energyGainPerTick + (energyPerSecond / 20);
                }
            });
            return true;
        }

    }
}

