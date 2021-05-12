package com.ebicep.warlords.powerups;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.maps.GameLobby;
import com.ebicep.warlords.maps.Map;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PowerupManager extends BukkitRunnable {

    private List<AbstractPowerUp> powerUps = new ArrayList<>();
    private GameLobby.GameMap map;

    public PowerupManager(GameLobby.GameMap map) {
        this.map = map;
        powerUps.add(new DamagePowerUp(map.map.getDamagePowerupBlue(), 30, 45 * 20));
        powerUps.add(new DamagePowerUp(map.map.getDamagePowerupRed(), 30, 45 * 20));
        powerUps.add(new HealingPowerUp(map.map.getHealingPowerupBlue(), 0, 45 * 20));
        powerUps.add(new HealingPowerUp(map.map.getHealingPowerupRed(), 0, 45 * 20));
        //TODO find duration of speed
        powerUps.add(new SpeedPowerUp(map.map.getSpeedPowerupBlue(), 10, 45 * 20));
        powerUps.add(new SpeedPowerUp(map.map.getSpeedPowerupRed(), 10, 45 * 20));
    }


    @Override
    public void run() {
        for (AbstractPowerUp powerUp : powerUps) {
            if (powerUp.getCooldown() == 0) {
                List<Entity> entitiesNear = (List<Entity>) powerUp.getLocation().getWorld().getNearbyEntities(powerUp.getLocation(), 1, 1, 1);
                entitiesNear = entitiesNear.stream().filter(entity -> entity instanceof Player).collect(Collectors.toList());
                if (entitiesNear.size() != 0) {
                    WarlordsPlayer warlordsPlayer = Warlords.getPlayer((Player) entitiesNear.get(0));
                    if (powerUp instanceof DamagePowerUp) {
                        if (warlordsPlayer.isEnergyPowerup()) {
                            Warlords.getPlayer((Player) entitiesNear.get(0)).setPowerUpEnergy(powerUp.getDuration());
                            entitiesNear.get(0).sendMessage("picked up energy");
                        } else {
                            Warlords.getPlayer((Player) entitiesNear.get(0)).setPowerUpDamage(powerUp.getDuration());
                            entitiesNear.get(0).sendMessage("picked up damage");

                        }
                    } else if (powerUp instanceof HealingPowerUp) {
                        Warlords.getPlayer((Player) entitiesNear.get(0)).setPowerUpHeal(true);
                        entitiesNear.get(0).sendMessage("picked up healing");

                    } else if (powerUp instanceof SpeedPowerUp) {
                        Warlords.getPlayer((Player) entitiesNear.get(0)).setPowerUpSpeed(powerUp.getDuration());
                        entitiesNear.get(0).sendMessage("picked up speed");

                    }
//                    PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(powerUp.getPowerUp().getId());
//                    for (WarlordsPlayer value : Warlords.getPlayers().values()) {
//                        ((CraftPlayer)value.getPlayer()).getHandle().playerConnection.sendPacket(destroyPacket);
//                    }
                    powerUp.getPowerUp().remove();
                    powerUp.setCooldown(powerUp.getMaxCooldown());
                }
            } else {
                powerUp.setCooldown(powerUp.getCooldown() - 1);
                if (powerUp.getCooldown() == 0) {
                    powerUp.spawn();
                }
            }
        }
    }
}
