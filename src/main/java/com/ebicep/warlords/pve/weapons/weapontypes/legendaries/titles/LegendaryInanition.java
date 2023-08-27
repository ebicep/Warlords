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

public class LegendaryInanition extends AbstractLegendaryWeapon implements EventTitle {

    private static final int DEBUFF_DAMAGE_BOOST = 10;
    private static final float DEBUFF_DAMAGE_BOOST_PER_UPGRADE = 2.5f;
    private static final int CAP_DEBUFF_DAMAGE_BOOST = 40;
    private static final int CAP_DEBUFF_DAMAGE_BOOST_PER_UPGRADE = 10;

    public LegendaryInanition() {
    }

    public LegendaryInanition(UUID uuid) {
        super(uuid);
    }

    public LegendaryInanition(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);

        float debuffDamageBoost = (DEBUFF_DAMAGE_BOOST + DEBUFF_DAMAGE_BOOST_PER_UPGRADE * getTitleLevel()) / 100f;
        float maxDebuffDamageBoost = (CAP_DEBUFF_DAMAGE_BOOST + CAP_DEBUFF_DAMAGE_BOOST_PER_UPGRADE * getTitleLevel()) / 100f;
        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Inanition",
                null,
                LegendaryInanition.class,
                null,
                player,
                CooldownTypes.WEAPON,
                cooldownManager -> {
                },
                false
        ) {

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                int debuffs = event.getWarlordsEntity().getCooldownManager().getDebuffCooldowns(true).size();
                float damageBoost = 1 + Math.min(debuffs * debuffDamageBoost, maxDebuffDamageBoost);
                return currentDamageValue * damageBoost;
            }
        });
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("Each debuff on mobs increases your damage to them by ", NamedTextColor.GRAY)
                        .append(formatTitleUpgrade(DEBUFF_DAMAGE_BOOST + DEBUFF_DAMAGE_BOOST_PER_UPGRADE * getTitleLevel(), "%"))
                        .append(Component.text(". Max ", NamedTextColor.GRAY))
                        .append(formatTitleUpgrade(CAP_DEBUFF_DAMAGE_BOOST + CAP_DEBUFF_DAMAGE_BOOST_PER_UPGRADE * getTitleLevel(), "%"))
                        .append(Component.text("."));
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.INANITION;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 150;
    }

    @Override
    protected float getHealthBonusValue() {
        return 500;
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
        return 180;
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
                        formatTitleUpgrade(DEBUFF_DAMAGE_BOOST + DEBUFF_DAMAGE_BOOST_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(DEBUFF_DAMAGE_BOOST + DEBUFF_DAMAGE_BOOST_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(CAP_DEBUFF_DAMAGE_BOOST + CAP_DEBUFF_DAMAGE_BOOST_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(CAP_DEBUFF_DAMAGE_BOOST + CAP_DEBUFF_DAMAGE_BOOST_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                )
        );
    }

    @Override
    public LinkedHashMap<Currencies, Long> getCost() {
        LinkedHashMap<Currencies, Long> baseCost = super.getCost();
        baseCost.put(Currencies.TITLE_TOKEN_BANE_OF_IMPURITIES, 1L);
        return baseCost;
    }


}
