package com.ebicep.warlords.classes.internal;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.abilties.DeathsDebt;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public abstract class AbstractTotemBase extends AbstractAbility {

    public AbstractTotemBase(String name, float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
    }

    protected abstract ItemStack getTotemItemStack();

    protected abstract void onTotemStand(ArmorStand totemStand, WarlordsPlayer wp);

    protected abstract void playSound(Player player, Location location);

    protected abstract void onActivation(WarlordsPlayer wp, Player player, ArmorStand totemStand);

    @Override
    public void onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);

        Location standLocation = player.getLocation();
        standLocation.setYaw(0);
        standLocation.setY(getLocationUnderPlayer(player));
        ArmorStand totemStand = player.getWorld().spawn(this instanceof DeathsDebt ? standLocation.clone().add(0, -.25, 0) : standLocation, ArmorStand.class);
        totemStand.setVisible(false);
        totemStand.setGravity(false);
        totemStand.setMarker(true);
        totemStand.setHelmet(getTotemItemStack());
        onTotemStand(totemStand, wp);

        playSound(player, standLocation);

        onActivation(wp, player, totemStand);
    }

    private double getLocationUnderPlayer(Player player) {
        Location location = player.getLocation().clone();
        location.setY(location.getBlockY() + 2);
        for (int i = 0; i < 20; i++) {
            if (player.getWorld().getBlockAt(location).getType() == Material.AIR) {
                location.add(0, -1, 0);
            } else {
                break;
            }
        }
        return location.getY();
    }

}
