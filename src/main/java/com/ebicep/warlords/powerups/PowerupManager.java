package com.ebicep.warlords.powerups;

import com.ebicep.warlords.maps.GameMap;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PowerupManager extends BukkitRunnable {

    private final List<AbstractPowerUp> powerUps = new ArrayList<>();
    private final GameMap map;

    public PowerupManager(GameMap map) {
        this.map = map;
        powerUps.add(new DamagePowerUp(map.getDamagePowerupBlue(), 30, 45 * 20, 60));
        powerUps.add(new DamagePowerUp(map.getDamagePowerupRed(), 30, 45 * 20, 60));
        powerUps.add(new HealingPowerUp(map.getHealingPowerupBlue(), 0, 45 * 20, 60));
        powerUps.add(new HealingPowerUp(map.getHealingPowerupRed(), 0, 45 * 20, 60));
        powerUps.add(new SpeedPowerUp(map.getSpeedPowerupBlue(), 10, 45 * 20, 60));
        powerUps.add(new SpeedPowerUp(map.getSpeedPowerupRed(), 10, 45 * 20, 60));
        for (AbstractPowerUp powerUp : powerUps) {
            powerUp.spawn();
        }
    }

    @Override
    public void run() {
        for (AbstractPowerUp powerUp : powerUps) {
            if (powerUp.getCooldown() == 0) {
                PlayerFilter.entitiesAround(powerUp.getLocation(), 1.4, 1.4, 1.4)
                        .isAlive()
                        .first((nearPlayer) -> {
                            powerUp.onPickUp(nearPlayer);
                            powerUp.getPowerUp().remove();
                            powerUp.setCooldown(powerUp.getMaxCooldown());
                        });
            } else {
                powerUp.setCooldown(powerUp.getCooldown() - 1);
                if (powerUp.getCooldown() == 0) {
                    powerUp.spawn();
                }
            }
        }
    }
}
