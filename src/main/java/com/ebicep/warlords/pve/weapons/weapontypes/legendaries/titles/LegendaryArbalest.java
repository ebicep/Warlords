package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class LegendaryArbalest extends AbstractLegendaryWeapon implements EventTitle {

    public static final int DAMAGE_BOOST = 30;
    public static final int DAMAGE_BOOST_PER_UPGRADE = 5;
    public static final int LESS_THAN_HP_CHECK = 200;
    public static final int LESS_THAN_HP_CHECK_PER_UPGRADE = 10;

    public LegendaryArbalest() {
    }

    public LegendaryArbalest(UUID uuid) {
        super(uuid);
    }

    public LegendaryArbalest(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public LinkedHashMap<Currencies, Long> getCost() {
        LinkedHashMap<Currencies, Long> baseCost = super.getCost();
        baseCost.put(Currencies.TITLE_TOKEN_PHARAOHS_REVENGE, 1L);
        return baseCost;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);

        float playerHPCheck = player.getMaxHealth() * ((LESS_THAN_HP_CHECK + LESS_THAN_HP_CHECK_PER_UPGRADE * getTitleLevel()) / 100f + 1);
        float damageBoost = 1 + (DAMAGE_BOOST + DAMAGE_BOOST_PER_UPGRADE * getTitleLevel()) / 100f;
        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Arbalest",
                null,
                LegendaryArbalest.class,
                null,
                player,
                CooldownTypes.WEAPON,
                cooldownManager -> {
                },
                false
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (event.getWarlordsEntity().getCurrentHealth() < playerHPCheck) {
                    return currentDamageValue * damageBoost;
                }
                return currentDamageValue;
            }
        });
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("Deal", NamedTextColor.GRAY)
                        .append(formatTitleUpgrade(DAMAGE_BOOST + DAMAGE_BOOST_PER_UPGRADE * getTitleLevel(), "%"))
                        .append(Component.text(" more damage to enemies whose current health is less than "))
                        .append(formatTitleUpgrade(LESS_THAN_HP_CHECK + LESS_THAN_HP_CHECK_PER_UPGRADE * getTitleLevel(), "%"))
                        .append(Component.text(" of your max health."));
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.ARBALEST;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 140;
    }

    @Override
    protected float getHealthBonusValue() {
        return 1000;
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
        return 5f;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 170;
    }

    @Override
    protected float getCritChanceValue() {
        return 25;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 170;
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(new Pair<>(
                        formatTitleUpgrade(DAMAGE_BOOST + DAMAGE_BOOST_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(DAMAGE_BOOST + DAMAGE_BOOST_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(LESS_THAN_HP_CHECK + LESS_THAN_HP_CHECK_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(LESS_THAN_HP_CHECK + LESS_THAN_HP_CHECK_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                )
        );
    }

}
