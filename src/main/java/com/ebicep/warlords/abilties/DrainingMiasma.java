package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class DrainingMiasma extends AbstractAbility {

    private int duration = 5;
    private int enemyHitRadius = 8;
    // Percent
    private final int maxHealthDamage = 4;

    public DrainingMiasma() {
        super("Draining Miasma", 0, 0, 55, 40, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Summon a toxin-filled cloud around you,\n" +
                "§7poisoning all enemies inside the area. Poisoned\n" +
                "§7enemies take §c50 §7+ §c" + maxHealthDamage + "% §7of their max health as\n" +
                "§7damage per second, for §6" + duration + " §7seconds. Enemies\n" +
                "§7poisoned by your Draining Miasma are slowed by\n" +
                "§e25% §7for §63 §7seconds on cast." +
                "\n\n" +
                "§7Each enemy hit will be afflicted with §aLEECH §7for\n" +
                "§65 §7seconds.";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);

        Utils.playGlobalSound(player.getLocation(), "rogue.drainingmiasma.activation", 2, 1.7f);
        Utils.playGlobalSound(player.getLocation(), "shaman.earthlivingweapon.activation", 2, 0.65f);

        EffectUtils.playSphereAnimation(player, 6, ParticleEffect.SLIME, 1);

        FireWorkEffectPlayer.playFirework(wp.getLocation(), FireworkEffect.builder()
                .withColor(Color.LIME)
                .with(FireworkEffect.Type.BALL_LARGE)
                .build());

        DrainingMiasma tempDrainingMiasma = new DrainingMiasma();
        for (WarlordsPlayer miasmaTarget : PlayerFilter
                .entitiesAround(wp, getEnemyHitRadius(), getEnemyHitRadius(), getEnemyHitRadius())
                .aliveEnemiesOf(wp)
        ) {
            Runnable cancelSpeed = miasmaTarget.getSpeed().addSpeedModifier("Draining Miasma Slow", -25, 3 * 20, "BASE");
            miasmaTarget.getCooldownManager().addRegularCooldown(
                    "Draining Miasma",
                    "MIASMA",
                    DrainingMiasma.class,
                    tempDrainingMiasma,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                        cancelSpeed.run();
                    },
                    duration * 20
            );

            miasmaTarget.getCooldownManager().removeCooldown(ImpalingStrike.class);
            miasmaTarget.getCooldownManager().addCooldown(new RegularCooldown<ImpalingStrike>(
                    "Leech Debuff",
                    "LEECH",
                    ImpalingStrike.class,
                    new ImpalingStrike(),
                    wp,
                    CooldownTypes.DEBUFF,
                    cooldownManager -> {
                    },
                    5 * 20
            ) {
                @Override
                public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                    float healingMultiplier;
                    if (event.getAttacker() == wp) {
                        healingMultiplier = 0.25f;
                    } else {
                        healingMultiplier = 0.15f;
                    }

                    event.getAttacker().addHealingInstance(
                            wp,
                            "Leech",
                            currentDamageValue * healingMultiplier,
                            currentDamageValue * healingMultiplier,
                            -1,
                            100,
                            false,
                            false
                    ).ifPresent(warlordsDamageHealingFinalEvent -> {
                        if (event.getPlayer().hasFlag()) {
                            this.getCooldownObject().addHealingDoneFromEnemyCarrier(warlordsDamageHealingFinalEvent.getValue());
                        }
                    });
                }
            });

            new GameRunnable(wp.getGame()) {
                @Override
                public void run() {
                    float healthDamage = miasmaTarget.getMaxHealth() * maxHealthDamage / 100f;
                    if (miasmaTarget.getCooldownManager().hasCooldown(tempDrainingMiasma)) {
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

                        Utils.playGlobalSound(miasmaTarget.getLocation(), Sound.DIG_SNOW, 2, 0.4f);

                        for (int i = 0; i < 3; i++) {
                            ParticleEffect.REDSTONE.display(
                                    new ParticleEffect.OrdinaryColor(30, 200, 30),
                                    miasmaTarget.getLocation().clone().add(
                                    (Math.random() * 2) - 1,
                                    1.2 + (Math.random() * 2) - 1,
                                    (Math.random() * 2) - 1),
                                    500
                            );
                        }
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 20);
        }

        return true;
    }

    public int getEnemyHitRadius() {
        return enemyHitRadius;
    }

    public void setEnemyHitRadius(int enemyHitRadius) {
        this.enemyHitRadius = enemyHitRadius;
    }
}
