package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class LegendaryRevolt extends AbstractLegendaryWeapon implements LibraryArchivesTitle {

    private static final int RADIUS = 10;
    private static final int RADIUS_PER_UPGRADE = 1;
    private static final int RESISTANCE_SHRED = 3;
    private static final float RESISTANCE_SHRED_PER_UPGRADE = .5f;

    public LegendaryRevolt() {
    }

    public LegendaryRevolt(UUID uuid) {
        super(uuid);
    }

    public LegendaryRevolt(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public LinkedHashMap<Currencies, Long> getCost() {
        LinkedHashMap<Currencies, Long> baseCost = super.getCost();
        baseCost.put(Currencies.TITLE_TOKEN_LIBRARY_ARCHIVES, 1L);
        return baseCost;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);

        int radius = RADIUS + RADIUS_PER_UPGRADE * getTitleLevel();
        float resistanceShred = RESISTANCE_SHRED + RESISTANCE_SHRED_PER_UPGRADE * getTitleLevel();

        new GameRunnable(pveOption.getGame()) {
            @Override
            public void run() {
                boolean hasShield = player.getCooldownManager().hasCooldownExtends(Shield.class);
                if (!hasShield) {
                    return;
                }
                PlayerFilter.entitiesAround(player, radius, radius, radius)
                            .aliveEnemiesOf(player)
                            .forEach(enemy -> enemy.setDamageResistance(enemy.getSpec().getDamageResistance() - resistanceShred));
            }
        }.runTaskTimer(0, 20);
    }

    @Override
    public TextComponent getPassiveEffect() {
        return ComponentBuilder.create("While protected by a shield, enemies within a ")
                               .append(formatTitleUpgrade(RADIUS + RADIUS_PER_UPGRADE * getTitleLevel()))
                               .text(" block radius around you have ")
                               .append(formatTitleUpgrade(RESISTANCE_SHRED + RESISTANCE_SHRED_PER_UPGRADE * getTitleLevel(), "%"))
                               .text(" of their resistance shredded every second.")
                               .build();
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.REVOLT;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 185;
    }

    @Override
    protected float getHealthBonusValue() {
        return 400;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 7;
    }

    @Override
    protected float getEnergyPerSecondBonusValue() {
        return 3;
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
    protected float getMeleeDamageMaxValue() {
        return 200;
    }

    @Override
    protected float getCritChanceValue() {
        return 25;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 175;
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(new Pair<>(
                        formatTitleUpgrade(RADIUS + RADIUS_PER_UPGRADE * getTitleLevel()),
                        formatTitleUpgrade(RADIUS + RADIUS_PER_UPGRADE * getTitleLevelUpgraded())
                ),
                new Pair<>(
                        formatTitleUpgrade(RESISTANCE_SHRED + RESISTANCE_SHRED_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(RESISTANCE_SHRED + RESISTANCE_SHRED_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                )
        );
    }
}
