package com.ebicep.warlords.abilties.internal;

import com.ebicep.warlords.abilties.DeathsDebt;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public abstract class AbstractTotem extends AbstractAbility {

    public static Optional<AbstractTotem> getAnyTotemDownAndClose(WarlordsEntity warlordsPlayer, Entity searchNearby) {
        List<Entity> entitiesAround = searchNearby.getNearbyEntities(5, 3, 5);
        return new CooldownFilter<>(warlordsPlayer, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(AbstractTotem.class)
                .filter(abstractTotemBase -> entitiesAround.contains(abstractTotemBase.getTotem()))
                .findFirst();
    }

    public ArmorStand getTotem() {
        return totem;
    }

    public static <T extends AbstractTotem> Optional<T> getTotemDownAndClose(WarlordsEntity warlordsPlayer, Entity searchNearby, Class<T> clazz) {
        List<Entity> entitiesAround = searchNearby.getNearbyEntities(5, 3, 5);
        return new CooldownFilter<>(warlordsPlayer, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(clazz)
                .filter(abstractTotemBase -> entitiesAround.contains(abstractTotemBase.getTotem()))
                .findFirst();
    }

    public static <T extends AbstractTotem> List<T> getTotemsDownAndClose(WarlordsEntity warlordsPlayer, Entity searchNearby, Class<T> clazz) {
        List<Entity> entitiesAround = searchNearby.getNearbyEntities(5, 3, 5);
        return new CooldownFilter<>(warlordsPlayer, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(clazz)
                .filter(abstractTotemBase -> entitiesAround.contains(abstractTotemBase.getTotem()))
                .toList();
    }

    protected WarlordsEntity owner;
    protected ArmorStand totem;

    public AbstractTotem(String name, float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost, float critChance, float critMultiplier) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
    }

    public AbstractTotem(
            String name,
            float minDamageHeal,
            float maxDamageHeal,
            float cooldown,
            float energyCost,
            float critChance,
            float critMultiplier,
            ArmorStand totem,
            WarlordsEntity owner
    ) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
        this.totem = totem;
        this.owner = owner;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);

        Location standLocation = player.getLocation();
        standLocation.setYaw(0);
        standLocation.setY(getLocationUnderPlayer(player));

        playSound(player, standLocation);

        ArmorStand totemStand = Utils.spawnArmorStand(this instanceof DeathsDebt ? standLocation.clone().add(0, -.25, 0) : standLocation, armorStand -> {
            armorStand.getEquipment().setHelmet(getTotemItemStack());
        });

        onActivation(wp, player, totemStand);

        return true;
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

    protected abstract void playSound(Player player, Location location);

    protected abstract ItemStack getTotemItemStack();

    protected abstract void onActivation(WarlordsEntity wp, Player player, ArmorStand totemStand);

    public boolean isPlayerLookingAtTotem(WarlordsEntity warlordsPlayer) {
        if (!(warlordsPlayer.getEntity() instanceof Player player)) {
            return false;
        }
        Location eye = new LocationBuilder(player.getEyeLocation()).addY(.5).backward(1);
        Vector toEntity = this.totem.getEyeLocation().add(0, 1, 0).toVector().subtract(eye.toVector());
        float dot = (float) toEntity.normalize().dot(eye.getDirection());
        return dot > .93f;
    }
}
