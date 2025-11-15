package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
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

public class LegendaryValiant extends AbstractLegendaryWeapon implements EventTitle {

    public static final int HP_CHECK = 70;
    public static final float HP_CHECK_INCREASE_PER_UPGRADE = 2.5f;
    public static final int EPS_INCREASE = 50;
    public static final int EPS_INCREASE_PER_UPGRADE = 5;

    public LegendaryValiant() {
    }

    public LegendaryValiant(UUID uuid) {
        super(uuid);
    }

    public LegendaryValiant(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("While you are at max health", NamedTextColor.GRAY)
                        .append(Component.text(", your EPS and EPH are increased by "))
                        .append(formatTitleUpgrade(EPS_INCREASE + EPS_INCREASE_PER_UPGRADE * getTitleLevel(), "%"))
                        .append(Component.text("."));
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.VALIANT;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 160;
    }

    @Override
    protected float getHealthBonusValue() {
        return 500;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 5;
    }

    @Override
    protected float getSkillCritChanceBonusValue() {
        return 5;
    }

    @Override
    protected float getSkillCritMultiplierBonusValue() {
        return 10;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);

        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Valiant",
                null,
                LegendaryValiant.class,
                null,
                player,
                CooldownTypes.WEAPON,
                cooldownManager -> {

                },
                false
        ).addModifier(Modifier.ENERGY_GAIN_PER_TICK, energyGainPerTick -> {
                    if (player.getCurrentHealth() == player.getMaxHealth()) {
                        energyGainPerTick.addMultiplicativeModifierMult(getTitleName(), (1 + (EPS_INCREASE + EPS_INCREASE_PER_UPGRADE * getTitleLevel()) / 100f));
                    }
                }
        ).addModifier(Modifier.ENERGY_GAIN_PER_HIT, energyGainPerHit -> {
                    if (player.getCurrentHealth() == player.getMaxHealth()) {
                        energyGainPerHit.addMultiplicativeModifierMult(getTitleName(), (1 + (EPS_INCREASE + EPS_INCREASE_PER_UPGRADE * getTitleLevel()) / 100f));
                    }
                }
        ));
    }

    @Override
    public LinkedHashMap<Currencies, Long> getCost() {
        LinkedHashMap<Currencies, Long> baseCost = super.getCost();
        baseCost.put(Currencies.TITLE_TOKEN_PHARAOHS_REVENGE, 1L);
        return baseCost;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 180;
    }

    @Override
    protected float getCritChanceValue() {
        return 25;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 180;
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(
                new Pair<>(
                        formatTitleUpgrade(EPS_INCREASE + EPS_INCREASE_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(EPS_INCREASE + EPS_INCREASE_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                )
        );
    }

}
