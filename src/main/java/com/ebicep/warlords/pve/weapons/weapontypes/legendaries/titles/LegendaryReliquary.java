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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class LegendaryReliquary extends AbstractLegendaryWeapon {

    public static final int INCOMING_DAMAGE_INCREASE = 15;
    public static final int INCOMING_DAMAGE_INCREASE_PER_UPGRADE = 1;
    public static final int OUTGOING_DAMAGE_INCREASE = 30;
    public static final float OUTGOING_DAMAGE_INCREASE_PER_UPGRADE = 2.5f;


    public LegendaryReliquary() {
    }

    public LegendaryReliquary(UUID uuid) {
        super(uuid);
    }

    public LegendaryReliquary(AbstractLegendaryWeapon legendaryWeapon) {
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

        float incomingDamageIncrease = 1 + (INCOMING_DAMAGE_INCREASE + INCOMING_DAMAGE_INCREASE_PER_UPGRADE * getTitleLevel()) / 100f;
        float outgoingDamageIncrease = 1 + (OUTGOING_DAMAGE_INCREASE + OUTGOING_DAMAGE_INCREASE_PER_UPGRADE * getTitleLevel()) / 100f;
        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Reliquary",
                null,
                LegendaryReliquary.class,
                null,
                player,
                CooldownTypes.WEAPON,
                cooldownManager -> {
                },
                false
        ) {

            @Override
            public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * incomingDamageIncrease;
            }

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * outgoingDamageIncrease;
            }
        });
    }

    @Override
    public String getPassiveEffect() {
        return "Incoming damage increased by " +
                formatTitleUpgrade(INCOMING_DAMAGE_INCREASE + INCOMING_DAMAGE_INCREASE_PER_UPGRADE * getTitleLevel(), "%") + ".\n" +
                "Outgoing damage increased by " +
                formatTitleUpgrade(OUTGOING_DAMAGE_INCREASE + OUTGOING_DAMAGE_INCREASE_PER_UPGRADE * getTitleLevel(), "%") + ".";
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.RELIQUARY;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 170;
    }

    @Override
    protected float getHealthBonusValue() {
        return 700;
    }

    @Override
    protected float getEnergyPerSecondBonusValue() {
        return -3;
    }

    @Override
    protected float getEnergyPerHitBonusValue() {
        return 5;
    }

    @Override
    protected float getSkillCritChanceBonusValue() {
        return 5;
    }

    @Override
    protected float getSkillCritMultiplierBonusValue() {
        return 15;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 190;
    }

    @Override
    protected float getCritChanceValue() {
        return 25;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 185;
    }

    @Override
    public List<Pair<String, String>> getPassiveEffectUpgrade() {
        return Arrays.asList(
                new Pair<>(
                        formatTitleUpgrade(INCOMING_DAMAGE_INCREASE + INCOMING_DAMAGE_INCREASE_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(INCOMING_DAMAGE_INCREASE + INCOMING_DAMAGE_INCREASE_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(OUTGOING_DAMAGE_INCREASE + OUTGOING_DAMAGE_INCREASE_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(OUTGOING_DAMAGE_INCREASE + OUTGOING_DAMAGE_INCREASE_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                )
        );
    }
}

