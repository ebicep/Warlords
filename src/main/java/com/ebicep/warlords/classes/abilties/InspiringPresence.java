package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;



public class InspiringPresence extends AbstractAbility {

    public InspiringPresence() {
        super("Inspiring Presence", 0, 0, 60f + 10.47f, 0, 0, 0
        );
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Your presence on the battlefield\n" +
                "§7inspires your allies, increasing\n" +
                "§7their energy regeneration by §e10\n" +
                "§7per second and their movement\n" +
                "§7by §e30% §7for §612 §7seconds.";
    }

    @Override
    public void onActivate(WarlordsPlayer warlordsPlayer, Player player) {
        warlordsPlayer.getCooldownManager().addCooldown(InspiringPresence.this.getClass(), "PRES", 12, warlordsPlayer, CooldownTypes.BUFF);
        PlayerFilter.entitiesAround(warlordsPlayer, 6.0D, 6.0D, 6.0D)
            .aliveTeammatesOf(warlordsPlayer)
            .concat(warlordsPlayer)
            .forEach((nearPlayer) -> {
                nearPlayer.getSpeed().addSpeedModifier("Inspiring Presence", 30, 12 * 20, "BASE");
                nearPlayer.getCooldownManager().addCooldown(InspiringPresence.this.getClass(), "PRES", 12, warlordsPlayer, CooldownTypes.BUFF);
            });

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.inspiringpresence.activation", 2, 0.1f);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (warlordsPlayer.getCooldownManager().getCooldown(InspiringPresence.class).size() > 0) {
                    Location location = player.getLocation();
                    location.add(0, 1.5, 0);
                    ParticleEffect.SMOKE_NORMAL.display(0.3F, 0.3F, 0.3F, 0.02F, 1, location, 500);
                    ParticleEffect.SPELL.display(0.3F, 0.3F, 0.3F, 0.5F, 2, location, 500);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 2);
    }
}