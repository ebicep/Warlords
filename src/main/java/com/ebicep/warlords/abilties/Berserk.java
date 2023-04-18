package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.Duration;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Berserk extends AbstractAbility implements Duration {

    public int hitsDoneAmplified = 0;
    public int hitsTakenAmplified = 0;

    private int tickDuration = 360;
    private int speedBuff = 30;
    private float damageIncrease = 30;
    private float damageTakenIncrease = 10;

    public Berserk() {
        super("Berserk", 0, 0, 46.98f, 30);
    }

    @Override
    public void updateDescription(Player player) {
        description = "You go into a berserker rage, increasing your damage by §c" + format(damageIncrease) + "% §7and movement speed by §e" + speedBuff +
                "%§7. While active, you also take §c" + format(damageTakenIncrease) + "% §7more damage. Lasts §6" + format(tickDuration / 20f) + " §7seconds.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Hits Done Amplified", "" + hitsDoneAmplified));
        info.add(new Pair<>("Hits Taken Amplified", "" + hitsTakenAmplified));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(player.getLocation(), "warrior.berserk.activation", 2, 1);

        Runnable cancelSpeed = wp.addSpeedModifier(wp, name, speedBuff, tickDuration, "BASE");

        Berserk tempBerserk = new Berserk();
        wp.getCooldownManager().removeCooldown(Berserk.class, false);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "BERS",
                Berserk.class,
                tempBerserk,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    cancelSpeed.run();
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 3 == 0) {
                        wp.getLocation().getWorld().spawnParticle(
                                Particle.VILLAGER_ANGRY,
                                wp.getLocation().add(0, 1.2, 0),
                                1,
                                0,
                                0,
                                0,
                                0.1F,
                                null,
                                true
                        );
                    }
                })
        ) {
            int multiplier = 0;

            @Override
            public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                hitsTakenAmplified++;
                return currentDamageValue * (1 + damageTakenIncrease / 100);
            }

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                hitsDoneAmplified++;
                multiplier++;
                return currentDamageValue * (1 + damageIncrease / 100);
            }

            @Override
            public float addCritChanceFromAttacker(WarlordsDamageHealingEvent event, float currentCritChance) {
                if (pveUpgrade) {
                    if (event.getAbility().isEmpty() || event.getAbility().equals("Time Warp")) {
                        return currentCritChance;
                    }
                    float critBoost = (0.2f * multiplier);
                    if (critBoost > 50) {
                        critBoost = 50;
                    }
                    return currentCritChance + critBoost;
                }
                return currentCritChance;
            }

            @Override
            public float addCritMultiplierFromAttacker(WarlordsDamageHealingEvent event, float currentCritMultiplier) {
                if (pveUpgrade) {
                    if (event.getAbility().isEmpty() || event.getAbility().equals("Time Warp")) {
                        return currentCritMultiplier;
                    }
                    float critBoost = (0.2f * multiplier);
                    if (critBoost > 50) {
                        critBoost = 50;
                    }
                    return currentCritMultiplier + critBoost;
                }
                return currentCritMultiplier;
            }
        });

        return true;
    }

    public float getDamageIncrease() {
        return damageIncrease;
    }

    public void setDamageIncrease(float damageIncrease) {
        this.damageIncrease = damageIncrease;
    }

    public float getDamageTakenIncrease() {
        return damageTakenIncrease;
    }

    public void setDamageTakenIncrease(float damageTakenIncrease) {
        this.damageTakenIncrease = damageTakenIncrease;
    }

    public int getSpeedBuff() {
        return speedBuff;
    }

    public void setSpeedBuff(int speedBuff) {
        this.speedBuff = speedBuff;
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
