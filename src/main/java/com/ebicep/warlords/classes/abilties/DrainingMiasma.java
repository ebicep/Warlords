package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;

import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;

public class DrainingMiasma extends AbstractAbility {

    private final int duration = 4;

    public DrainingMiasma() {
        super("Draining Miasma", 0, 0, 55, 40, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {

    }

    @Override
    public void onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);

        DrainingMiasma tempDrainingMiasma = new DrainingMiasma();
        PlayerFilter.entitiesAround(wp, 6, 6, 6)
                .aliveEnemiesOf(wp)
                .forEach((miasmaTarget) -> {
                    // 4% current health damage.
                    float healthDamage = miasmaTarget.getHealth() / 15.5f;
                    miasmaTarget.addDamageInstance(wp, "Draining Miasma", healthDamage, healthDamage, -1, 100, false);
                    miasmaTarget.getCooldownManager().addRegularCooldown("Draining Miasma", "MIASMA", DrainingMiasma.class, tempDrainingMiasma, wp, CooldownTypes.DEBUFF, cooldownManager -> {
                    }, duration * 20);

                    wp.getGame().getGameTasks().put(

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (miasmaTarget.getCooldownManager().hasCooldown(tempDrainingMiasma)) {
                                        ParticleEffect.REDSTONE.display(
                                                new ParticleEffect.OrdinaryColor(30, 200, 30),
                                                miasmaTarget.getLocation().clone().add((Math.random() * 2) - 1, 1.2 + (Math.random() * 2) - 1, (Math.random() * 2) - 1),
                                                500);
                                    } else {
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2 * 20, 0, true, false), true);
                                        this.cancel();
                                    }
                                }
                            }.runTaskTimer(Warlords.getInstance(), 0, 10),
                            System.currentTimeMillis()
                    );
                });

    }
}
