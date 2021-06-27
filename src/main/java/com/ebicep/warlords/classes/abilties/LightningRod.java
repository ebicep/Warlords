package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    public void onActivate(WarlordsPlayer warlordsPlayer, Player player) {
        warlordsPlayer.addEnergy(warlordsPlayer, name, 160);
        warlordsPlayer.addHealth(warlordsPlayer, name, (warlordsPlayer.getMaxHealth() * .3f), (warlordsPlayer.getMaxHealth() * .3f), critChance, critMultiplier);

        Location playerLocation = player.getLocation();


        PlayerFilter.entitiesAround(player, 5.5, 5.5, 5.5)
            .aliveEnemiesOf(warlordsPlayer)
            .forEach((p) -> {
                //knockback
                final Location loc = p.getLocation();
                final Vector v = player.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-1.5).setY(0.4);

                p.setVelocity(v);

                // pulsedamage
                if (Utils.getTotemDownAndClose(warlordsPlayer, p.getEntity()) != null) {
                    p.addHealth(warlordsPlayer, warlordsPlayer.getSpec().getOrange().getName(), warlordsPlayer.getSpec().getOrange().getMinDamageHeal(), warlordsPlayer.getSpec().getOrange().getMaxDamageHeal(), warlordsPlayer.getSpec().getOrange().getCritChance(), warlordsPlayer.getSpec().getOrange().getCritMultiplier());
                }
            });

        ArmorStand totem = getTotem(warlordsPlayer);
        if (totem != null) {
            new FallingBlockWaveEffect(totem.getLocation(), 4, 1.1, Material.SAPLING, (byte) 0).play();
        }

        //pulseDamage(warlordsPlayer, near);

        new FallingBlockWaveEffect(playerLocation, 4, 1.1, Material.RED_ROSE, (byte) 5).play();
        player.getWorld().spigot().strikeLightningEffect(playerLocation, true);
        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "shaman.lightningrod.activation", 2, 1);
        }
    }

    /*private void pulseDamage(WarlordsPlayer warlordsPlayer, List<Entity> near) {
        for (Entity entity : near) {
            if (entity instanceof Player) {
                Player nearPlayer = (Player) entity;
                if (Utils.totemDownAndClose(warlordsPlayer, nearPlayer) && nearPlayer.getGameMode() != GameMode.SPECTATOR) {
                    Warlords.getPlayer(nearPlayer).addHealth(warlordsPlayer, warlordsPlayer.getSpec().getOrange().getName(), warlordsPlayer.getSpec().getOrange().getMinDamageHeal(), warlordsPlayer.getSpec().getOrange().getMaxDamageHeal(), warlordsPlayer.getSpec().getOrange().getCritChance(), warlordsPlayer.getSpec().getOrange().getCritMultiplier());
                }
            }
        }
    }*/

    @Nullable
    private ArmorStand getTotem(@Nonnull WarlordsPlayer player) {
        for (Entity entity : player.getEntity().getNearbyEntities(20, 17, 20)) {
            if (entity instanceof ArmorStand && entity.hasMetadata("capacitor-totem-" + player.getName().toLowerCase())) {
                return (ArmorStand) entity;
            }
        }
        return null;
    }
}
