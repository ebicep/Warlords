package com.ebicep.warlords.abilties.internal;

import com.ebicep.warlords.abilties.Consecrate;
import com.ebicep.warlords.abilties.HammerOfLight;
import com.ebicep.warlords.abilties.ProtectorsStrike;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsStrikeEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractStrikeBase extends AbstractAbility {

    public static Optional<Consecrate> getStandingOnConsecrate(WarlordsEntity owner, WarlordsEntity standing) {
        return new CooldownFilter<>(owner, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(Consecrate.class)
                .filter(consecrate -> consecrate.getLocation().distanceSquared(standing.getLocation()) < consecrate.getRadius() * consecrate.getRadius())
                .max(Comparator.comparingInt(Consecrate::getStrikeDamageBoost));
    }

    private double hitbox = 4.8;

    public AbstractStrikeBase(String name, float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost, float critChance, float critMultiplier) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        AtomicBoolean hitPlayer = new AtomicBoolean(false);
        PlayerFilter.entitiesAround(wp, hitbox, hitbox, hitbox)
                .aliveEnemiesOf(wp)
                .closestFirst(wp)
                .requireLineOfSight(wp)
                .lookingAtFirst(wp)
                .first((nearPlayer) -> {
                    if (Utils.isLookingAt(wp.getEntity(), nearPlayer.getEntity()) && Utils.hasLineOfSight(wp.getEntity(), nearPlayer.getEntity())) {
                        addTimesUsed();
                        AbstractPlayerClass.sendRightClickPacket(player);
                        playSoundAndEffect(nearPlayer.getLocation());

                        boolean successfulStrike = onHit(wp, player, nearPlayer);
                        Bukkit.getPluginManager().callEvent(new WarlordsStrikeEvent(wp, this, nearPlayer));
                        if (this instanceof ProtectorsStrike) {
                            Optional<HammerOfLight> optionalHammerOfLight = new CooldownFilter<>(wp, RegularCooldown.class)
                                    .filterCooldownClassAndMapToObjectsOfClass(HammerOfLight.class)
                                    .findAny();
                            if (optionalHammerOfLight.isPresent()) {
                                wp.subtractEnergy(energyCost - (optionalHammerOfLight.get().isCrownOfLight() ? 10 : 0), false);
                            } else {
                                wp.subtractEnergy(energyCost, false);
                            }
                        } else {
                            wp.subtractEnergy(energyCost, false);
                        }
                        hitPlayer.set(successfulStrike);
                    }
                });

        return hitPlayer.get();
    }

    protected abstract void playSoundAndEffect(Location location);

    protected abstract boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull Player player, @Nonnull WarlordsEntity nearPlayer);

    public void knockbackOnHit(WarlordsEntity giver, WarlordsEntity kbTarget, double velocity, double y) {
        final Location loc = kbTarget.getLocation();
        final Vector v = giver.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-velocity).setY(y);
        kbTarget.setVelocity(name, v, false);
    }

    public void tripleHit(WarlordsEntity giver, WarlordsEntity initialTarget) {
        for (WarlordsEntity we : PlayerFilter
                .entitiesAround(initialTarget, 4, 4, 4)
                .aliveEnemiesOf(giver)
                .closestFirst(initialTarget)
                .excluding(initialTarget)
                .limit(2)
        ) {
            we.addDamageInstance(
                    giver,
                    name,
                    minDamageHeal,
                    maxDamageHeal,
                    critChance,
                    critMultiplier,
                    false
            );
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

    public double getHitbox() {
        return hitbox;
    }

    public void setHitbox(double hitbox) {
        this.hitbox = hitbox;
    }
}
