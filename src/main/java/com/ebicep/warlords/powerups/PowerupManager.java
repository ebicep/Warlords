package com.ebicep.warlords.powerups;

import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.GameMap;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PowerupManager extends BukkitRunnable {

    private final List<AbstractPowerUp> powerUps = new ArrayList<>();
    private final Game game;

    public PowerupManager(Game game) {
        this.game = game;
        powerUps.add(new DamagePowerUp(game.getMap().getDamagePowerupBlue(), 30, 45 * 20, 60));
        powerUps.add(new DamagePowerUp(game.getMap().getDamagePowerupRed(), 30, 45 * 20, 60));
        powerUps.add(new HealingPowerUp(game.getMap().getHealingPowerupBlue(), 0, 45 * 20, 60));
        powerUps.add(new HealingPowerUp(game.getMap().getHealingPowerupRed(), 0, 45 * 20, 60));
        powerUps.add(new SpeedPowerUp(game.getMap().getSpeedPowerupBlue(), 10, 45 * 20, 60));
        powerUps.add(new SpeedPowerUp(game.getMap().getSpeedPowerupRed(), 10, 45 * 20, 60));
        for (AbstractPowerUp powerUp : powerUps) {
            powerUp.spawn();
        }
    }

    @Override
    public void run() {
        if(!game.isGameFreeze()) {
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
}
