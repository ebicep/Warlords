package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class LightningRod extends AbstractAbility {

    public LightningRod() {
        super("Lightning Rod", 0, 0, 31.32f, 0, -1, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Call down an energizing bolt of lightning\n" +
                "§7upon yourself, restoring §a30% §7health and\n" +
                "§e160 §7energy and knock all nearby enemies back.";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        wp.addEnergy(wp, name, 160);
        wp.addHealth(wp, name, (wp.getMaxHealth() * .3f), (wp.getMaxHealth() * .3f), critChance, critMultiplier);

        Location playerLocation = player.getLocation();


        PlayerFilter.entitiesAround(player, 5, 5, 5)
                .aliveEnemiesOf(wp)
                .forEach((p) -> {
                    //knockback
                    final Location loc = p.getLocation();
                    final Vector v = player.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-1.45).setY(0.35);

                    p.setVelocity(v);

                    // pulsedamage
                    if (Utils.getTotemDownAndClose(wp, p.getEntity()) != null) {
                        p.addHealth(wp, wp.getSpec().getOrange().getName(), wp.getSpec().getOrange().getMinDamageHeal(), wp.getSpec().getOrange().getMaxDamageHeal(), wp.getSpec().getOrange().getCritChance(), wp.getSpec().getOrange().getCritMultiplier());
                        new FallingBlockWaveEffect(p.getLocation().add(0, 1, 0), 5, 1.2, Material.SAPLING, (byte) 0).play();
                        for (Player player1 : wp.getWorld().getPlayers()) {
                            player1.playSound(wp.getLocation(), "shaman.capacitortotem.pulse", 2, 1);
                        }
                    }
                });

        new FallingBlockWaveEffect(playerLocation, 5, 1, Material.RED_ROSE, (byte) 5).play();
        player.getWorld().spigot().strikeLightningEffect(playerLocation, true);
        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "shaman.lightningrod.activation", 2, 1);
        }
    }
}
