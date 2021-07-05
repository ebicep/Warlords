package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LightInfusion extends AbstractAbility {

    public LightInfusion(float cooldown) {
        super("Light Infusion", 0, 0, cooldown, -120, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7You become infused with light,\n" +
                "§7restoring §a120 §7energy and\n" +
                "§7increasing your movement speed by\n" +
                "§e40% §7for §63 §7seconds";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        wp.getSpeed().addSpeedModifier("Infusion", 40, 3 * 20, "BASE");
        wp.subtractEnergy(energyCost);
        wp.getCooldownManager().addCooldown(LightInfusion.this.getClass(), "INF", 3, wp, CooldownTypes.BUFF);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.infusionoflight.activation", 2, 1);
        }

        for (int i = 0; i < 10; i++) {
            Location particleLoc = player.getLocation().add(0, 1.5, 0);
            ParticleEffect.SPELL.display(1F, 0F, 1F, 0.3F, 5, particleLoc, 500);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (wp.getCooldownManager().getCooldown(LightInfusion.class).size() > 0) {
                    Location location = player.getLocation();
                    location.add(0, 1.2, 0);
                    ParticleEffect.SPELL.display(0.3F, 0.1F, 0.3F, 0.2F, 2, location, 500);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 2);

    }
}
