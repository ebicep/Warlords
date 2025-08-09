package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.HammerOfLight;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityTargetEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public abstract class AbstractHolyRadiance extends AbstractAbility implements BlueAbilityIcon, HitBox, AbilityStats<AbstractHolyRadiance, AbstractHolyRadiance.AbstractHolyRadianceStats> {

    private final AbstractHolyRadianceStats stats = new AbstractHolyRadianceStats();
    private FloatModifiable radius;
    private FloatModifiable speed;

    public AbstractHolyRadiance(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.radius = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radius"), float.class));
        this.speed = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("speed"), float.class));
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Value.RangedValueCritable radianceHealing = getRadianceHealing();
        wp.addInstance(InstanceBuilder
                .healing()
                .ability(this)
                .source(wp)
                .value(radianceHealing)
        );

        List<WarlordsEntity> chained = chain(wp);
        stats.playersMarked += chained.size();
        for (WarlordsEntity warlordsEntity : chained) {
            if (warlordsEntity.isTeammate(wp)) {
                heal(wp, warlordsEntity);
            }
        }

        float rad = radius.getCalculatedValue();
        Set<WarlordsEntity> warlordsEntities = PlayerFilter
                .entitiesAround(wp, rad, rad, rad)
                .aliveTeammatesOfExcludingSelf(wp)
                .excluding(chained)
                .stream()
                .collect(Collectors.toSet());
        for (WarlordsEntity radianceTarget : warlordsEntities) {
            new FlyingArmorStand(
                    wp.getGame(),
                    wp.getLocation(),
                    radianceTarget,
                    wp,
                    speed.getCalculatedValue(),
                    this::heal
            ).runTaskTimer(1, 1);
        }
        Bukkit.getPluginManager().callEvent(new WarlordsAbilityTargetEvent.WarlordsBlueAbilityTargetEvent(wp, name, warlordsEntities));

        wp.playSound(wp.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        Utils.playGlobalSound(wp.getLocation(), "paladin.holyradiance.activation", 2, 1);

        Location particleLoc = wp.getLocation().add(0, 1.2, 0);

        EffectUtils.displayParticle(Particle.HAPPY_VILLAGER, particleLoc, 2, 1, 1, 1, 0.1);
        EffectUtils.displayParticle(Particle.EFFECT, particleLoc, 12, 1, 1, 1, 0.06);

        return true;
    }

    @Override
    public void runEveryTick(@Nullable WarlordsEntity warlordsEntity) {
        speed.tick();
        super.runEveryTick(warlordsEntity);
    }

    public abstract Value.RangedValueCritable getRadianceHealing();

    public abstract List<WarlordsEntity> chain(WarlordsEntity wp);

    public void heal(WarlordsEntity owner, WarlordsEntity target) {
        target.addInstance(InstanceBuilder
                .healing()
                .ability(this)
                .source(owner)
                .value(getRadianceHealing())
        ).ifPresent(warlordsDamageHealingFinalEvent -> {
            stats.playersHealed++;
            new CooldownFilter<>(owner, RegularCooldown.class)
                    .filterCooldownFrom(owner)
                    .filterCooldownClassAndMapToObjectsOfClass(HammerOfLight.HammerOfLightData.class)
                    .forEach(hammerOfLight -> hammerOfLight.addAmountHealed(warlordsDamageHealingFinalEvent.getValue()));
        });
    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return radius;
    }

    @Override
    public AbstractHolyRadianceStats getAbilityStats() {
        return stats;
    }

    public FloatModifiable getSpeed() {
        return speed;
    }

    public static class AbstractHolyRadianceStats extends AbstractAbilityStats<AbstractHolyRadiance, AbstractHolyRadianceStats> {

        @Field("targets_healed")
        private int playersHealed = 0;
        @Field("targets_marked")
        private int playersMarked = 0;

        @Override
        public Class<AbstractHolyRadianceStats> getClazz() {
            return AbstractHolyRadianceStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Healed", String.valueOf(playersHealed)));
            statsDisplay.add(new AbilityStatDisplay("Targets Marked", String.valueOf(playersMarked)));
            return statsDisplay;
        }

        @Override
        public AbstractHolyRadianceStats merge(AbstractHolyRadianceStats other, int multiplier) {
            AbstractHolyRadianceStats stats = super.merge(other, multiplier);
            stats.playersHealed = this.playersHealed + other.playersHealed * multiplier;
            stats.playersMarked = this.playersMarked + other.playersMarked * multiplier;
            return stats;
        }

        @Override
        public AbstractHolyRadianceStats create() {
            return new AbstractHolyRadianceStats();
        }

    }

    public static class FlyingArmorStand extends GameRunnable {

        private final WarlordsEntity target;
        private final WarlordsEntity owner;
        private final double speed;
        private final ArmorStand armorStand;
        private final BiConsumer<WarlordsEntity, WarlordsEntity> healFunction;

        public FlyingArmorStand(
                Game game,
                Location location,
                WarlordsEntity target,
                WarlordsEntity owner,
                double speed,
                BiConsumer<WarlordsEntity, WarlordsEntity> healFunction
        ) {
            super(game);
            this.armorStand = Utils.spawnArmorStand(location);
            this.target = target;
            this.speed = speed;
            this.owner = owner;
            this.healFunction = healFunction;
        }

        @Override
        public void run() {
            if (this.target.isDead()) {
                this.cancel();
                return;
            }

            if (target.getWorld() != armorStand.getWorld()) {
                this.cancel();
                return;
            }

            Location targetLocation = target.getLocation();
            Location armorStandLocation = armorStand.getLocation();
            double distance = targetLocation.distanceSquared(armorStandLocation);

            if (distance < speed * speed) {
                healFunction.accept(owner, target);
                this.cancel();
                return;
            }

            targetLocation.subtract(armorStandLocation);
            //System.out.println(Math.max(speed * 3.25 / targetLocation.lengthSquared() / 2, speed / 10));
            targetLocation.multiply(Math.max(speed * 3.25 / targetLocation.lengthSquared() / 2, speed / 10));

            armorStandLocation.add(targetLocation);
            this.armorStand.teleport(armorStandLocation);

            EffectUtils.displayParticle(Particle.EFFECT, armorStandLocation.add(0, 1.75, 0), 2, 0.01, 0, 0.01, 0.1);
        }

        @Override
        public void cancel() {
            super.cancel();
            armorStand.remove();
        }

    }

}
