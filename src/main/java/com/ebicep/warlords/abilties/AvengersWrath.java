package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.abilties.internal.Duration;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsStrikeEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.*;

public class AvengersWrath extends AbstractAbility implements Duration {

    public int extraPlayersStruck = 0;
    public int playersStruckDuringWrath = 0;
    public int playersKilledDuringWrath = 0;

    private int tickDuration = 240;
    private float energyPerSecond = 20;
    private int maxTargets = 2;
    private int hitRadius = 5;

    public AvengersWrath() {
        super("Avenger's Wrath", 0, 0, 52.85f, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Burst with incredible holy power, causing your Avenger's Strikes to " +
                "hit up to §e" + maxTargets + " §7additional enemies that are within §e5 §7blocks of your target. Your energy per second is increased by §e" +
                format(energyPerSecond) + " §7for the duration of the effect. Lasts §6" + format(tickDuration / 20f) + " §7seconds.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Extra Players Struck", "" + extraPlayersStruck));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        Utils.playGlobalSound(wp.getLocation(), "paladin.avengerswrath.activation", 2, 1);

        AvengersWrath tempAvengersWrath = new AvengersWrath();
        wp.getCooldownManager().removeCooldown(AvengersWrath.class, false);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "WRATH",
                AvengersWrath.class,
                tempAvengersWrath,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 == 0) {
                        wp.getLocation().getWorld().spawnParticle(
                                Particle.SPELL,
                                wp.getLocation().add(0, 1.2, 0),
                                6,
                                0.3F,
                                0.1F,
                                0.3F,
                                0.2F,
                                null,
                                true
                        );
                    }
                })
        ) {
            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (event.getAbility().equals("Avenger's Strike") && !event.getFlags().contains(InstanceFlags.AVENGER_WRATH_STRIKE)) {
                    tempAvengersWrath.addPlayersStruckDuringWrath();
                    for (WarlordsEntity wrathTarget : PlayerFilter
                            .entitiesAround(event.getWarlordsEntity(), hitRadius, hitRadius, hitRadius)
                            .aliveEnemiesOf(wp)
                            .closestFirst(event.getWarlordsEntity())
                            .excluding(event.getWarlordsEntity())
                            .limit(maxTargets)
                    ) {
                        wp.doOnStaticAbility(AvengersWrath.class, AvengersWrath::addExtraPlayersStruck);
                        tempAvengersWrath.addPlayersStruckDuringWrath();

                        Optional<Consecrate> standingOnConsecrate = AbstractStrikeBase.getStandingOnConsecrate(wp, wrathTarget);
                        if (standingOnConsecrate.isPresent() && !event.getFlags().contains(InstanceFlags.STRIKE_IN_CONS)) {
                            wp.doOnStaticAbility(Consecrate.class, Consecrate::addStrikesBoosted);
                            wrathTarget.addDamageInstance(
                                    wp,
                                    "Avenger's Strike",
                                    event.getMin() * (1 + standingOnConsecrate.get().getStrikeDamageBoost() / 100f),
                                    event.getMax() * (1 + standingOnConsecrate.get().getStrikeDamageBoost() / 100f),
                                    event.getCritChance(),
                                    event.getCritMultiplier(),
                                    false,
                                    EnumSet.of(InstanceFlags.AVENGER_WRATH_STRIKE)
                            );
                        } else {
                            wrathTarget.addDamageInstance(
                                    wp,
                                    "Avenger's Strike",
                                    event.getMin(),
                                    event.getMax(),
                                    event.getCritChance(),
                                    event.getCritMultiplier(),
                                    false,
                                    EnumSet.of(InstanceFlags.AVENGER_WRATH_STRIKE)
                            );
                        }
                        Bukkit.getPluginManager().callEvent(new WarlordsStrikeEvent(wp, AvengersWrath.this, wrathTarget));
                        wrathTarget.subtractEnergy(10, true);
                    }
                }
            }

            @Override
            public void onDeathFromEnemies(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit, boolean isKiller) {
                if (isKiller) {
                    tempAvengersWrath.addPlayersKilledDuringWrath();
                }
            }

            @Override
            public float addEnergyGainPerTick(float energyGainPerTick) {
                return energyGainPerTick + energyPerSecond / 20f;
            }
        });

        return true;
    }

    public void addPlayersStruckDuringWrath() {
        playersStruckDuringWrath++;
    }

    public void addExtraPlayersStruck() {
        extraPlayersStruck++;
    }

    public void addPlayersKilledDuringWrath() {
        playersKilledDuringWrath++;
    }

    public int getPlayersStruckDuringWrath() {
        return playersStruckDuringWrath;
    }

    public int getPlayersKilledDuringWrath() {
        return playersKilledDuringWrath;
    }

    public float getEnergyPerSecond() {
        return energyPerSecond;
    }

    public void setEnergyPerSecond(float energyPerSecond) {
        this.energyPerSecond = energyPerSecond;
    }


    public int getMaxTargets() {
        return maxTargets;
    }

    public void setMaxTargets(int maxTargets) {
        this.maxTargets = maxTargets;
    }

    public int getHitRadius() {
        return hitRadius;
    }

    public void setHitRadius(int hitRadius) {
        this.hitRadius = hitRadius;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }
}
