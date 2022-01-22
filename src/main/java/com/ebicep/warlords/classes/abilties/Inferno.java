package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.ClassesSkillBoosts;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Inferno extends AbstractAbility {

    private final int duration = 18;

    public Inferno() {
        super("Inferno", 0, 0, 46.98f, 0, 30, 30);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Combust into a molten inferno,\n" +
                "§7increasing your Crit Chance by §c" + critChance + "%\n" +
                "§7and your Crit Multiplier by §c" + critMultiplier + "%§7. Lasts\n" +
                "§6" + duration + " §7seconds.";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        wp.getCooldownManager().addCooldown(name, Inferno.this.getClass(), new Inferno(), "INFR", duration, wp, CooldownTypes.BUFF);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "mage.inferno.activation", 2, 1);
        }
        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                if (!wp.getCooldownManager().getCooldown(Inferno.class).isEmpty()) {
                    Location location = player.getLocation();
                    location.add(0, 1.2, 0);
                    ParticleEffect.DRIP_LAVA.display(0.5F, 0.3F, 0.5F, 0.4F, 1, location, 500);
                    ParticleEffect.FLAME.display(0.5F, 0.3F, 0.5F, 0.0001F, 1, location, 500);
                    ParticleEffect.CRIT.display(0.5F, 0.3F, 0.5F, 0.0001F, 1, location, 500);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 3);
    }
}
