package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractHolyRadiance;
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

public class HolyRadianceAvenger extends AbstractHolyRadiance {

    private final int markDuration = 8;
    private int markRadius = 15;
    private int energyDrainPerSecond = 8;

    protected int timesWrathReduced = 0;

    public HolyRadianceAvenger(float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost, float critChance, float critMultiplier) {
        super("Holy Radiance", minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, 6);
    }

    public HolyRadianceAvenger() {
        super("Holy Radiance", 582, 760, 16.53f, 20, 15, 175, 6);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Radiate with holy energy, healing yourself and all nearby allies for ")
                               .append(formatRangeHealing(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" health."))
                               .append(Component.newline())
                               .append(Component.newline())
                               .append(Component.text("You may look at an enemy to mark them for "))
                               .append(Component.text(markDuration, NamedTextColor.GOLD))
                               .append(Component.text(" seconds. Reducing their energy per second by "))
                               .append(Component.text(energyDrainPerSecond, NamedTextColor.YELLOW))
                               .append(Component.text(" for the duration."))
                               .append(Component.text("\n\nMark has an optimal range of "))
                               .append(Component.text(markRadius, NamedTextColor.YELLOW));
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

            HolyRadianceAvenger tempMark = new HolyRadianceAvenger(
                    minDamageHeal.getCalculatedValue(),
                    maxDamageHeal.getCalculatedValue(),
                    cooldown.getBaseValue(),
                    energyCost.getBaseValue(),
                    critChance,
                    critMultiplier
            );

            markTarget.getCooldownManager().removeCooldown(HolyRadianceAvenger.class, false);
            markTarget.getCooldownManager().addCooldown(new RegularCooldown<>(
                    name,
                    "AVE MARK",
                    HolyRadianceAvenger.class,
                    tempMark,
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
        HolyRadianceAvenger tempMark = new HolyRadianceAvenger(
                minDamageHeal.getCalculatedValue(),
                maxDamageHeal.getCalculatedValue(),
                cooldown.getBaseValue(),
                energyCost.getBaseValue(),
                critChance,
                critMultiplier
        );
        target.getCooldownManager().removeCooldown(HolyRadianceAvenger.class, false);
        target.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "AVE MARK",
                HolyRadianceAvenger.class,
                tempMark,
                giver,
                CooldownTypes.DEBUFF,
                cooldownManager -> {
                    if (pveMasterUpgrade2 && target.isDead() && tempMark.timesWrathReduced < 10) {
                        tempMark.timesWrathReduced++;
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

    public int getMarkRadius() {
        return markRadius;
    }

    public void setMarkRadius(int markRadius) {
        this.markRadius = markRadius;
    }

    public int getEnergyDrainPerSecond() {
        return energyDrainPerSecond;
    }

    public void setEnergyDrainPerSecond(int energyDrainPerSecond) {
        this.energyDrainPerSecond = energyDrainPerSecond;
    }


}
