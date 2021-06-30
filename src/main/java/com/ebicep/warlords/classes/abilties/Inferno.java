package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Inferno extends AbstractAbility {

    public Inferno() {
        super("Inferno", 0, 0, 46.98f, 0, 30, 30
        );
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Combust into a molten inferno,\n" +
                "§7increasing your Crit Chance by §c30%\n" +
                "§7and your Crit Multiplier by §c30%§7. Lasts\n" +
                "§618 §7seconds.";
    }

    @Override
    public void onActivate(WarlordsPlayer warlordsPlayer, Player player) {
        warlordsPlayer.getCooldownManager().addCooldown(Inferno.this.getClass(), "INFR", 18, warlordsPlayer, CooldownTypes.BUFF);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "mage.inferno.activation", 2, 1);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (warlordsPlayer.getCooldownManager().getCooldown(Inferno.class).size() > 0) {
                    Location location = player.getLocation();
                    location.add(0, 1.2, 0);
                    ParticleEffect.DRIP_LAVA.display(0.5F, 0.3F, 0.5F, 0.4F, 1, location, 500);
                    ParticleEffect.FLAME.display(0.5F, 0.3F, 0.5F, 0.0001F, 1, location, 500);
                    ParticleEffect.CRIT.display(0.5F, 0.3F, 0.5F, 0.0001F, 1, location, 500);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 2);
    }
}
