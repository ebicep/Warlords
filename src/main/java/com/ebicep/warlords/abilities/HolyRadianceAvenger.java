package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.avenger.HolyRadianceBranchAvenger;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class HolyRadianceAvenger extends AbstractHolyRadiance implements Heals<HolyRadianceAvenger.HealingValues> {

    public static final ItemStack ITEM_STACK = new ItemStack(Material.PITCHER_PLANT);
    private final HealingValues healingValues = new HealingValues();
    private int markDuration = 8;
    private int markRadius = 15;
    private float energyDrainPerSecond = 8;

    public HolyRadianceAvenger() {
        super(AbstractAbilityBuilder.create("holyRadianceAvenger").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.markDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("markDuration"), int.class);
        this.markRadius = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("markRadius"), int.class);
        this.energyDrainPerSecond = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("energyDrainPerSecond"), float.class);
    }

    @Override
    public Value.RangedValueCritable getRadianceHealing() {
        return healingValues.radianceHealing;
    }

    @Override
    public boolean chain(WarlordsEntity wp) {
        if (pveMasterUpgrade || pveMasterUpgrade2) {
            for (WarlordsEntity markTarget : PlayerFilter.entitiesAround(wp, 8, 8, 8).aliveEnemiesOf(wp)) {
                Utils.playGlobalSound(wp.getLocation(), "paladin.consecrate.activation", 2, 0.65f);
                EffectUtils.playParticleLinkAnimation(wp.getLocation(), markTarget.getLocation(), 255, 50, 0, 1);
                EffectUtils.playChainAnimation(wp, markTarget, ITEM_STACK, 8);
                aoeMark(wp, markTarget);
            }
            return true;
        }
        for (WarlordsEntity markTarget : PlayerFilter.entitiesAround(wp, markRadius, markRadius, markRadius).aliveEnemiesOf(wp).lookingAtFirst(wp).limit(1)) {
            if (!LocationUtils.isLookingAtMark(wp, markTarget) || !LocationUtils.hasLineOfSight(wp, markTarget)) {
                wp.sendMessage(Component.text("Your mark was out of range or you did not target a player!", NamedTextColor.RED));
                continue;
            }
            Utils.playGlobalSound(wp.getLocation(), "paladin.consecrate.activation", 2, 0.65f);
            EffectUtils.playParticleLinkAnimation(wp.getLocation(), markTarget.getLocation(), 255, 50, 0, 1);
            EffectUtils.playChainAnimation(wp, markTarget, ITEM_STACK, 8);
            mark(wp, markTarget);
            return true;
        }
        return false;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Radiate with holy energy, healing yourself and all nearby allies for ")
                .heal(healingValues.radianceHealing)
                .text(" health.")
                .emptyLine()
                .text("You may look at an enemy to inflict them with ")
                .text("MARK", NamedTextColor.DARK_RED)
                .text(" for ")
                .durationSeconds(markDuration)
                .text(", causing them to lose ")
                .energy(energyDrainPerSecond)
                .text(" per second.")
                .maxRange(markRadius)
                .build();
    }

    private void aoeMark(WarlordsEntity giver, WarlordsEntity target) {
        RadianceData radianceData = new RadianceData();
        target.getCooldownManager().removeCooldownByName("Strike Priority");
        AbstractStrike.giveStrikePriority(giver, target, markDuration * 20);
        target.getCooldownManager().removeCooldown(RadianceData.class, false);
        target.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Avenger's Mark",
                "AVE MARK",
                RadianceData.class,
                radianceData,
                giver,
                CooldownTypes.LOW_LEVEL_DEBUFF,
                cooldownManager -> {
                },
                markDuration * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 12 == 0) {
                        EffectUtils.playCylinderAnimation(target.getLocation(), 1, 250, 25, 25);
                    }
                })
        ) {

            @Override
            public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (pveMasterUpgrade && event.getCause().equals("Avenger's Strike")) {
                    return currentDamageValue * 1.4f;
                }
                if (pveMasterUpgrade2) {
                    return currentDamageValue * 1.2f;
                }
                return currentDamageValue;
            }

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (pveMasterUpgrade) {
                    return currentDamageValue * .9f;
                }
                return currentDamageValue;
            }
        });
    }

    private void mark(WarlordsEntity wp, WarlordsEntity markTarget) {
        wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN.append(Component.text(" Your ", NamedTextColor.GRAY))
                                                      .append(Component.text("Avenger's Mark", NamedTextColor.YELLOW))
                                                      .append(Component.text(" marked " + markTarget.getName() + "!", NamedTextColor.GRAY)));
        markTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED.append(Component.text(" You have been cursed with ", NamedTextColor.GRAY))
                                                               .append(Component.text("Avenger's Mark", NamedTextColor.YELLOW))
                                                               .append(Component.text(" by " + wp.getName() + "!", NamedTextColor.GRAY)));
        RadianceData radianceData = new RadianceData();
        markTarget.getCooldownManager().removeCooldown(RadianceData.class, false);
        markTarget.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Avenger's Mark",
                "AVE MARK",
                RadianceData.class,
                radianceData,
                wp,
                CooldownTypes.HIGH_LEVEL_DEBUFF,
                cooldownManager -> {
                },
                markDuration * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 12 == 0) {
                        EffectUtils.playCylinderAnimation(markTarget.getLocation(), 1, 250, 25, 25);
                    }
                })
        ) {

            @Override
            public float addEnergyGainPerTick(float energyGainPerTick) {
                return energyGainPerTick - energyDrainPerSecond / 20f;
            }
        });
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new HolyRadianceBranchAvenger(abilityTree, this);
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    public float getEnergyDrainPerSecond() {
        return energyDrainPerSecond;
    }

    public void setEnergyDrainPerSecond(float energyDrainPerSecond) {
        this.energyDrainPerSecond = energyDrainPerSecond;
    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.RangedValueCritable radianceHealing = new Value.RangedValueCritable(582, 760, 15, 175);

        private List<Value> values = List.of(radianceHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.radianceHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameHealing("radianceHealing"),
                    Value.RangedValueCritable.class
            );
            this.values = List.of(radianceHealing);
        }

    }

    public static class RadianceData {

        private final int timesWrathReduced = 0;

    }

}
