package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.data.annotation.Transient;

import java.util.*;

public class LegendaryChaotic extends AbstractLegendaryWeapon implements Listener, LibraryArchivesTitle {

    private static final int CRIT_CHANCE = 5;
    private static final float CRIT_CHANCE_PER_UPGRADE = 1;
    private static final int MAX_STACKS = 5;
    private static final float MAX_STACKS_PER_UPGRADE = 1;
    @Transient
    public List<String> abilityNames;
    @Transient
    private RegularCooldown<LegendaryChaotic> cooldown = null;
    @Transient
    private int stacks = 0;

    public LegendaryChaotic() {
    }

    public LegendaryChaotic(UUID uuid) {
        super(uuid);
    }

    public LegendaryChaotic(AbstractLegendaryWeapon legendaryWeapon) {
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
        cooldown = null;
        abilityNames = player.getAbilities().stream().map(AbstractAbility::getName).toList();
        stacks = 0;
    }

    @Override
    public TextComponent getPassiveEffect() {
        return ComponentBuilder.create("Upon damaging an enemy, all abilities gain a ")
                               .append(formatTitleUpgrade(CRIT_CHANCE + CRIT_CHANCE_PER_UPGRADE * getTitleLevel(), "%"))
                               .text(" crit chance and 10% crit multiplier. Maximum ")
                               .append(formatTitleUpgrade(MAX_STACKS + MAX_STACKS_PER_UPGRADE * getTitleLevel()))
                               .text(" stacks. Once an ability crit occurs, all stacks are removed.")
                               .build();
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.CHAOTIC;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 180;
    }

    @Override
    protected float getHealthBonusValue() {
        return 300;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 5;
    }

    @Override
    protected float getEnergyPerSecondBonusValue() {
        return 3;
    }

    @Override
    protected float getSkillCritMultiplierBonusValue() {
        return 25;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 200;
    }

    @Override
    protected float getCritChanceValue() {
        return 20;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 200;
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(new Pair<>(
                        formatTitleUpgrade(CRIT_CHANCE + CRIT_CHANCE_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(CRIT_CHANCE + CRIT_CHANCE_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(MAX_STACKS + MAX_STACKS_PER_UPGRADE * getTitleLevel()),
                        formatTitleUpgrade(MAX_STACKS + MAX_STACKS_PER_UPGRADE * getTitleLevelUpgraded())
                )
        );
    }

    @EventHandler
    public void onFinalDamageHeal(WarlordsDamageHealingFinalEvent event) {
        if (event.isHealingInstance()) {
            return;
        }
        if (!Objects.equals(event.getAttacker(), warlordsPlayer)) {
            return;
        }
        if (event.getInstanceFlags().contains(InstanceFlags.RECURSIVE)) {
            return;
        }
        if (event.isCrit()) {
            stacks = 0;
            return;
        }
        if (stacks < MAX_STACKS + MAX_STACKS_PER_UPGRADE * getTitleLevel()) {
            stacks++;
        }
        if (cooldown == null) {
            warlordsPlayer.getCooldownManager().addCooldown(cooldown = new RegularCooldown<>(
                    getTitleName() + " 1",
                    "CHAOTIC 1",
                    LegendaryChaotic.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.WEAPON,
                    cooldownManager -> {
                    },
                    cooldownManager -> {
                        cooldown = null;
                        stacks = 0;
                    },
                    5 * 20
            ) {
                @Override
                public float addCritChanceFromAttacker(WarlordsDamageHealingEvent event, float currentCritChance) {
                    if (!abilityNames.contains(event.getAbility())) {
                        return currentCritChance;
                    }
                    return currentCritChance + (CRIT_CHANCE + CRIT_CHANCE_PER_UPGRADE * getTitleLevel()) * stacks;
                }

                @Override
                public float addCritMultiplierFromAttacker(WarlordsDamageHealingEvent event, float currentCritMultiplier) {
                    if (!abilityNames.contains(event.getAbility())) {
                        return currentCritMultiplier;
                    }
                    return currentCritMultiplier + 10 * stacks;
                }
            });
        } else {
            cooldown.setTicksLeft(5 * 20);
            cooldown.setName(getTitleName() + " " + stacks);
            cooldown.setNameAbbreviation("CHAOTIC " + stacks);
        }
    }

}
