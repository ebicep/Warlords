package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DrainingMiasma extends AbstractAbility {

    protected int numberOfLeechProcd = 0;

    public int playersHit = 0;

    private final int maxHealthDamage = 4;
    private int duration = 5;
    private int leechDuration = 5;
    private int enemyHitRadius = 8;

    public DrainingMiasma() {
        super("Draining Miasma", 0, 0, 50, 40, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Summon a toxin-filled cloud around you, poisoning all enemies inside the area. Poisoned enemies take §c50 §7+ §c" + maxHealthDamage +
                "% §7of their max health as damage per second, for §6" + duration + " §7seconds. " +
                "Enemies poisoned by your Draining Miasma are slowed by §e25% §7for §63 §7seconds on cast." +
                "\n\nEach enemy hit will be afflicted with §aLEECH §7for §6" + leechDuration + " §7seconds.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Hit", "" + playersHit));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);

        Utils.playGlobalSound(player.getLocation(), "rogue.drainingmiasma.activation", 2, 1.7f);
        Utils.playGlobalSound(player.getLocation(), "shaman.earthlivingweapon.activation", 2, 0.65f);

        EffectUtils.playSphereAnimation(player, 6, ParticleEffect.SLIME, 1);

        FireWorkEffectPlayer.playFirework(wp.getLocation(), FireworkEffect.builder()
                .withColor(Color.LIME)
                .with(FireworkEffect.Type.BALL_LARGE)
                .build());

        int hitCounter = 0;
        DrainingMiasma tempDrainingMiasma = new DrainingMiasma();
        for (WarlordsEntity miasmaTarget : PlayerFilter
                .entitiesAround(wp, getEnemyHitRadius(), getEnemyHitRadius(), getEnemyHitRadius())
                .aliveEnemiesOf(wp)
        ) {
            hitCounter++;
            Runnable cancelSlowness = miasmaTarget.getSpeed().addSpeedModifier("Draining Miasma Slow", -25, 3 * 20, "BASE");
            miasmaTarget.getCooldownManager().addRegularCooldown(
                    name,
                    "MIAS",
                    DrainingMiasma.class,
                    tempDrainingMiasma,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                        cancelSlowness.run();
                        if (tempDrainingMiasma.numberOfLeechProcd >= 150) {
                            wp.unlockAchievement(ChallengeAchievements.LIFELEECHER);
                        }
                    },
                    duration * 20,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        if (ticksElapsed % 20 == 0) {
                            Utils.playGlobalSound(miasmaTarget.getLocation(), Sound.DIG_SNOW, 2, 0.4f);

                            for (int i = 0; i < 3; i++) {
                                ParticleEffect.REDSTONE.display(
                                        new ParticleEffect.OrdinaryColor(30, 200, 30),
                                        miasmaTarget.getLocation().clone().add(
                                                (Math.random() * 2) - 1,
                                                1.2 + (Math.random() * 2) - 1,
                                                (Math.random() * 2) - 1
                                        ),
                                        500
                                );
                            }

                            float healthDamage = miasmaTarget.getMaxHealth() * maxHealthDamage / 100f;
                            // 4% current health damage.
                            miasmaTarget.addDamageInstance(
                                    wp,
                                    name,
                                    50 + healthDamage,
                                    50 + healthDamage,
                                    -1,
                                    100,
                                    false
                            );
                        }
                    })
            );
            playersHit += hitCounter;

            ImpalingStrike.giveLeechCooldown(
                    wp,
                    miasmaTarget,
                    leechDuration,
                    0.25f,
                    0.15f,
                    warlordsDamageHealingFinalEvent -> {
                        tempDrainingMiasma.numberOfLeechProcd++;
                    }
            );

        }

        if (pveUpgrade) {
            increaseDamageOnHit(wp, hitCounter);
        }

        return true;
    }

    public int getEnemyHitRadius() {
        return enemyHitRadius;
    }

    private void increaseDamageOnHit(WarlordsEntity we, int hitCounter) {
        we.getCooldownManager().addCooldown(new RegularCooldown<DrainingMiasma>(
                "Impaling Boost",
                "IMP BOOST",
                DrainingMiasma.class,
                new DrainingMiasma(),
                we,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                duration * 20
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (event.getAbility().equals("Impaling Strike")) {
                    return currentDamageValue * (1 + (0.04f * hitCounter));
                }
                return currentDamageValue;
            }
        });
    }

    public void setEnemyHitRadius(int enemyHitRadius) {
        this.enemyHitRadius = enemyHitRadius;
    }

    public int getLeechDuration() {
        return leechDuration;
    }

    public void setLeechDuration(int leechDuration) {
        this.leechDuration = leechDuration;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


}
