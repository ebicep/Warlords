package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class LightningRod extends AbstractAbility {

    public LightningRod() {
        super("Lightning Rod", 0, 0, 31.32f, 0, 0, 0
        );
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Call down an energizing bolt of lightning\n" +
                "§7upon yourself, restoring §a30% §7health and\n" +
                "§e160 §7energy and knock all nearby enemies back.";
    }

    @Override
    public void onActivate(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.addEnergy(warlordsPlayer, name, 160);
        warlordsPlayer.addHealth(warlordsPlayer, name, (warlordsPlayer.getMaxHealth() * .3f), (warlordsPlayer.getMaxHealth() * .3f), critChance, critMultiplier);

        Location playerLocation = player.getLocation();

        List<Entity> near = player.getNearbyEntities(5.0D, 5.0D, 5.0D);
        near = Utils.filterOutTeammates(near, player);

        for (Entity entity : near) {
            final Location otherLocation = entity.getLocation();
            if (entity instanceof Player && ((Player) entity).getGameMode() != GameMode.SPECTATOR && otherLocation.distanceSquared(playerLocation) < 30) {
                if (entity != player) {
                    //knockback
                    final Location loc = entity.getLocation();
                    final Vector v = player.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-1.5).setY(0.4);

                    entity.setVelocity(v);
                }
            }
        }

        pulseDamage(warlordsPlayer, near);

        new FallingBlockWaveEffect(playerLocation, 4, 1.1, Material.RED_ROSE, (byte) 5).play();
        player.getWorld().spigot().strikeLightningEffect(playerLocation, true);
        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "shaman.lightningrod.activation", 2, 1);
        }
    }

    private void pulseDamage(WarlordsPlayer warlordsPlayer, List<Entity> near) {
        ArmorStand totem = getTotem(warlordsPlayer.getPlayer());
        for (Entity entity : near) {
            if (entity instanceof Player) {
                Player nearPlayer = (Player) entity;
                if (Utils.totemDownAndClose(warlordsPlayer, nearPlayer) && nearPlayer.getGameMode() != GameMode.SPECTATOR) {
                    new FallingBlockWaveEffect(totem.getLocation(), 4, 1.1, Material.SAPLING, (byte) 0).play();
                    Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, warlordsPlayer.getSpec().getOrange().getName(), warlordsPlayer.getSpec().getOrange().getMinDamageHeal(), warlordsPlayer.getSpec().getOrange().getMaxDamageHeal(), warlordsPlayer.getSpec().getOrange().getCritChance(), warlordsPlayer.getSpec().getOrange().getCritMultiplier());
                }
            }
        }
    }

    private ArmorStand getTotem(Player player) {
        for (Entity entity : player.getNearbyEntities(20, 17, 20)) {
            if (entity instanceof ArmorStand && entity.hasMetadata("Capacitor Totem - " + player.getName())) {
                return (ArmorStand) entity;
            }
        }
        return null;
    }
}
