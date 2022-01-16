package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;

import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class DrainingMiasma extends AbstractAbility {

    private final int duration = 5;

    public DrainingMiasma() {
        super("Draining Miasma", 0, 0, 55, 40, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Summon a toxic-filled cloud around you,\n" +
                "§7poisoning all enemies inside the area. Poisoned\n" +
                "§7enemies take §c4% §7of their current health as\n" +
                "§7damage per second, for §6" + duration + " §7seconds. The\n" +
                "§7caster receives healing equal to §a25% §7of the\n" +
                "§7damage dealt.";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);

        DrainingMiasma tempDrainingMiasma = new DrainingMiasma();
        PlayerFilter.entitiesAround(wp, 6, 6, 6)
                .aliveEnemiesOf(wp)
                .forEach((miasmaTarget) -> {
                    miasmaTarget.getCooldownManager().addRegularCooldown("Draining Miasma", "MIASMA", DrainingMiasma.class, tempDrainingMiasma, wp, CooldownTypes.DEBUFF, cooldownManager -> {
                    }, duration * 20);

                    wp.getGame().getGameTasks().put(


                            new BukkitRunnable() {
                                float totalDamage = 0;
                                @Override
                                public void run() {
                                    float healthDamage = miasmaTarget.getHealth() * 0.04f;
                                    if (miasmaTarget.getCooldownManager().hasCooldown(tempDrainingMiasma)) {
                                        // 6% current health damage.
                                        miasmaTarget.addDamageInstance(wp, "Draining Miasma", healthDamage, healthDamage, -1, 100, false);
                                        totalDamage += healthDamage;

                                        for (Player player1 : player.getWorld().getPlayers()) {
                                            player1.playSound(player.getLocation(), Sound.FIRE_IGNITE, 2, 0.4f);
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
                            }.runTaskTimer(Warlords.getInstance(), 0, 20),
                            System.currentTimeMillis()
                    );
                });

        return true;
    }
}
