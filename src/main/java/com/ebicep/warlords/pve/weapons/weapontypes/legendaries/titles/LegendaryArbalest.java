package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class LegendaryArbalest extends AbstractLegendaryWeapon {

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
    protected float getMeleeDamageMaxValue() {
        return 170;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);

        player.getGame().registerEvents(new Listener() {
            @EventHandler
            public void onEvent(WarlordsDamageHealingEvent event) {
                if (event.isDamageInstance() && event.getAttacker().equals(player)) {
                    float playerHPCheck = player.getHealth() * ((LESS_THAN_HP_CHECK + LESS_THAN_HP_CHECK_PER_UPGRADE * getTitleLevel()) / 100f + 1);
                    if (event.getPlayer().getHealth() < playerHPCheck) {
                        float damageBoost = 1 + (DAMAGE_BOOST + DAMAGE_BOOST_PER_UPGRADE * getTitleLevel()) / 100f;
                        event.setMin(event.getMin() * damageBoost);
                        event.setMax(event.getMax() * damageBoost);
                    }
                }
            }
        });
    }

    @Override
    public String getPassiveEffect() {
        return "Deal " + formatTitleUpgrade(DAMAGE_BOOST + DAMAGE_BOOST_PER_UPGRADE * getTitleLevel(), "%") + " more damage to enemies that have less than " +
                formatTitleUpgrade(LESS_THAN_HP_CHECK + LESS_THAN_HP_CHECK_PER_UPGRADE * getTitleLevel(), "%") + " of your current HP.";
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.BENEVOLENT;
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
    protected float getCritChanceValue() {
        return 25;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 170;
    }

    @Override
    public List<Pair<String, String>> getPassiveEffectUpgrade() {
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
