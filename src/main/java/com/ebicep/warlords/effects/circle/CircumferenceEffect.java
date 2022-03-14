package com.ebicep.warlords.effects.circle;

import com.ebicep.warlords.effects.AbstractEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.TeamBasedEffect;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.function.DoubleUnaryOperator;

import static com.ebicep.warlords.effects.circle.CircleEffect.LOCATION_CACHE;
import static com.ebicep.warlords.effects.circle.CircleEffect.RANDOM;

public class CircumferenceEffect extends AbstractEffectPlayer<CircleEffect> {

    private final DoubleUnaryOperator INITIAL_PARTICLES = i -> Math.PI * 2 * i * 0.05;
    @Nonnull
    private TeamBasedEffect effect;
    @Nonnull
    private DoubleUnaryOperator particles = INITIAL_PARTICLES;
    private double cachedParticles;
    private double pendingParticles;

    public CircumferenceEffect(ParticleEffect own, ParticleEffect other) {
        this(new TeamBasedEffect(own, other));
    }

    public CircumferenceEffect(ParticleEffect effect) {
        this(new TeamBasedEffect(effect));
    }

    public CircumferenceEffect(TeamBasedEffect effect) {
        this.effect = effect;
    }


    @Override
    public void playEffect(CircleEffect baseData) {
        Location center = baseData.getCenter();
        double radius = baseData.getRadius();
        LOCATION_CACHE.setY(center.getY());

        double newParticles = pendingParticles + cachedParticles;
        int maxCircleParticles = (int) newParticles;
        pendingParticles = newParticles - maxCircleParticles;
        for (int i = 0; i < maxCircleParticles; i++) {
            double angle = RANDOM.nextInt(360) * Math.PI / 180;
            LOCATION_CACHE.setX(radius * Math.sin(angle) + center.getX());
            LOCATION_CACHE.setZ(radius * Math.cos(angle) + center.getZ());
            LOCATION_CACHE.setY(center.getY());
            do {
                LOCATION_CACHE.setY(LOCATION_CACHE.getY() - 1);
            } while (LOCATION_CACHE.getY() >= 0 && !LOCATION_CACHE.getBlock().getType().isOccluding());
            LOCATION_CACHE.setY(LOCATION_CACHE.getY() + 1);
            effect.display(baseData.players, 0, 0, 0, 0, 1, LOCATION_CACHE);
        }
    }

    @Override
    public void updateCachedData(CircleEffect baseData) {
        cachedParticles = particles.applyAsDouble(baseData.getRadius());
        needsUpdate = false;
    }


    public CircumferenceEffect effect(@Nonnull ParticleEffect effect) {
        this.effect = new TeamBasedEffect(effect);
        return this;
    }

    public CircumferenceEffect effect(@Nonnull ParticleEffect ownTeam, @Nonnull ParticleEffect enemyTeam) {
        this.effect = new TeamBasedEffect(ownTeam, enemyTeam);
        return this;
    }

    public CircumferenceEffect particles(double particles) {
        this.particles = d -> particles;
        this.needsUpdate = true;
        return this;
    }

    public CircumferenceEffect particlesPerCircumference(double particles) {
        this.particles = d -> Math.PI * 2 * d * particles;
        this.needsUpdate = true;
        return this;
    }
}
