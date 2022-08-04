package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AvengersWrath extends AbstractAbility {
    protected int extraPlayersStruck = 0;
    private boolean pveUpgrade = false;
    private int duration = 12;
    private int energyPerSecond = 20;
    private int maxTargets = 2;

    public AvengersWrath() {
        super("Avenger's Wrath", 0, 0, 52.85f, 0, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Burst with incredible holy power,\n" +
                "§7causing your Avenger's Strikes to\n" +
                "§7hit up to §e" + maxTargets + " §7additional enemies\n" +
                "§7that are within §e5 §7blocks of your\n" +
                "§7target. Your energy per second is\n" +
                "§7increased by §e" + energyPerSecond + " §7for the duration\n" +
                "§7of the effect. Lasts §6" + duration + " §7seconds.";
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
                (cooldown, ticksLeft, counter) -> {
                    if (counter % 4 == 0) {
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
                }
        ) {
            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (event.getAbility().equals("Avenger's Strike")) {
                    for (WarlordsEntity wrathTarget : PlayerFilter
                            .entitiesAround(event.getPlayer(), 5, 4, 5)
                            .aliveEnemiesOf(wp)
                            .closestFirst(event.getPlayer())
                            .excluding(event.getPlayer())
                            .limit(maxTargets)
                    ) {
                        if (pveUpgrade) {
                            wp.addEnergy(wp, name, 2);
                        }
                        wp.doOnStaticAbility(AvengersWrath.class, AvengersWrath::addExtraPlayersStruck);

                        Optional<Consecrate> standingOnConsecrate = AbstractStrikeBase.getStandingOnConsecrate(wp, wrathTarget);
                        if (standingOnConsecrate.isPresent()) {
                            wp.doOnStaticAbility(Consecrate.class, Consecrate::addStrikesBoosted);
                            wrathTarget.addDamageInstance(
                                    wp,
                                    "Avenger's Strìke",
                                    event.getMin() * (1 + standingOnConsecrate.get().getStrikeDamageBoost() / 100f),
                                    event.getMax() * (1 + standingOnConsecrate.get().getStrikeDamageBoost() / 100f),
                                    event.getCritChance(),
                                    event.getCritMultiplier(),
                                    false
                            );
                        } else {
                            wrathTarget.addDamageInstance(
                                    wp,
                                    "Avenger's Strìke",
                                    event.getMin(),
                                    event.getMax(),
                                    event.getCritChance(),
                                    event.getCritMultiplier(),
                                    false
                            );
                        }
                        wrathTarget.subtractEnergy(10, true);
                    }
                }
            }

            @Override
            public float addEnergyGainPerTick(float energyGainPerTick) {
                return energyGainPerTick + energyPerSecond / 20f;
            }
        });

        return true;
    }

    public void addExtraPlayersStruck() {
        extraPlayersStruck++;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getEnergyPerSecond() {
        return energyPerSecond;
    }

    public void setEnergyPerSecond(int energyPerSecond) {
        this.energyPerSecond = energyPerSecond;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }

    public int getMaxTargets() {
        return maxTargets;
    }

    public void setMaxTargets(int maxTargets) {
        this.maxTargets = maxTargets;
    }
}
