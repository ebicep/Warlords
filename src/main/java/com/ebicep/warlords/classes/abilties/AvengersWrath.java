package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AvengersWrath extends AbstractAbility {

    private final int duration = 12;

    public AvengersWrath() {
        super("Avenger's Wrath", 0, 0, 52.85f, 0, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Burst with incredible holy power,\n" +
                "§7causing your Avenger's Strikes to\n" +
                "§7hit up to §e2 §7additional enemies\n" +
                "§7that are within §e5 §7blocks of your\n" +
                "§7target. Your energy per second is\n" +
                "§7increased by §e20 §7for the duration\n" +
                "§7of the effect. Lasts §6" + duration + " §7seconds.";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        wp.getCooldownManager().addCooldown(name, AvengersWrath.this.getClass(), new AvengersWrath(), "WRATH", duration, wp, CooldownTypes.BUFF);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.avengerswrath.activation", 2, 1);
        }

        wp.getGame().getGameTasks().put(
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!wp.getCooldownManager().getCooldown(AvengersWrath.class).isEmpty()) {
                            Location location = player.getLocation();
                            location.add(0, 1.2, 0);
                            ParticleEffect.SPELL.display(0.3F, 0.1F, 0.3F, 0.2F, 6, location, 500);
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Warlords.getInstance(), 0, 4),
                System.currentTimeMillis()
        );

    }
}
