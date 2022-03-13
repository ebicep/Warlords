package com.ebicep.warlords.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.*;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class DrainingMiasma extends AbstractAbility {

    private int duration = 5;
    private int enemyHitRadius = 6;
    private int allyHitRadius = 6;
    // Percent
    private final int maxHealthDamage = 4;
    private int damageDealtHealing = 40;

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
                "§7The caster emits healing particles that heal all\n" +
                "§7allies within the range for §a" + damageDealtHealing + "% §7of the damage\n" +
                "§7dealt and increase their movement speed by §e30%\n" +
                "§7for §62 §7seconds.\n";
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
            miasmaTarget.getCooldownManager().addRegularCooldown(
                    "Draining Miasma",
                    "MIASMA",
                    DrainingMiasma.class,
                    tempDrainingMiasma,
                    wp,
                    CooldownTypes.DEBUFF,
                    cooldownManager -> {},
                    duration * 20
            );

            miasmaTarget.getSpeed().addSpeedModifier("Draining Miasma Slow", -25, 3 * 20, "BASE");

            new GameRunnable(wp.getGame()) {

                float totalDamage = 0;

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

                        totalDamage += healthDamage;
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

                        for (WarlordsPlayer ally : PlayerFilter
                                .entitiesAround(wp, getAllyHitRadius(), getAllyHitRadius(), getAllyHitRadius())
                                .aliveTeammatesOf(wp)
                        ) {
                            ally.addHealingInstance(
                                    wp,
                                    name,
                                    totalDamage * damageDealtHealing / 100f,
                                    totalDamage * damageDealtHealing / 100f,
                                    -1,
                                    100,
                                    false,
                                    false
                            );

                            totalDamage = 0;

                            ally.getSpeed().addSpeedModifier("Draining Miasma Speed", 30, 2 * 20, "BASE");
                        }
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 20);
        }

        return true;
    }

    public int getDamageDealtHealing() {
        return damageDealtHealing;
    }

    public void setDamageDealtHealing(int damageDealtHealing) {
        this.damageDealtHealing = damageDealtHealing;
    }

    public int getEnemyHitRadius() {
        return enemyHitRadius;
    }

    public void setEnemyHitRadius(int enemyHitRadius) {
        this.enemyHitRadius = enemyHitRadius;
    }

    public int getAllyHitRadius() {
        return allyHitRadius;
    }

    public void setAllyHitRadius(int allyHitRadius) {
        this.allyHitRadius = allyHitRadius;
    }
}
