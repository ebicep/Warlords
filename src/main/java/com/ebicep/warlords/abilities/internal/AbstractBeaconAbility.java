package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.LineEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;

public abstract class AbstractBeaconAbility<T extends AbstractBeaconAbility<T>> extends AbstractAbility implements Duration, HitBox {

    protected Location groundLocation; // not static
    protected CircleEffect effect; // not static
    protected FloatModifiable radius; // not static
    protected int tickDuration;
    private int maxBeaconsAtATime = 1;

    public AbstractBeaconAbility(
            String name,
            float minDamageHeal,
            float maxDamageHeal,
            float cooldown,
            float energyCost,
            float critChance,
            float critMultiplier,
            Location groundLocation,
            float radius,
            int secondDuration,
            CircleEffect effect
    ) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
        this.groundLocation = groundLocation;
        this.radius = new FloatModifiable(radius);
        this.tickDuration = secondDuration * 20;
        this.effect = effect;
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
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        wp.getCooldownManager().limitCooldowns(RegularCooldown.class, AbstractBeaconAbility.class, maxBeaconsAtATime);
        Location groundLocation = LocationUtils.getGroundLocation(wp.getLocation());

        Utils.playGlobalSound(groundLocation, "arcanist.beacon.impact", 0.3f, 1);
        Utils.playGlobalSound(groundLocation, "arcanist.beaconshadow.activation", 2, 1);

        CircleEffect teamCircleEffect = new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                groundLocation,
                radius.getCalculatedValue(),
                new CircumferenceEffect(Particle.VILLAGER_HAPPY, Particle.REDSTONE),
                getLineEffect(groundLocation)
        );

        ArmorStand beacon = Utils.spawnArmorStand(
                groundLocation.clone().add(0, -1.425, 0),
                armorStand -> armorStand.getEquipment().setHelmet(new ItemStack(Material.BEACON))
        );

        new GameRunnable(wp.getGame()) {
            int interval = 4;
            @Override
            public void run() {
                interval--;
                EffectUtils.playSphereAnimation(
                        beacon.getLocation(),
                        2.5 + interval,
                        150,
                        80,
                        80
                );

                if (interval <= 0) {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 2);

        Utils.playGlobalSound(beacon.getLocation(), Sound.AMBIENT_SOUL_SAND_VALLEY_MOOD, 0.2f, 0.5f);

        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                getAbbreviation(),
                getBeaconClass(),
                getObject(groundLocation, teamCircleEffect),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    beacon.remove();
                    if (getCrystal() != null) {
                        getCrystal().remove();
                    }
                },
                false,
                tickDuration + 1,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    //particle effects
                    teamCircleEffect.playEffects();
                    whileActive(wp, cooldown, ticksLeft, ticksElapsed);
                })
        ));
        return true;
    }

    @Override
    public void runEveryTick(@Nullable WarlordsEntity warlordsEntity) {
        radius.tick();
        super.runEveryTick(warlordsEntity);
    }

    public abstract LineEffect getLineEffect(Location target);

    public abstract String getAbbreviation();

    public abstract Class<T> getBeaconClass();

    public abstract T getObject(Location groundLocation, CircleEffect effect);

    public abstract void whileActive(@Nonnull WarlordsEntity wp, RegularCooldown<T> cooldown, Integer ticksLeft, Integer ticksElapsed);

    public abstract ArmorStand getCrystal();

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public Location getGroundLocation() {
        return groundLocation;
    }

    public FloatModifiable getHitBoxRadius() {
        return radius;
    }

    public void setMaxBeaconsAtATime(int maxBeaconsAtATime) {
        this.maxBeaconsAtATime = maxBeaconsAtATime;
    }
}
