package com.ebicep.warlords.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.EffectUtils;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class SoulSwitch extends AbstractAbility {

    private final int radius = 13;

    public SoulSwitch() {
        super("Soul Switch", 0, 0, 30, 40, -1, 50);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Switch locations with an enemy, has an\n" +
                "§7optimal range of §e" + radius + " §7blocks. Soul Switch\n" +
                "§7has low vertical range.";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {

        for (WarlordsPlayer swapTarget : PlayerFilter
                .entitiesAround(wp.getLocation(), radius, 6, radius)
                .aliveEnemiesOf(wp)
                .requireLineOfSight(wp)
                .closestFirst(wp)
        ) {
            if (swapTarget.getCarriedFlag() != null) {
                wp.sendMessage(ChatColor.RED + "You cannot Soul Switch with a player holding the flag!");
            } else if (wp.getCarriedFlag() != null) {
                wp.sendMessage(ChatColor.RED + "You cannot Soul Switch while holding the flag!");
            } else {
                Location swapLocation = swapTarget.getLocation();
                Location ownLocation = wp.getLocation();

                swapTarget.sendMessage(WarlordsPlayer.GIVE_ARROW + ChatColor.GRAY + "You've been Soul Switched by " + ChatColor.YELLOW + wp.getName() + "!");
                swapTarget.teleport(new Location(
                        wp.getWorld(),
                        ownLocation.getX(),
                        ownLocation.getY(),
                        ownLocation.getZ(),
                        swapLocation.getYaw(),
                        swapLocation.getPitch()));

                wp.sendMessage(WarlordsPlayer.RECEIVE_ARROW + ChatColor.GRAY + "You swapped with " + ChatColor.YELLOW + swapTarget.getName() + "!");
                wp.teleport(new Location(
                        swapLocation.getWorld(),
                        swapLocation.getX(),
                        swapLocation.getY(),
                        swapLocation.getZ(),
                        ownLocation.getYaw(),
                        ownLocation.getPitch()));

                wp.subtractEnergy(energyCost);

                EffectUtils.playCylinderAnimation(swapLocation, 1.05, ParticleEffect.CLOUD, 1);
                EffectUtils.playCylinderAnimation(ownLocation, 1.05, ParticleEffect.CLOUD, 1);

                Utils.playGlobalSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 2, 1.5f);

                return true;
            }
        }

        return false;
    }
}
