package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AvengersWrath extends AbstractAbility {

    private static final String WRATH_SKIP = "wrath_skip";

    public int extraPlayersStruck = 0;
    public int playersStruckDuringWrath = 0;
    public int playersKilledDuringWrath = 0;

    private int duration = 12;
    private float energyPerSecond = 20;
    private int maxTargets = 2;

    public AvengersWrath() {
        super("Avenger's Wrath", 0, 0, 52.85f, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Burst with incredible holy power, causing your Avenger's Strikes to " +
                "hit up to §e" + maxTargets + " §7additional enemies that are within §e5 §7blocks of your target. Your energy per second is increased by §e" +
                format(energyPerSecond) + " §7for the duration of the effect. Lasts §6" + duration + " §7seconds.";
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
        wp.getCooldownManager().addCooldown(new RegularCooldown<AvengersWrath>(
                name,
                "WRATH",
                AvengersWrath.class,
                tempAvengersWrath,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                duration * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 == 0) {
                        ParticleEffect.SPELL.display(
                                0.3F,
                                0.1F,
                                0.3F,
                                0.2F,
                                6,
                                wp.getLocation().add(0, 1.2, 0),
                                500
                        );
                    }
                })
        ) {
            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (event.getAbility().equals("Avenger's Strike") && !event.getFlags().contains(WRATH_SKIP)) {
                    tempAvengersWrath.addPlayersStruckDuringWrath();
                    for (WarlordsEntity wrathTarget : PlayerFilter
                            .entitiesAround(event.getPlayer(), 5, 4, 5)
                            .aliveEnemiesOf(wp)
                            .closestFirst(event.getPlayer())
                            .excluding(event.getPlayer())
                            .limit(maxTargets)
                    ) {
                        wp.doOnStaticAbility(AvengersWrath.class, AvengersWrath::addExtraPlayersStruck);
                        tempAvengersWrath.addPlayersStruckDuringWrath();

                        Optional<Consecrate> standingOnConsecrate = AbstractStrikeBase.getStandingOnConsecrate(wp, wrathTarget);
                        if (standingOnConsecrate.isPresent()) {
                            wp.doOnStaticAbility(Consecrate.class, Consecrate::addStrikesBoosted);
                            wrathTarget.addDamageInstance(
                                    wp,
                                    "Avenger's Strike",
                                    event.getMin() * (1 + standingOnConsecrate.get().getStrikeDamageBoost() / 100f),
                                    event.getMax() * (1 + standingOnConsecrate.get().getStrikeDamageBoost() / 100f),
                                    event.getCritChance(),
                                    event.getCritMultiplier(),
                                    false,
                                    Collections.singletonList(WRATH_SKIP)
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
                                    Collections.singletonList(WRATH_SKIP)
                            );
                        }
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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
}
