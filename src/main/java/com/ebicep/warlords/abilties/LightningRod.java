package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class LightningRod extends AbstractAbility {

    private final int energyRestore = 160;
    private final int knockbackRadius = 5;

    public LightningRod() {
        super("Lightning Rod", 0, 0, 31.32f, 0, -1, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Call down an energizing bolt of lightning\n" +
                "§7upon yourself, restoring §a30% §7health and\n" +
                "§e" + energyRestore + " §7energy and knock all nearby enemies\n" +
                "§7in a §e" + knockbackRadius + " §7block radius back.";
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        wp.addEnergy(wp, name, energyRestore);
        wp.addHealingInstance(wp, name, (wp.getMaxHealth() * .3f), (wp.getMaxHealth() * .3f), critChance, critMultiplier, false, false);

        Location playerLocation = player.getLocation();

        PlayerFilter.entitiesAround(player, knockbackRadius, knockbackRadius, knockbackRadius)
                .aliveEnemiesOf(wp)
                .forEach((p) -> {
                    //knockback
                    final Location loc = p.getLocation();
                    final Vector v = player.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-1.45).setY(0.35);

                    p.setVelocity(v);
                });

        // pulsedamage
        List<ArmorStand> totemDownAndClose = Utils.getCapacitorTotemDownAndClose(wp, wp.getEntity());
        totemDownAndClose.forEach(totem -> {
            PlayerFilter.entitiesAround(totem.getLocation(), 6, 6, 6)
                    .aliveEnemiesOf(wp)
                    .forEach(enemy -> enemy.addDamageInstance(
                            wp,
                            wp.getSpec().getOrange().getName(),
                            wp.getSpec().getOrange().getMinDamageHeal(),
                            wp.getSpec().getOrange().getMaxDamageHeal(),
                            wp.getSpec().getOrange().getCritChance(),
                            wp.getSpec().getOrange().getCritMultiplier(),
                            false)
                    );
            new FallingBlockWaveEffect(totem.getLocation().add(0, 1, 0), 6, 1.2, Material.SAPLING, (byte) 0).play();
            Utils.playGlobalSound(totem.getLocation(), "shaman.capacitortotem.pulse", 2, 1);

            player.playSound(player.getLocation(), "shaman.chainlightning.impact", 2, 1);
        });

        new FallingBlockWaveEffect(playerLocation, knockbackRadius, 1, Material.RED_ROSE, (byte) 5).play();
        player.getWorld().spigot().strikeLightningEffect(playerLocation, true);
        Utils.playGlobalSound(player.getLocation(), "shaman.lightningrod.activation", 2, 1);

        return true;
    }
}
