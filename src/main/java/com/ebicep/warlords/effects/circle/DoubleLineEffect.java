package com.ebicep.warlords.effects.circle;

import com.ebicep.warlords.effects.AbstractEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.TeamBasedEffect;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.function.DoubleUnaryOperator;

import static com.ebicep.warlords.effects.circle.CircleEffect.LOCATION_CACHE;
import static com.ebicep.warlords.effects.circle.CircleEffect.RANDOM;

public class DoubleLineEffect extends AbstractEffectPlayer<CircleEffect> {

    private final DoubleUnaryOperator INITIAL_PARTICLES = i -> Math.sqrt(i) * 1.5;
    @Nonnull
    private TeamBasedEffect effect;
    @Nonnull
    private DoubleUnaryOperator particles = INITIAL_PARTICLES;
    private double pendingParticles = 0;
    private double period = 0.2;
    private double pendingPeriod = 0;

    public DoubleLineEffect(ParticleEffect own, ParticleEffect other) {
        this(new TeamBasedEffect(own, other));
    }

    public DoubleLineEffect(ParticleEffect effect) {
        this(new TeamBasedEffect(effect));
    }

    public DoubleLineEffect(TeamBasedEffect effect) {
        this.effect = effect;
    }

    @Override
    public void playEffect(CircleEffect baseData) {
        Location center = baseData.getCenter();
        double radius = baseData.getRadius();
        LOCATION_CACHE.setY(center.getY());

        double newLines = pendingPeriod + period;
        int maxNewLines = (int) newLines;
        pendingPeriod = newLines - maxNewLines;
        for (int i = 0; i < maxNewLines; i++) {
            double angleA;
            double angleB;
            double difference;
            do {
                angleA = RANDOM.nextInt(360) * Math.PI / 180;
                angleB = RANDOM.nextInt(360) * Math.PI / 180;
                difference = Math.abs(angleA - angleB);
                if (difference > Math.PI) {
                    difference = Math.PI * 2 - difference;
                }
            } while (difference < Math.PI / 4); // 45 in degrees

            double xA = radius * Math.sin(angleA);
            double zA = radius * Math.cos(angleA);
            double xB = radius * Math.sin(angleB);
            double zB = radius * Math.cos(angleB);
            double distanceSquared = (xA - xB) * (xA - xB) + (zA - zB) * (zA - zB);

            double newPoints = pendingParticles + this.particles.applyAsDouble(distanceSquared);
            int maxNewPoints = (int) newPoints;
            pendingParticles = newPoints - maxNewPoints;
            // spread out `maxNewPoints` over the line
            double maxNewPointsMinusOne = maxNewPoints - 1;
            for (int j = 0; j < maxNewPoints; j++) {
                double factorA = j / maxNewPointsMinusOne;
                double factorB = 1 - (j / maxNewPointsMinusOne);
                LOCATION_CACHE.setX(xA * factorA + xB * factorB + center.getX());
                LOCATION_CACHE.setZ(zA * factorA + zB * factorB + center.getZ());
                effect.display(baseData.players, 0, 0, 0, 0, 1, LOCATION_CACHE);
            }
        }
    }

    @Override
    public void updateCachedData(CircleEffect baseData) {
        this.needsUpdate = false;
    }

    public DoubleLineEffect effect(@Nonnull ParticleEffect effect) {
        this.effect = new TeamBasedEffect(effect);
        return this;
    }

    public DoubleLineEffect effect(@Nonnull ParticleEffect ownTeam, @Nonnull ParticleEffect enemyTeam) {
        this.effect = new TeamBasedEffect(ownTeam, enemyTeam);
        return this;
    }

    public DoubleLineEffect particles(double particles) {
        this.particles = d -> particles;
        return this;
    }

    public DoubleLineEffect particlesPerBlock(double particles) {
        this.particles = d -> Math.sqrt(d) * particles;
        return this;
    }

    public double getPeriod() {
        return period;
    }

    public DoubleLineEffect period(double period) {
        this.period = period;
        return this;
    }
}
