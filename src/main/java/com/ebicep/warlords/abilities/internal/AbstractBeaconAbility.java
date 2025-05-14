package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.database.repositories.config.ConfigManager;
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
import java.util.Collections;

public abstract class AbstractBeaconAbility<T extends AbstractBeaconAbility<T, R>, R extends AbstractBeaconAbility.BeaconData> extends AbstractAbility implements Duration, HitBox {

    protected FloatModifiable radius;
    protected int tickDuration;
    private int maxBeaconsAtATime = 2;

    public AbstractBeaconAbility(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.radius = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radius"), float.class));
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Place a stationary beacon on the ground that lasts ")
                .durationTicks(tickDuration)
                .text(". ")
                .append(getBonusDescription())
                .emptyLine()
                .text("Up to ")
                .text(maxBeaconsAtATime, NamedTextColor.BLUE)
                .text(" beacons can be present on the field at once.")
                .build();
    }

    public abstract Component getBonusDescription();

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {

        wp.getCooldownManager().limitCooldowns(RegularCooldown.class, getDataClass(), maxBeaconsAtATime);
        Location groundLocation = LocationUtils.getGroundLocation(wp.getLocation());

        Utils.playGlobalSound(groundLocation, "arcanist.beacon.impact", 0.3f, 1);
        Utils.playGlobalSound(groundLocation, "arcanist.beaconshadow.activation", 2, 1);

        CircleEffect teamCircleEffect = new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                groundLocation,
                radius.getCalculatedValue(),
                new CircumferenceEffect(Particle.HAPPY_VILLAGER, Particle.DUST),
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

        R dataObject = getDataObject(wp, beacon, groundLocation, teamCircleEffect, radius.getCalculatedValue());
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                getAbbreviation(),
                getDataClass(),
                dataObject,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    beacon.remove();
                    onRemove(dataObject);
                },
                false,
                tickDuration + 1,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    //particle effects
                    if ((inPve && ticksElapsed % 3 == 0) || (!inPve && ticksElapsed % 2 == 0)) {
                        teamCircleEffect.playEffects();
                    }
                    whileActive(wp, cooldown, ticksLeft, ticksElapsed);
                })
        ));
        return true;
    }

    public abstract Class<R> getDataClass();

    public abstract LineEffect getLineEffect(Location target);

    public abstract R getDataObject(WarlordsEntity wp, ArmorStand beacon, Location groundLocation, CircleEffect effect, float radius);

    public abstract String getAbbreviation();

    protected void onRemove(R data) {

    }

    public abstract void whileActive(@Nonnull WarlordsEntity wp, RegularCooldown<R> cooldown, Integer ticksLeft, Integer ticksElapsed);

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return radius;
    }

    public void setMaxBeaconsAtATime(int maxBeaconsAtATime) {
        this.maxBeaconsAtATime = maxBeaconsAtATime;
    }

    public static class BeaconData {

        private final ArmorStand beacon;
        private final Location groundLocation;
        private final CircleEffect effect;
        private final FloatModifiable radius;

        public BeaconData(ArmorStand beacon, Location groundLocation, CircleEffect effect, float radius) {
            this.beacon = beacon;
            this.groundLocation = groundLocation;
            this.effect = effect;
            this.radius = new FloatModifiable(radius);
        }

        public Location getGroundLocation() {
            return groundLocation;
        }

        public ArmorStand getBeacon() {
            return beacon;
        }

        public CircleEffect getEffect() {
            return effect;
        }

        public FloatModifiable getRadius() {
            return radius;
        }

    }

}
