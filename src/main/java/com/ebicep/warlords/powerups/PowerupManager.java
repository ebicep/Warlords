package com.ebicep.warlords.powerups;

import com.ebicep.warlords.maps.GameMap;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.entity.Player;
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
                List<WarlordsPlayer> entitiesNear = PlayerFilter.entitiesAround(powerUp.getLocation(), 1.25, 1.25, 1.25).isAlive().stream().collect(Collectors.toList());
                if (entitiesNear.size() != 0) {
                    WarlordsPlayer warlordsPlayer = entitiesNear.get(0);
                    if (powerUp instanceof DamagePowerUp) {
                        warlordsPlayer.getCooldownManager().addCooldown(DamagePowerUp.class, this, "DMG", powerUp.getDuration(), warlordsPlayer, CooldownTypes.BUFF);
                        entitiesNear.get(0).sendMessage("§6You activated the §c§lDAMAGE §6powerup! §a+20% §6Damage for §a30 §6seconds!");
                    } else if (powerUp instanceof HealingPowerUp) {
                        warlordsPlayer.setPowerUpHeal(true);
                        entitiesNear.get(0).sendMessage("§6You activated the §a§lHEALING §6powerup! §a+10% §6Health per second for §a10 §6seconds!");
                    } else if (powerUp instanceof SpeedPowerUp) {
                        warlordsPlayer.getCooldownManager().addCooldown(SpeedPowerUp.class, this, "SPEED", powerUp.getDuration(), warlordsPlayer, CooldownTypes.BUFF);
                        entitiesNear.get(0).sendMessage("§6You activated the §e§lSPEED §6powerup! §a+40% §6Speed for §a10 §6seconds!");
                        warlordsPlayer.getSpeed().addSpeedModifier("Speed Powerup", 40, 10 * 20, "BASE");

                        for (Player player1 : powerUp.getLocation().getWorld().getPlayers()) {
                            player1.playSound(powerUp.getLocation(), "ctf.powerup.speed", 2, 1);
                        }
                    }
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
