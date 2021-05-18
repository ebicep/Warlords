package com.ebicep.warlords.powerups;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.maps.GameMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PowerupManager extends BukkitRunnable {

    private List<AbstractPowerUp> powerUps = new ArrayList<>();
    private GameMap map;

    public PowerupManager(GameMap map) {
        this.map = map;
        powerUps.add(new DamagePowerUp(map.getDamagePowerupBlue(), 30, 45 * 20, 30));
        powerUps.add(new DamagePowerUp(map.getDamagePowerupRed(), 30, 45 * 20, 30));
        powerUps.add(new HealingPowerUp(map.getHealingPowerupBlue(), 0, 45 * 20, 30));
        powerUps.add(new HealingPowerUp(map.getHealingPowerupRed(), 0, 45 * 20, 30));
        powerUps.add(new SpeedPowerUp(map.getSpeedPowerupBlue(), 10, 45 * 20, 30));
        powerUps.add(new SpeedPowerUp(map.getSpeedPowerupRed(), 10, 45 * 20, 30));
        for (AbstractPowerUp powerUp : powerUps) {
            powerUp.spawn();
        }
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
                            entitiesNear.get(0).sendMessage("§6You activated the §c§lDAMAGE §6powerup! §a+20% §6Damage for §a30 §6seconds!");

                        }
                    } else if (powerUp instanceof HealingPowerUp) {
                        Warlords.getPlayer((Player) entitiesNear.get(0)).setPowerUpHeal(true);
                        entitiesNear.get(0).sendMessage("§6You activated the §a§lHEALING §6powerup! §a+10% §6Health per second for §a10 §6seconds!");

                    } else if (powerUp instanceof SpeedPowerUp) {
                        Warlords.getPlayer((Player) entitiesNear.get(0)).setPowerUpSpeed(powerUp.getDuration());
                        entitiesNear.get(0).sendMessage("§6You activated the §e§lSPEED §6powerup! §a+40% §6Speed for §a10 §6seconds!");
                        warlordsPlayer.getSpeed().changeCurrentSpeed("Speed Powerup", 40, 10 * 20);

                        for (Player player1 : powerUp.getLocation().getWorld().getPlayers()) {
                            player1.playSound(powerUp.getLocation(), "ctf.powerup_speed", 1, 1);
                        }
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
