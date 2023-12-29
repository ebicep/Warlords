package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.PassiveCounter;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.springframework.data.annotation.Transient;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class LegendaryFulcrum extends AbstractLegendaryWeapon implements GardenOfHesperidesTitle, PassiveCounter {

    public static final int SHIELD_PERCENT = 25;
    public static final int COOLDOWN = 20;
    public static final int COOLDOWN_PER_UPGRADE = 1;
    public static final int EPS_BOOST = 10;
    public static final int EPS_BOOST_PER_UPGRADE = 2;

    @Transient
    private int secondCounter = 0;

    public LegendaryFulcrum() {
    }

    public LegendaryFulcrum(UUID uuid) {
        super(uuid);
    }

    public LegendaryFulcrum(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public LinkedHashMap<Currencies, Long> getCost() {
        LinkedHashMap<Currencies, Long> baseCost = super.getCost();
        baseCost.put(Currencies.TITLE_TOKEN_GARDEN_OF_HESPERIDES, 1L);
        return baseCost;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);
        this.secondCounter = 0;

        float epsBoost = (EPS_BOOST + EPS_BOOST_PER_UPGRADE * getTitleLevel()) / 20f;
        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getTitleName(),
                null,
                LegendaryFulcrum.class,
                null,
                player,
                CooldownTypes.WEAPON,
                cooldownManager -> {
                },
                false
        ) {
            @Override
            public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (secondCounter != 0) {
                    return;
                }
                secondCounter = COOLDOWN + COOLDOWN_PER_UPGRADE * getTitleLevel();
                float shieldHealth = player.getMaxBaseHealth() * SHIELD_PERCENT / 100;
                player.getCooldownManager().addCooldown(new RegularCooldown<>(
                        getTitleName(),
                        null,
                        Shield.class,
                        new Shield(getTitleName(), shieldHealth),
                        player,
                        CooldownTypes.WEAPON,
                        cooldownManager -> {
                        },
                        200
                ) {
                    @Override
                    public float addEnergyGainPerTick(float energyGainPerTick) {
                        return energyGainPerTick + epsBoost;
                    }
                });
            }
        });

        new GameRunnable(player.getGame()) {
            @Override
            public void run() {
                if (secondCounter > 0) {
                    secondCounter--;
                }
            }
        }.runTaskTimer(0, 20);
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("Taking damage create a shield that absorbs damage based on " + SHIELD_PERCENT + "% of your max health. This shield lasts 10s or until broken and can only be triggered every ",
                                NamedTextColor.GRAY
                        )
                        .append(formatTitleUpgrade(COOLDOWN + COOLDOWN_PER_UPGRADE * getTitleLevel(), "s"))
                        .append(Component.text(". While the shield is active, EPS is increased by "))
                        .append(formatTitleUpgrade(EPS_BOOST + EPS_BOOST_PER_UPGRADE * getTitleLevel()))
                        .append(Component.text("."));
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.FULCRUM;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 160;
    }

    @Override
    protected float getHealthBonusValue() {
        return 800;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 5;
    }

    @Override
    protected float getEnergyPerHitBonusValue() {
        return 3;
    }
    
    @Override
    protected float getSkillCritChanceBonusValue() {
        return 5;
    }

    @Override
    protected float getSkillCritMultiplierBonusValue() {
        return 5;
    }
    
    @Override
    protected float getMeleeDamageMaxValue() {
        return 185;
    }

    @Override
    protected float getCritChanceValue() {
        return 20;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 190;
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(new Pair<>(
                        formatTitleUpgrade(COOLDOWN + COOLDOWN_PER_UPGRADE * getTitleLevel()),
                        formatTitleUpgrade(COOLDOWN + COOLDOWN_PER_UPGRADE * getTitleLevelUpgraded())
                ),
                new Pair<>(
                        formatTitleUpgrade(EPS_BOOST + EPS_BOOST_PER_UPGRADE * getTitleLevel()),
                        formatTitleUpgrade(EPS_BOOST + EPS_BOOST_PER_UPGRADE * getTitleLevelUpgraded())
                )
        );
    }

    @Override
    public int getCounter() {
        return secondCounter;
    }
}
