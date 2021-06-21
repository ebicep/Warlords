package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;


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
    public void onActivate(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.getSpeed().changeCurrentSpeed("Inspiring Presence", 30, 12 * 20, "BASE");
        warlordsPlayer.getCooldownManager().addCooldown(InspiringPresence.this.getClass(), "PRES", 12, warlordsPlayer, CooldownTypes.BUFF);


        // TODO: make range a circle instead of square
        // TODO: give near players pres after initial onactivate
        List<Entity> near = player.getNearbyEntities(6.0D, 6.0D, 6.0D);
        near = Utils.filterOnlyTeammates(near, player);
        for (Entity entity : near) {
            if (entity instanceof Player && ((Player) entity).getGameMode() != GameMode.SPECTATOR) {
                Warlords.getPlayer((Player) entity).getSpeed().changeCurrentSpeed("Inspiring Presence", 30, 12 * 20, "BASE");
                Warlords.getPlayer((Player) entity).getCooldownManager().addCooldown(InspiringPresence.this.getClass(), "PRES", 12, warlordsPlayer, CooldownTypes.BUFF);
            }
        }

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.inspiringpresence.activation", 2, 1);
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