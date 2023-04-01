package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsAbilityTargetEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class LegendaryRevered extends AbstractLegendaryWeapon {

    public static final int DAMAGE_INCREASE = 10;
    public static final float DAMAGE_INCREASE_PER_UPGRADE = 2.5f;
    public static final int DURATION = 5;
    public static final float DURATION_INCREASE_PER_UPGRADE = .25f;
    private static final String COOLDOWN_NAME = "Revered";
    private static final List<String> EFFECTED_ABILITIES = Arrays.asList("Chain Heal", "Remedic Chains", "Intervene", "Heart To Heart");

    public LegendaryRevered() {
    }

    public LegendaryRevered(UUID uuid) {
        super(uuid);
    }

    public LegendaryRevered(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 200;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);

        player.getGame().registerEvents(new Listener() {
            @EventHandler
            public void onBlueAbilityTarget(WarlordsAbilityTargetEvent event) {
                if (!event.getPlayer().equals(player)) {
                    return;
                }
                if (!EFFECTED_ABILITIES.contains(event.getAbilityName())) {
                    return;
                }
                resetSelfCooldown(player);
                for (WarlordsEntity target : event.getTargets()) {
                    resetTeammateCooldown(player, target);
                }
            }
        });
    }

    @Override
    public String getPassiveEffect() {
        return "If the equipping player is connected to another player by means of Chain Heal, Heart To Heart, Intervene, Remedic Chains, both players will have their damage increased by " +
                formatTitleUpgrade(DAMAGE_INCREASE + DAMAGE_INCREASE_PER_UPGRADE * getTitleLevel(), "%") + " for " +
                formatTitleUpgrade(DURATION + DURATION_INCREASE_PER_UPGRADE * getTitleLevel(), "s") + ".";
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.REVERED;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 180;
    }

    @Override
    protected float getHealthBonusValue() {
        return 500;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 8;
    }

    @Override
    protected float getEnergyPerSecondBonusValue() {
        return 3;
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
    protected float getCritChanceValue() {
        return 20;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 175;
    }

    @Override
    public List<Pair<String, String>> getPassiveEffectUpgrade() {
        return Arrays.asList(
                new Pair<>(
                        formatTitleUpgrade(DAMAGE_INCREASE + DAMAGE_INCREASE_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(DAMAGE_INCREASE + DAMAGE_INCREASE_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(DURATION + DURATION_INCREASE_PER_UPGRADE * getTitleLevel(), "s"),
                        formatTitleUpgrade(DURATION + DURATION_INCREASE_PER_UPGRADE * getTitleLevelUpgraded(), "s")
                )
        );
    }

    private void resetSelfCooldown(WarlordsPlayer player) {
        player.getCooldownManager().removeCooldownByName(COOLDOWN_NAME);
        player.getCooldownManager().addCooldown(generateCooldown(player));
    }

    private void resetTeammateCooldown(WarlordsPlayer player, WarlordsEntity teammate) {
        teammate.getCooldownManager().removeCooldownByName(COOLDOWN_NAME);
        teammate.getCooldownManager().addCooldown(generateCooldown(player));
    }

    private RegularCooldown<LegendaryRevered> generateCooldown(WarlordsPlayer from) {
        return new RegularCooldown<>(
                COOLDOWN_NAME,
                "REV",
                LegendaryRevered.class,
                null,
                from,
                CooldownTypes.WEAPON,
                cooldownManager -> {

                },
                (int) ((DURATION + DURATION_INCREASE_PER_UPGRADE * getTitleLevel()) * 20)
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * (1 + (DAMAGE_INCREASE + DAMAGE_INCREASE_PER_UPGRADE * getTitleLevel()) / 100f);
            }
        };
    }

    @Override
    public LinkedHashMap<Currencies, Long> getCost() {
        LinkedHashMap<Currencies, Long> baseCost = super.getCost();
        baseCost.put(Currencies.TITLE_TOKEN_PHARAOHS_REVENGE, 1L);
        return baseCost;
    }
}
