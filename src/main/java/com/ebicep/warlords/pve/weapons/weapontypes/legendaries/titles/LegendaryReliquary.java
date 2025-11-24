package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class LegendaryReliquary extends AbstractLegendaryWeapon implements EventTitle {

    public static final int INCOMING_DAMAGE_INCREASE = 50;
    //    public static final int INCOMING_DAMAGE_INCREASE_PER_UPGRADE = 1;
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
    public TextComponent getPassiveEffect() {
        return Component.text("Incoming damage increased by " + INCOMING_DAMAGE_INCREASE + "%.", NamedTextColor.GRAY)
                        .append(Component.newline())
                        .append(Component.text("Outgoing damage increased by "))
                        .append(formatTitleUpgrade(OUTGOING_DAMAGE_INCREASE + OUTGOING_DAMAGE_INCREASE_PER_UPGRADE * getTitleLevel(), "%"))
                        .append(Component.text("."));
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
        return 900;
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
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);

        float incomingDamageIncrease = 1 + INCOMING_DAMAGE_INCREASE / 100f;
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
        ).addModifier(Modifier.OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, getTitleName(), outgoingDamageIncrease);
                }
        ).addModifier(Modifier.INCOMING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, getTitleName(), incomingDamageIncrease);
                }
        ));
    }

    @Override
    public LinkedHashMap<Currencies, Long> getCost() {
        LinkedHashMap<Currencies, Long> baseCost = super.getCost();
        baseCost.put(Currencies.TITLE_TOKEN_SPIDERS_BURROW, 1L);
        return baseCost;
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
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return List.of(
                new Pair<>(
                        formatTitleUpgrade(OUTGOING_DAMAGE_INCREASE + OUTGOING_DAMAGE_INCREASE_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(OUTGOING_DAMAGE_INCREASE + OUTGOING_DAMAGE_INCREASE_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                )
        );
    }

}

