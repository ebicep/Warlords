package com.ebicep.customentities.nms;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomHorse extends net.minecraft.world.entity.animal.horse.Horse {

    private final WarlordsEntity warlordsEntityOwner;
    private final int cooldown = 15;
    private final float speed = .318f;

    public CustomHorse(ServerLevel serverLevel, WarlordsEntity warlordsEntityOwner) {
        super(net.minecraft.world.entity.EntityType.HORSE, serverLevel);
        this.warlordsEntityOwner = warlordsEntityOwner;
    }

    public void spawn() {
        if (!(warlordsEntityOwner.getEntity() instanceof Player player)) {
            return;
        }
        Horse horse = (Horse) player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
        horse.setTamed(true);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        horse.setOwner(player);
        horse.setJumpStrength(0);
        horse.setColor(Horse.Color.BROWN);
        horse.setStyle(Horse.Style.NONE);
        horse.setAdult();
        AttributeInstance attribute = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (attribute == null) {
            horse.registerAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
            attribute = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        }
        attribute.setBaseValue(speed);
        horse.addPassenger(player);
    }

    public WarlordsEntity getWarlordsOwner() {
        return warlordsEntityOwner;
    }

    public int getCooldown() {
        return cooldown;
    }
}
