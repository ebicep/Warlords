package com.ebicep.warlords.effects.circle;

import com.ebicep.warlords.effects.AbstractEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.TeamBasedEffect;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.function.DoubleUnaryOperator;

import static com.ebicep.warlords.effects.circle.CircleEffect.LOCATION_CACHE;
import static com.ebicep.warlords.effects.circle.CircleEffect.RANDOM;

public final class AreaEffect extends AbstractEffectPlayer<CircleEffect> {

    private final DoubleUnaryOperator INITIAL_PARTICLES = i -> Math.PI * i * i * 0.1;
    @Nonnull
    private TeamBasedEffect effect;
    @Nonnull
    private DoubleUnaryOperator particles = INITIAL_PARTICLES;
    private double cachedParticles;
    private double pendingParticles;
    private double yOffset;

    public AreaEffect(double yOffset, ParticleEffect own, ParticleEffect other) {
        this(yOffset, new TeamBasedEffect(own, other));
    }

    public AreaEffect(double yOffset, ParticleEffect effect) {
        this(yOffset, new TeamBasedEffect(effect));
    }

    public AreaEffect(double yOffset, TeamBasedEffect effect) {
        this.yOffset = yOffset;
        this.effect = effect;
    }

    @Override
    public void playEffect(CircleEffect baseData) {
        Location center = baseData.getCenter();
        double radius = baseData.getRadius();
        LOCATION_CACHE.setY(center.getY() + yOffset);

        double newParticles = pendingParticles + cachedParticles;
        int maxParticles = (int) newParticles;
        pendingParticles = newParticles - maxParticles;
        for (int i = 0; i < maxParticles; i++) {
            double x;
            double z;
            double distanceSquared;
            do {
                x = RANDOM.nextDouble() * radius * 2 - radius;
                z = RANDOM.nextDouble() * radius * 2 - radius;
                distanceSquared = x * x + z * z;
            } while (distanceSquared > radius * radius);

            LOCATION_CACHE.setX(x + center.getX());
            LOCATION_CACHE.setZ(z + center.getZ());
            this.effect.display(baseData.players, 0, 0, 0, 0.01F, 1, LOCATION_CACHE);
        }
    }

    @Override
    public void updateCachedData(CircleEffect baseData) {
        cachedParticles = particles.applyAsDouble(baseData.getRadius());
        needsUpdate = false;
    }

    public AreaEffect effect(@Nonnull ParticleEffect effect) {
        this.effect = new TeamBasedEffect(effect);
        return this;
    }

    public AreaEffect effect(@Nonnull ParticleEffect ownTeam, @Nonnull ParticleEffect enemyTeam) {
        this.effect = new TeamBasedEffect(ownTeam, enemyTeam);
        return this;
    }

    public AreaEffect particles(double particles) {
        this.particles = d -> particles;
        this.needsUpdate = true;
        return this;
    }

    public AreaEffect particlesPerSurface(double particles) {
        this.particles = d -> Math.PI * d * d * particles;
        this.needsUpdate = true;
        return this;
    }

    public double getyOffset() {
        return yOffset;
    }

    public AreaEffect yOffset(double yOffset) {
        this.yOffset = yOffset;
        return this;
    }
}
