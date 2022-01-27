package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.EffectUtils;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;

public class CrossVital extends AbstractAbility {

    private final int duration = 12;
    private final int speedDuration = 4;

    public CrossVital() {
        super("Cross Vital", 0, 0, 30, 40, -1, 50);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Increase the critical damage of all\n" +
                "§7your abilities by §c" + critMultiplier + "%§7. When dealing a critical\n" +
                "§7hit with Judgement Strike, gain an absorption shield\n" +
                "§7equal to §e25% §7of the strike's damage. Gain §e40% §7speed\n" +
                "§7for §6" + speedDuration + " §7seconds upon defeating an enemy while Cross\n" +
                "§7Vital is active. Lasts §6" + duration + " §7seconds.";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        CrossVital tempCrossVital = new CrossVital();

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "mage.arcaneshield.activation", 2, 2);
        }

        EffectUtils.playStarAnimation(player, 1, ParticleEffect.CRIT);

        wp.getCooldownManager().addRegularCooldown(name, "VITAL", CrossVital.class, tempCrossVital, wp, CooldownTypes.ABILITY, cooldownManager -> {
        }, duration * 20);

        wp.getGame().getGameTasks().put(

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (wp.getCooldownManager().hasCooldown(tempCrossVital)) {
                            ParticleEffect.ENCHANTMENT_TABLE.display(0.4f, 0.4f, 0.4f, 0, 4, wp.getLocation().add(0 , 1, 0), 500);
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Warlords.getInstance(), 0, 4),
                System.currentTimeMillis()
        );

        return true;
    }
}
