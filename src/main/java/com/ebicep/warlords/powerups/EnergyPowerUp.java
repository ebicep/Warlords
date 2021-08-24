package com.ebicep.warlords.powerups;

import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

public class EnergyPowerUp extends AbstractPowerUp {

    public EnergyPowerUp() {
        super(null, 0, 0, 0);
    }

    public EnergyPowerUp(Location location, int duration, int cooldown, int timeToSpawn) {
        super(location, duration, cooldown, timeToSpawn);
    }

    @Override
    public void onPickUp(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.getCooldownManager().addCooldown("Energy", EnergyPowerUp.class, this, "ENERGY", duration, warlordsPlayer, CooldownTypes.BUFF);
        warlordsPlayer.sendMessage("§6You activated the §lENERGY §6powerup! §a+40% §6Energy gain for §a30 §6seconds!");
    }

    @Override
    public void setNameAndItem(ArmorStand armorStand) {
        armorStand.setCustomName("§6§lENERGY");
        armorStand.setHelmet(new ItemStack(Material.WOOL, 1, (short) 1));
    }
}
