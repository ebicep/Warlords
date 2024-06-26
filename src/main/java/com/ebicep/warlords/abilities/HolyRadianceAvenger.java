package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractHolyRadiance;
import com.ebicep.warlords.abilities.internal.Heals;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.avenger.HolyRadianceBranchAvenger;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HolyRadianceAvenger extends AbstractHolyRadiance implements Heals<HolyRadianceAvenger.HealingValues> {

    private final int markDuration = 8;
    private final HealingValues healingValues = new HealingValues();
    private int markRadius = 15;
    private float energyDrainPerSecond = 8;

    public HolyRadianceAvenger() {
        super("Holy Radiance", 16.53f, 20, 6);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Radiate with holy energy, healing yourself and all nearby allies for ")
                               .append(Heals.formatHealing(healingValues.radianceHealing))
                               .append(Component.text(" health."))
                               .append(Component.newline())
                               .append(Component.newline())
                               .append(Component.text("You may look at an enemy to mark them for "))
                               .append(Component.text(markDuration, NamedTextColor.GOLD))
                               .append(Component.text(" seconds. Reducing their energy per second by "))
                               .append(Component.text(format(energyDrainPerSecond), NamedTextColor.YELLOW))
                               .append(Component.text(" for the duration."))
                               .append(Component.text("\n\nMark has a maximum range of "))
                               .append(Component.text(markRadius, NamedTextColor.YELLOW))
                               .append(Component.text(" blocks."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Healed", "" + playersHealed));
        info.add(new Pair<>("Players Marked", "" + playersMarked));

        return info;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new HolyRadianceBranchAvenger(abilityTree, this);
    }

    @Override
    public boolean chain(WarlordsEntity wp) {
        if (pveMasterUpgrade || pveMasterUpgrade2) {
            for (WarlordsEntity circleTarget : PlayerFilter
                    .entitiesAround(wp, 8, 8, 8)
                    .aliveEnemiesOf(wp)
            ) {
                emitMarkRadiance(wp, circleTarget);
            }

            return true;
        }

        for (WarlordsEntity markTarget : PlayerFilter
                .entitiesAround(wp, markRadius, markRadius, markRadius)
                .aliveEnemiesOf(wp)
                .lookingAtFirst(wp)
                .limit(1)
        ) {
            if (!LocationUtils.isLookingAtMark(wp, markTarget) || !LocationUtils.hasLineOfSight(wp, markTarget)) {
                wp.sendMessage(Component.text("Your mark was out of range or you did not target a player!", NamedTextColor.RED));
                continue;
            }
            Utils.playGlobalSound(wp.getLocation(), "paladin.consecrate.activation", 2, 0.65f);

            // chain particles
            EffectUtils.playParticleLinkAnimation(wp.getLocation(), markTarget.getLocation(), 255, 50, 0, 1);
            EffectUtils.playChainAnimation(wp, markTarget, new ItemStack(Material.BIRCH_LEAVES), 8);

            RadianceData radianceData = new RadianceData();
            markTarget.getCooldownManager().removeCooldown(RadianceData.class, false);
            markTarget.getCooldownManager().addCooldown(new RegularCooldown<>(
                    name,
                    "AVE MARK",
                    RadianceData.class,
                    radianceData,
                    wp,
                    CooldownTypes.DEBUFF,
                    cooldownManager -> {
                    },
                    markDuration * 20,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        if (ticksElapsed % 10 == 0) {
                            EffectUtils.playCylinderAnimation(markTarget.getLocation(), 1, 250, 25, 25);
                        }
                    })
            ) {
                @Override
                public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    if (pveMasterUpgrade) {
                        if (event.getCause().equals("Avenger's Strike")) {
                            return currentDamageValue * 1.4f;
                        }
                        return currentDamageValue;
                    }
                    return currentDamageValue;
                }

                @Override
                public float addEnergyGainPerTick(float energyGainPerTick) {
                    return energyGainPerTick - energyDrainPerSecond / 20f;
                }
            });

            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" Your ", NamedTextColor.GRAY))
                    .append(Component.text("Avenger's Mark", NamedTextColor.YELLOW))
                    .append(Component.text(" marked " + markTarget.getName() + "!", NamedTextColor.GRAY))
            );

            markTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED
                    .append(Component.text(" You have been cursed with ", NamedTextColor.GRAY))
                    .append(Component.text("Avenger's Mark", NamedTextColor.YELLOW))
                    .append(Component.text(" by " + wp.getName() + "!", NamedTextColor.GRAY))
            );

            return true;
        }
        return false;
    }

    private void emitMarkRadiance(WarlordsEntity giver, WarlordsEntity target) {
        RadianceData radianceData = new RadianceData();
        target.getCooldownManager().removeCooldown(RadianceData.class, false);
        target.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "AVE MARK",
                RadianceData.class,
                radianceData,
                giver,
                CooldownTypes.DEBUFF,
                cooldownManager -> {
                    if (pveMasterUpgrade2 && target.isDead() && radianceData.timesWrathReduced < 10) {
                        radianceData.timesWrathReduced++;
                        giver.getAbilitiesMatching(AvengersWrath.class).forEach(avengersWrath -> avengersWrath.subtractCurrentCooldown(.5f));
                        playCooldownReductionEffect(target);
                    }
                },
                markDuration * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 10 == 0) {
                        EffectUtils.playCylinderAnimation(target.getLocation(), 1, 250, 25, 25);
                    }
                })
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (pveMasterUpgrade) {
                    if (event.getCause().equals("Avenger's Strike")) {
                        return currentDamageValue * 1.4f;
                    }
                    return currentDamageValue;
                }
                return currentDamageValue;
            }
        });
    }

    @Override
    public Value.RangedValueCritable getRadianceHealing() {
        return healingValues.radianceHealing;
    }

    public float getEnergyDrainPerSecond() {
        return energyDrainPerSecond;
    }

    public void setEnergyDrainPerSecond(float energyDrainPerSecond) {
        this.energyDrainPerSecond = energyDrainPerSecond;
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    public static class HealingValues implements Value.ValueHolder {

        private final Value.RangedValueCritable radianceHealing = new Value.RangedValueCritable(582, 760, 15, 175);
        private final List<Value> values = List.of(radianceHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

    public static class RadianceData {

        private int timesWrathReduced = 0;

    }


}
