package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class LegendaryIncendiary extends AbstractLegendaryWeapon implements EventTitle {

    public static final int CRIT_CHANCE_BOOST = 15;
    public static final float CRIT_CHANCE_BOOST_INCREASE_PER_UPGRADE = 0.5f;
    public static final int EPH_PERCENT_INCREASE = 20;
    public static final float EPH_PERCENT_INCREASE_PER_UPGRADE = 2.5f;


    public LegendaryIncendiary() {
    }

    public LegendaryIncendiary(UUID uuid) {
        super(uuid);
    }

    public LegendaryIncendiary(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public LinkedHashMap<Currencies, Long> getCost() {
        LinkedHashMap<Currencies, Long> baseCost = super.getCost();
        baseCost.put(Currencies.TITLE_TOKEN_SPIDERS_BURROW, 1L);
        return baseCost;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);

        float critChanceBoost = CRIT_CHANCE_BOOST + CRIT_CHANCE_BOOST_INCREASE_PER_UPGRADE * getTitleLevel();
        AbstractPlayerClass playerSpec = player.getSpec();

        playerSpec.setEnergyPerHit(playerSpec.getEnergyPerHit() * (1 + (EPH_PERCENT_INCREASE + EPH_PERCENT_INCREASE_PER_UPGRADE * getTitleLevel()) / 100));
        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Incendiary",
                null,
                LegendaryIncendiary.class,
                null,
                player,
                CooldownTypes.WEAPON,
                cooldownManager -> {
                },
                false
        ) {

            @Override
            public float addCritChanceFromAttacker(WarlordsDamageHealingEvent event, float currentCritChance) {
                String ability = event.getCause();
                if (Utils.isProjectile(ability) || ability.equals("Boulder")) {
                    return currentCritChance + critChanceBoost;
                } else {
                    return currentCritChance;
                }
            }

        });
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("Increased ranged abilities Crit Chance by ", NamedTextColor.GRAY)
                        .append(formatTitleUpgrade(CRIT_CHANCE_BOOST + CRIT_CHANCE_BOOST_INCREASE_PER_UPGRADE * getTitleLevel(), "%"))
                        .append(Component.text(" and EPH increased by "))
                        .append(formatTitleUpgrade(EPH_PERCENT_INCREASE + EPH_PERCENT_INCREASE_PER_UPGRADE * getTitleLevel(), "%"))
                        .append(Component.text("."));
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.INCENDIARY;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 150;
    }

    @Override
    protected float getHealthBonusValue() {
        return 300;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 8;
    }

    @Override
    protected float getEnergyPerHitBonusValue() {
        return 3;
    }

    @Override
    protected float getSkillCritMultiplierBonusValue() {
        return 20;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 180;
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
                        formatTitleUpgrade(CRIT_CHANCE_BOOST + CRIT_CHANCE_BOOST_INCREASE_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(CRIT_CHANCE_BOOST + CRIT_CHANCE_BOOST_INCREASE_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(EPH_PERCENT_INCREASE + EPH_PERCENT_INCREASE_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(EPH_PERCENT_INCREASE + EPH_PERCENT_INCREASE_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                )
        );
    }
}

