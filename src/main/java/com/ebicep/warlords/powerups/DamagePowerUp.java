package com.ebicep.warlords.powerups;

import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

public class DamagePowerUp extends AbstractPowerUp {

    public DamagePowerUp() {
        super(null, 0, 0, 0);
    }

    public DamagePowerUp(Location location, int duration, int cooldown, int timeToSpawn) {
        super(location, duration, cooldown, timeToSpawn);
    }

    @Override
    public void onPickUp(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.getCooldownManager().addCooldown("Damage", DamagePowerUp.class, this, "DMG", duration, warlordsPlayer, CooldownTypes.BUFF);
        warlordsPlayer.sendMessage("§6You activated the §c§lDAMAGE §6powerup! §a+20% §6Damage for §a30 §6seconds!");
    }

    @Override
    public void setNameAndItem(ArmorStand armorStand) {
        armorStand.setCustomName("§c§lDAMAGE");
        armorStand.setHelmet(new ItemStack(Material.WOOL, 1, (short) 14));
    }
}
