package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.*;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class DrainingMiasma extends AbstractAbility {

    private final int duration = 5;

    public DrainingMiasma() {
        super("Draining Miasma", 0, 0, 55, 40, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Summon a toxic-filled cloud around you,\n" +
                "§7poisoning all enemies inside the area. Poisoned\n" +
                "§7enemies take §c20 §7+ §c4% §7of their max health as\n" +
                "§7damage per second, for §6" + duration + " §7seconds. Enemies\n" +
                "§7poisoned by your Draining Miasma are blinded for §62\n" +
                "§7seconds after the poison ends. The caster receives\n" +
                "§7healing equal to §a25% §7of the damage dealt.\n";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "rogue.drainingmiasma.activation", 2, 1.7f);
            player1.playSound(player.getLocation(), "shaman.earthlivingweapon.activation", 2, 0.65f);
        }

        EffectUtils.playCylinderAnimation(player, 6, 30, 200, 30);
        EffectUtils.playSphereAnimation(player, 6, ParticleEffect.SLIME, 1);

        FireWorkEffectPlayer.playFirework(wp.getLocation(), FireworkEffect.builder()
                .withColor(Color.LIME)
                .with(FireworkEffect.Type.BALL_LARGE)
                .build());

        DrainingMiasma tempDrainingMiasma = new DrainingMiasma();
        for (WarlordsPlayer miasmaTarget : PlayerFilter
                .entitiesAround(wp, 6, 6, 6)
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
                    duration * 20);

            new GameRunnable(wp.getGame()) {

                float totalDamage = 0;

                @Override
                public void run() {
                    float healthDamage = miasmaTarget.getMaxHealth() * 0.04f;
                    if (miasmaTarget.getCooldownManager().hasCooldown(tempDrainingMiasma)) {
                        // 4% current health damage.
                        miasmaTarget.addDamageInstance(wp, "Draining Miasma", 20 + healthDamage, 20 + healthDamage, -1, 100, false);
                        totalDamage += healthDamage;

                        for (Player player1 : miasmaTarget.getWorld().getPlayers()) {
                            player1.playSound(miasmaTarget.getLocation(), Sound.FIRE_IGNITE, 2, 0.4f);
                        }

                        for (int i = 0; i < 3; i++) {
                            ParticleEffect.REDSTONE.display(
                                    new ParticleEffect.OrdinaryColor(30, 200, 30),
                                    miasmaTarget.getLocation().clone().add((Math.random() * 2) - 1, 1.2 + (Math.random() * 2) - 1, (Math.random() * 2) - 1),
                                    500);
                        }

                    } else {
                        wp.addHealingInstance(wp, "Draining Miasma", totalDamage * 0.25f, totalDamage * 0.25f, -1, 100, false, false);
                        miasmaTarget.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2 * 25, 0, true, false), true);
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 20);
        }

        return true;
    }
}
