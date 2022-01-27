package com.ebicep.warlords.powerups;

import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

public class HealingPowerUp extends AbstractPowerUp {

    public HealingPowerUp() {
        super(null, 0, 0, 0);
    }

    public HealingPowerUp(Location location, int duration, int cooldown, int timeToSpawn) {
        super(location, duration, cooldown, timeToSpawn);
    }

    @Override
    public void onPickUp(WarlordsPlayer warlordsPlayer) {
        //warlordsPlayer.setPowerUpHeal(true);
        warlordsPlayer.getCooldownManager().addRegularCooldown("Healing", "HEAL", HealingPowerUp.class, this, warlordsPlayer, CooldownTypes.BUFF, cooldownManager -> {
            warlordsPlayer.cancelHealingPowerUp();
        }, duration * 20);
        warlordsPlayer.sendMessage("§6You activated the §a§lHEALING §6powerup! §a+8% §6Health per second for §a5 §6seconds!");
        warlordsPlayer.setPowerUpHeal(true);
    }

    @Override
    public void setNameAndItem(ArmorStand armorStand) {
        armorStand.setCustomName("§a§lHEALING");
        armorStand.setHelmet(new ItemStack(Material.WOOL, 1, (short) 13));
    }
}
