package com.ebicep.warlords.abilties.internal;

import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.LineEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collections;

public abstract class AbstractBeaconAbility<T extends AbstractBeaconAbility<T>> extends AbstractAbility {

    protected Location groundLocation; // not static
    protected int radius;
    protected int tickDuration;

    public AbstractBeaconAbility(
            String name,
            float minDamageHeal,
            float maxDamageHeal,
            float cooldown,
            float energyCost,
            float critChance,
            float critMultiplier,
            Location groundLocation,
            int radius,
            int secondDuration
    ) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
        this.groundLocation = groundLocation;
        this.radius = radius;
        this.tickDuration = secondDuration * 20;
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Place a stationary beacon on the ground that lasts ")
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. "))
                               .append(getBonusDescription());
    }

    public abstract Component getBonusDescription();

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, Player player) {
        wp.subtractEnergy(energyCost, false);
        wp.getCooldownManager().limitCooldowns(RegularCooldown.class, AbstractBeaconAbility.class, 2);
        Location groundLocation = LocationUtils.getGroundLocation(player);
        CircleEffect teamCircleEffect = new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                groundLocation,
                radius,
                new CircumferenceEffect(Particle.VILLAGER_HAPPY, Particle.REDSTONE),
                getLineEffect(groundLocation)
        );
        ArmorStand beacon = Utils.spawnArmorStand(groundLocation.clone().add(0, -1.425, 0), armorStand -> armorStand.getEquipment().setHelmet(new ItemStack(Material.BEACON)));
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                getAbbreviation(),
                getBeaconClass(),
                getObject(groundLocation),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    beacon.remove();
                },
                false,
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    //particle effects
                    teamCircleEffect.playEffects();
                    whileActive(wp, cooldown, ticksLeft, ticksElapsed);
                })
        ));
        return true;
    }

    public abstract LineEffect getLineEffect(Location target);

    public abstract String getAbbreviation();

    public abstract Class<T> getBeaconClass();

    public abstract T getObject(Location groundLocation);

    public abstract void whileActive(@Nonnull WarlordsEntity wp, RegularCooldown<T> cooldown, Integer ticksLeft, Integer ticksElapsed);

    public abstract Material getGlassMaterial();

    public Location getGroundLocation() {
        return groundLocation;
    }

}
