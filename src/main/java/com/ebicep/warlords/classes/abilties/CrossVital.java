package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;

import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.EffectUtils;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;

public class CrossVital extends AbstractAbility {

    public CrossVital() {
        super("Cross Vital", 0, 0, 20, 40, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Become invulnerable for §61 §7second. Any\n" +
                "§7incoming damage during this will be converted\n" +
                "§7into 50% energy and 50% critical chance. However,\n" +
                "§7not taking any damage during Cross Vital will\n" +
                "§7make you vulnerable, increasing the damage you\n" +
                "§7take by §c20% §7for §63 §7seconds.";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        CrossVital tempCrossVital = new CrossVital();
        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "rogue.assassinstrike.activation", 2, 1.5f);
        }

        wp.getCooldownManager().addRegularCooldown(name, "EARTH", CrossVital.class, tempCrossVital, wp, CooldownTypes.ABILITY, cooldownManager -> {
        }, 12 * 20);

        wp.getGame().getGameTasks().put(

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (wp.getCooldownManager().hasCooldown(tempCrossVital)) {

                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Warlords.getInstance(), 0, 20),
                System.currentTimeMillis()
        );

        EffectUtils.playStarAnimation(player.getLocation(), 0.5f, ParticleEffect.FLAME);

        return true;
    }
}
