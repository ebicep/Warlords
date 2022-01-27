package com.ebicep.warlords.powerups;

import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpeedPowerUp extends AbstractPowerUp {

    public SpeedPowerUp() {
        super(null, 0, 0, 0);
    }

    public SpeedPowerUp(Location location, int duration, int cooldown, int timeToSpawn) {
        super(location, duration, cooldown, timeToSpawn);
    }

    @Override
    public void onPickUp(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.getCooldownManager().addRegularCooldown("Speed", "SPEED", SpeedPowerUp.class, this, warlordsPlayer, CooldownTypes.BUFF, cooldownManager -> {
            warlordsPlayer.sendMessage("§6Your §e§lSPEED §6powerup has worn off!");
        }, duration * 20);
        warlordsPlayer.sendMessage("§6You activated the §e§lSPEED §6powerup! §a+40% §6Speed for §a10 §6seconds!");
        warlordsPlayer.getSpeed().addSpeedModifier("Speed Powerup", 40, 10 * 20, "BASE");

        for (Player player1 : powerUp.getLocation().getWorld().getPlayers()) {
            player1.playSound(powerUp.getLocation(), "ctf.powerup.speed", 2, 1);
        }
    }

    @Override
    public void setNameAndItem(ArmorStand armorStand) {
        armorStand.setCustomName("§b§lSPEED");
        armorStand.setHelmet(new ItemStack(Material.WOOL, 1, (short) 4));
    }
}
