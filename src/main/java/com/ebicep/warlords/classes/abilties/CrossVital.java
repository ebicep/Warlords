package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.EffectUtils;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class CrossVital extends AbstractAbility {

    public static final int SPEED_DURATION = 4;

    private final int duration = 12;

    public CrossVital() {
        super("Soul Switch", 0, 0, 30, 40, -1, 50);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7PLACEHOLDER";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {

        for (WarlordsPlayer swapTarget : PlayerFilter
                .entitiesAround(wp.getLocation(), 15, 15, 15)
                .aliveEnemiesOf(wp)
                .requireLineOfSight(wp)
                .closestFirst(wp)
        ) {
            Location swapLocation = swapTarget.getLocation();
            Location ownLocation = wp.getLocation();

            swapTarget.teleport(new Location(
                    wp.getWorld(), ownLocation.getX(), ownLocation.getY(), ownLocation.getZ(), swapLocation.getYaw(), swapLocation.getPitch()));
            wp.teleport(new Location(
                    swapLocation.getWorld(), swapLocation.getX(), swapLocation.getY(), swapLocation.getZ(), ownLocation.getYaw(), ownLocation.getPitch()));

            wp.subtractEnergy(energyCost);

            EffectUtils.playCylinderAnimation(swapLocation, 1.05, ParticleEffect.CLOUD, 1);
            EffectUtils.playCylinderAnimation(ownLocation, 1.05, ParticleEffect.CLOUD, 1);

            for (Player player1 : player.getWorld().getPlayers()) {
                player1.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 2, 1.5f);
            }

            return true;
        }

        return false;
    }
}
