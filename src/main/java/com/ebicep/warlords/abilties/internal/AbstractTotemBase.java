package com.ebicep.warlords.abilties.internal;

import com.ebicep.warlords.abilties.DeathsDebt;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractTotemBase extends AbstractAbility {

    protected WarlordsPlayer owner;
    protected ArmorStand totem;

    public AbstractTotemBase(String name, float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
    }

    public AbstractTotemBase(String name, float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier, ArmorStand totem, WarlordsPlayer owner) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
        this.totem = totem;
        this.owner = owner;
    }

    protected abstract ItemStack getTotemItemStack();

    protected abstract void playSound(Player player, Location location);

    protected abstract void onActivation(WarlordsPlayer wp, Player player, ArmorStand totemStand);

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);

        Location standLocation = player.getLocation();
        standLocation.setYaw(0);
        standLocation.setY(getLocationUnderPlayer(player));

        playSound(player, standLocation);

        ArmorStand totemStand = player.getWorld().spawn(this instanceof DeathsDebt ? standLocation.clone().add(0, -.25, 0) : standLocation, ArmorStand.class);
        totemStand.setVisible(false);
        totemStand.setGravity(false);
        totemStand.setHelmet(getTotemItemStack());

        onActivation(wp, player, totemStand);

        return true;
    }


    public static Optional<AbstractTotemBase> getAnyTotemDownAndClose(WarlordsPlayer warlordsPlayer, Entity searchNearby) {
        List<Entity> entitiesAround = searchNearby.getNearbyEntities(5, 3, 5);
        return new CooldownFilter<>(warlordsPlayer, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(AbstractTotemBase.class)
                .filter(abstractTotemBase -> entitiesAround.contains(abstractTotemBase.getTotem()))
                .findFirst();
    }

    public static <T extends AbstractTotemBase> Optional<T> getTotemDownAndClose(WarlordsPlayer warlordsPlayer, Entity searchNearby, Class<T> clazz) {
        List<Entity> entitiesAround = searchNearby.getNearbyEntities(5, 3, 5);
        return new CooldownFilter<>(warlordsPlayer, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(clazz)
                .filter(abstractTotemBase -> entitiesAround.contains(abstractTotemBase.getTotem()))
                .findFirst();
    }

    public static <T extends AbstractTotemBase> List<T> getTotemsDownAndClose(WarlordsPlayer warlordsPlayer, Entity searchNearby, Class<T> clazz) {
        List<Entity> entitiesAround = searchNearby.getNearbyEntities(5, 3, 5);
        return new CooldownFilter<>(warlordsPlayer, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(clazz)
                .filter(abstractTotemBase -> entitiesAround.contains(abstractTotemBase.getTotem()))
                .collect(Collectors.toList());
    }


    private double getLocationUnderPlayer(Player player) {
        Location location = player.getLocation().clone();
        location.setY(location.getBlockY() + 2);
        for (int i = 0; i < 20; i++) {
            if (!player.getWorld().getBlockAt(location).getType().isSolid()) {
                location.add(0, -1, 0);
            } else {
                break;
            }
        }
        return location.getY();
    }

    public boolean isPlayerLookingAtTotem(WarlordsPlayer warlordsPlayer) {
        if (!(warlordsPlayer.getEntity() instanceof Player)) {
            return false;
        }
        Player player = (Player) warlordsPlayer.getEntity();
        Location eye = new LocationBuilder(player.getEyeLocation()).addY(.5).backward(1).get();
        Vector toEntity = this.totem.getEyeLocation().add(0, 1, 0).toVector().subtract(eye.toVector());
        float dot = (float) toEntity.normalize().dot(eye.getDirection());
        return dot > .93f;
    }

    public ArmorStand getTotem() {
        return totem;
    }
}
