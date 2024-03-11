package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsStrikeEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractStrike extends AbstractAbility implements WeaponAbilityIcon, HitBox {

    private FloatModifiable hitbox = new FloatModifiable(4.8f);

    public AbstractStrike(String name, float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost, float critChance, float critMultiplier) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        AtomicBoolean hitPlayer = new AtomicBoolean(false);
        float radius = hitbox.getCalculatedValue();
        PlayerFilter.entitiesAround(wp, radius, radius, radius)
                    .aliveEnemiesOf(wp)
                    .closestFirst(wp)
                    .requireLineOfSight(wp)
                    .lookingAtFirst(wp)
                    .first(nearPlayer -> {
                        addTimesUsed();
                        AbstractPlayerClass.sendRightClickPacket(wp);
                        playSoundAndEffect(nearPlayer.getLocation());

                        boolean successfulStrike = onHit(wp, nearPlayer);
                        Bukkit.getPluginManager().callEvent(new WarlordsStrikeEvent(wp, this, nearPlayer));
                        hitPlayer.set(successfulStrike);
                    });

        return hitPlayer.get();
    }

    protected abstract void playSoundAndEffect(Location location);

    protected abstract boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull WarlordsEntity nearPlayer);

    public void knockbackOnHit(WarlordsEntity giver, WarlordsEntity kbTarget, double velocity, double y) {
        final Location loc = kbTarget.getLocation();
        final Vector v = giver.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-velocity).setY(y);
        kbTarget.setVelocity(name, v, false);
    }

    public void additionalHit(
            int additionalHitAmount,
            WarlordsEntity giver,
            WarlordsEntity initialTarget,
            float damageModifier,
            Function<WarlordsEntity, EnumSet<InstanceFlags>> getFlags,
            Consumer<Optional<WarlordsDamageHealingFinalEvent>> onFinalEvent
    ) {
        for (WarlordsEntity we : PlayerFilter
                .entitiesAround(initialTarget, 4, 4, 4)
                .aliveEnemiesOf(giver)
                .closestFirst(initialTarget)
                .excluding(initialTarget)
                .limit(additionalHitAmount)
        ) {
            EnumSet<InstanceFlags> flags = getFlags != null ? getFlags.apply(we) : EnumSet.noneOf(InstanceFlags.class);
            onFinalEvent.accept(we.addDamageInstance(
                    giver,
                    name,
                    minDamageHeal.getCalculatedValue() * damageModifier,
                    maxDamageHeal.getCalculatedValue() * damageModifier,
                    critChance,
                    critMultiplier,
                    flags
            ));
        }
    }

    protected void randomHitEffect(Location location, int particleAmount, int red, int green, int blue) {
        for (int i = 0; i < particleAmount; i++) {
            location.getWorld().spawnParticle(
                    Particle.REDSTONE,
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

    @Override
    public FloatModifiable getHitBoxRadius() {
        return hitbox;
    }
}
