package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.LocationUtils;
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

public abstract class AbstractTotem extends AbstractAbility implements OrangeAbilityIcon {

    public ArmorStand getTotem() {
        return totem;
    }

    public static <T extends TotemData<?>> Optional<T> getTotemDownAndClose(WarlordsEntity warlordsPlayer, Entity searchNearby, Class<T> clazz) {
        List<Entity> entitiesAround = searchNearby.getNearbyEntities(5, 3, 5);
        return new CooldownFilter<>(warlordsPlayer, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(clazz)
                .filter(data -> entitiesAround.contains(data.getArmorStand()))
                .findFirst();
    }

    public static <T extends TotemData<?>> List<T> getTotemsDownAndClose(WarlordsEntity warlordsPlayer, Entity searchNearby, Class<T> clazz) {
        List<Entity> entitiesAround = searchNearby.getNearbyEntities(5, 3, 5);
        return new CooldownFilter<>(warlordsPlayer, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(clazz)
                .filter(data -> entitiesAround.contains(data.getArmorStand()))
                .toList();
    }

    protected WarlordsEntity owner;
    protected ArmorStand totem;

    public AbstractTotem(String name, float cooldown, float energyCost) {
        super(name, cooldown, energyCost);
    }

    public AbstractTotem(
            String name,
            float cooldown,
            float energyCost,
            ArmorStand totem,
            WarlordsEntity owner
    ) {
        super(name, cooldown, energyCost);
        this.totem = totem;
        this.owner = owner;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        Location standLocation = LocationUtils.getGroundLocation(wp.getLocation());
        standLocation.setYaw(0);
        standLocation.setY(standLocation.getY() - 0.46);

        playSound(wp, standLocation);

        ArmorStand totemStand = Utils.spawnArmorStand(standLocation, armorStand -> {
            armorStand.getEquipment().setHelmet(getTotemItemStack());
            armorStand.setSmall(true);
        });

        onActivation(wp, totemStand);

        return true;
    }

    protected abstract void playSound(WarlordsEntity warlordsEntity, Location location);

    protected abstract ItemStack getTotemItemStack();

    protected abstract void onActivation(WarlordsEntity wp, ArmorStand totemStand);

    public static abstract class TotemData<T extends AbstractTotem> {

        protected final T totem;
        protected WarlordsEntity owner;
        protected ArmorStand armorStand;

        public TotemData(T totem, WarlordsEntity owner, ArmorStand armorStand) {
            this.totem = totem;
            this.owner = owner;
            this.armorStand = armorStand;
        }

        public T getTotem() {
            return totem;
        }

        public WarlordsEntity getOwner() {
            return owner;
        }

        public ArmorStand getArmorStand() {
            return armorStand;
        }

        public boolean playerOutsideTotem(WarlordsEntity warlordsEntity, float radius) {
            return warlordsEntity.getLocation().distanceSquared(armorStand.getLocation()) > radius * radius;
        }

        public boolean isPlayerLookingAtTotem(WarlordsEntity warlordsPlayer) {
            if (!(warlordsPlayer.getEntity() instanceof Player player)) {
                return false;
            }
            Location eye = new LocationBuilder(player.getEyeLocation()).addY(.46).backward(1);
            Vector toEntity = this.armorStand.getEyeLocation().add(0, 0, 0).toVector().subtract(eye.toVector());
            float dot = (float) toEntity.normalize().dot(eye.getDirection());
            return dot > .93f;
        }


    }

}
