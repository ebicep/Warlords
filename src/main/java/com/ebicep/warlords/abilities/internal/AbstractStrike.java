package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.events.player.ingame.WarlordsStrikeEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public abstract class AbstractStrike<T extends AbstractStrike<T, R>, R extends AbstractStrike.AbstractStrikeStats<T, R>> extends AbstractAbility implements WeaponAbilityIcon, HitBox, AbilityStats<T, R> {

    public static void giveStrikePriority(WarlordsEntity from, WarlordsEntity target, int tickDuration) {
        target.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Strike Priority",
                null,
                null,
                null,
                from,
                CooldownTypes.INTERNAL,
                cooldownManager -> {
                },
                tickDuration
        ));
    }

    private FloatModifiable hitbox = new FloatModifiable(4.8f);

    public AbstractStrike(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.hitbox = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("hitBox"), float.class));
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        AtomicBoolean hitPlayer = new AtomicBoolean(false);
        float radius = hitbox.getCalculatedValue();
        PlayerFilter.entitiesAround(wp, radius, radius, radius)
                    .aliveEnemiesOf(wp)
                    .closestFirst(wp)
                    .requireLineOfSight(wp)
                    .lookingAtFirst(wp)
                    .sorted((w1, w2) -> {
                        Optional<RegularCooldown> w1StrikePriority = new CooldownFilter<>(w1, RegularCooldown.class)
                                .filterCooldownName("Strike Priority")
                                .filterCooldownFrom(wp)
                                .findAny();
                        Optional<RegularCooldown> w2StrikePriority = new CooldownFilter<>(w2, RegularCooldown.class)
                                .filterCooldownName("Strike Priority")
                                .filterCooldownFrom(wp)
                                .findAny();
                        if (w1StrikePriority.isPresent() && w2StrikePriority.isPresent()) {
                            return 0;
                        } else if (w1StrikePriority.isPresent()) {
                            return -1;
                        } else if (w2StrikePriority.isPresent()) {
                            return 1;
                        }
                        return 0;
                    })
                    .first(nearPlayer -> {
                        AbstractPlayerClass.sendRightClickPacket(wp);
                        playSoundAndEffect(nearPlayer.getLocation());

                        boolean successfulStrike = onHit(wp, nearPlayer);
                        Bukkit.getPluginManager().callEvent(new WarlordsStrikeEvent(wp, this, nearPlayer));
                        hitPlayer.set(successfulStrike);
                        if (successfulStrike) {
                            getAbilityStats().playersStruck++;
                        }
                    });

        return hitPlayer.get();
    }

    protected abstract void playSoundAndEffect(Location location);

    protected abstract boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull WarlordsEntity nearPlayer);

    @Override
    public FloatModifiable getHitBoxRadius() {
        return hitbox;
    }

    public void knockbackOnHit(WarlordsEntity giver, WarlordsEntity kbTarget, double velocity, double y) {
        final Location loc = kbTarget.getLocation();
        final Vector v = giver.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-velocity).setY(y);
        kbTarget.setVelocity(name, v, false);
    }

    public void additionalHit(
            int additionalHitAmount,
            WarlordsEntity giver,
            WarlordsEntity initialTarget,
            Consumer<WarlordsEntity> onHit
    ) {
        PlayerFilter.entitiesAround(initialTarget, 4, 4, 4)
                    .aliveEnemiesOf(giver)
                    .closestFirst(initialTarget)
                    .excluding(initialTarget)
                    .limit(additionalHitAmount)
                    .forEach(warlordsEntity -> {
                        onHit.accept(warlordsEntity);
                        getAbilityStats().playersStruck++;
                    });
    }

    protected void randomHitEffect(Location location, int particleAmount, int red, int green, int blue) {
        for (int i = 0; i < particleAmount; i++) {
            location.getWorld().spawnParticle(
                    Particle.DUST,
                    location.clone().add((Math.random() * 2) - 1, 1.2 + (Math.random() * 2) - 1, (Math.random() * 2) - 1),
                    1,
                    0,
                    0,
                    0,
                    0,
                    new Particle.DustOptions(Color.fromRGB(red, green, blue), 1),
                    true
            );
        }
    }

    public static abstract class AbstractStrikeStats<T extends AbstractStrike<T, R>, R extends AbstractStrikeStats<T, R>> extends AbstractAbilityStats<T, R> {

        @Field("targets_struck")
        protected int playersStruck = 0;

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Struck", playersStruck));
            return statsDisplay;
        }

        @Override
        public R merge(R other, int multiplier) {
            R r = super.merge(other, multiplier);
            r.playersStruck += other.playersStruck * multiplier;
            return r;
        }

    }

}
