package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BloodLust extends AbstractAbility {

    public BloodLust() {
        super("Blood Lust", 0, 0, 31.32f, 20, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7You lust for blood, healing yourself\n" +
                "§7for §a65% §7of all the damage you deal.\n" +
                "§7Lasts §615 §7seconds.";
    }

    @Override
    public void onActivate(WarlordsPlayer warlordsPlayer, Player player) {
        warlordsPlayer.subtractEnergy(energyCost);
        warlordsPlayer.getCooldownManager().addCooldown(BloodLust.this.getClass(), "LUST", 15, warlordsPlayer, CooldownTypes.ABILITY);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "warrior.bloodlust.activation", 2, 1);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (warlordsPlayer.getCooldownManager().getCooldown(BloodLust.class).size() > 0) {
                    Location location = player.getLocation();
                    location.add((Math.random() - 0.5) * 1, 1.2, (Math.random() - 0.5) * 1);
                    ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(255, 0, 0), location, 500);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 4);

    }
}
