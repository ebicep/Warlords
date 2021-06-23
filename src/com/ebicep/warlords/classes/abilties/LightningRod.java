package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.util.Utils;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class LightningRod extends AbstractAbility {

    public LightningRod() {
        super("Lightning Rod", 0, 0, 32, 0, 0, 0,
                "§7Call down an energizing bolt of lightning\n" +
                        "§7upon yourself, restoring §a30% §7health and\n" +
                        "§e160 §7energy and knock all nearby enemies back.");
    }

    @Override
    public void onActivate(WarlordsPlayer warlordsPlayer, Player player) {
        warlordsPlayer.addEnergy(warlordsPlayer, name, 160);
        warlordsPlayer.addHealth(warlordsPlayer, name, (int) (warlordsPlayer.getMaxHealth() * .3), (int) (warlordsPlayer.getMaxHealth() * .3), critChance, critMultiplier);

        Location playerLocation = player.getLocation();

        
        PlayerFilter.entitiesAround(player, 5.477, 5.477, 5.477)
            .aliveEnemiesOf(warlordsPlayer)
            .forEach((p) -> {
                //knockback
                final Location loc = p.getLocation();
                final Vector v = player.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-1.5).setY(0.4);

                p.setVelocity(v);
                
                // pulsedamage
                if (Utils.totemDownAndClose(warlordsPlayer, p.getEntity())) {
                    p.addHealth(warlordsPlayer, warlordsPlayer.getSpec().getOrange().getName(), warlordsPlayer.getSpec().getOrange().getMinDamageHeal(), warlordsPlayer.getSpec().getOrange().getMaxDamageHeal(), warlordsPlayer.getSpec().getOrange().getCritChance(), warlordsPlayer.getSpec().getOrange().getCritMultiplier());
                }
            });

        ArmorStand totem = getTotem(warlordsPlayer);
        if (totem != null) {
            new FallingBlockWaveEffect(totem.getLocation(), 4, 1.1, Material.SAPLING, (byte) 0).play();
        }
        player.getWorld().spigot().strikeLightningEffect(playerLocation, true);
        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "shaman.lightningrod.activation", 2, 1);
        }
    }

    @Nullable
    private ArmorStand getTotem(@Nonnull WarlordsPlayer player) {
        for (Entity entity : player.getEntity().getNearbyEntities(20, 17, 20)) {
            if (entity instanceof ArmorStand && entity.hasMetadata("Capacitor Totem - " + player.getName())) {
                return (ArmorStand) entity;
            }
        }
        return null;
    }
}
