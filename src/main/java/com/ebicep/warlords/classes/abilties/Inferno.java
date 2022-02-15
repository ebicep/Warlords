package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Inferno extends AbstractAbility {

    private final int duration = 18;
    private final int critChance = 30;
    private final int critMultiplier = 30;

    public Inferno() {
        super("Inferno", 0, 0, 46.98f, 0, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Combust into a molten inferno,\n" +
                "§7increasing your Crit Chance by §c" + critChance + "%\n" +
                "§7and your Crit Multiplier by §c" + critMultiplier + "%§7. Lasts\n" +
                "§6" + duration + " §7seconds.";
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        Inferno tempInferno = new Inferno();
        wp.getCooldownManager().addCooldown(new RegularCooldown<Inferno>(
                name,
                "INFR",
                Inferno.class,
                tempInferno,
                wp,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                duration * 20
        ) {
            @Override
            public boolean distinct() {
                return true;
            }

            @Override
            public int addCritChanceFromAttacker(WarlordsDamageHealingEvent event, int currentCritChance) {
                if (event.getAbility().isEmpty() || event.getAbility().equals("Time Warp"))
                    return currentCritChance;
                return currentCritChance + critChance;
            }

            @Override
            public int addCritMultiplierFromAttacker(WarlordsDamageHealingEvent event, int currentCritMultiplier) {
                if (event.getAbility().isEmpty() || event.getAbility().equals("Time Warp"))
                    return currentCritMultiplier;
                return currentCritMultiplier + critMultiplier;
            }
        });

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "mage.inferno.activation", 2, 1);
        }
        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                if (wp.getCooldownManager().hasCooldown(tempInferno)) {
                    Location location = wp.getLocation();
                    location.add(0, 1.2, 0);
                    ParticleEffect.DRIP_LAVA.display(0.5F, 0.3F, 0.5F, 0.4F, 1, location, 500);
                    ParticleEffect.FLAME.display(0.5F, 0.3F, 0.5F, 0.0001F, 1, location, 500);
                    ParticleEffect.CRIT.display(0.5F, 0.3F, 0.5F, 0.0001F, 1, location, 500);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 3);

        return true;
    }
}
