package com.ebicep.warlords.effects.circle;

import com.ebicep.warlords.effects.AbstractEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.TeamBasedEffect;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.function.DoubleUnaryOperator;

import static com.ebicep.warlords.effects.circle.CircleEffect.LOCATION_CACHE;
import static com.ebicep.warlords.effects.circle.CircleEffect.RANDOM;

public class LineEffect extends AbstractEffectPlayer<CircleEffect> {

    private final DoubleUnaryOperator INITIAL_PARTICLES = i -> Math.sqrt(i) * 2;
    @Nonnull
    private TeamBasedEffect effect;
    @Nonnull
    private DoubleUnaryOperator particles = INITIAL_PARTICLES;
    private double pendingParticles = 0;
    private double period = 0.2;
    private double pendingPeriod = 0;

    private Location target;

    public LineEffect(Location target, ParticleEffect own, ParticleEffect other) {
        this(target, new TeamBasedEffect(own, other));
    }

    public LineEffect(Location target, ParticleEffect effect) {
        this(target, new TeamBasedEffect(effect));
    }

    public LineEffect(Location target, TeamBasedEffect effect) {
        this.target = target;
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
            double xA = target.getX();
            double yA = target.getY();
            double zA = target.getZ();
            double angleB = RANDOM.nextInt(360) * Math.PI / 180;
            double xB = center.getX() + radius * Math.sin(angleB);
            double yB = center.getY();
            double zB = center.getZ() + radius * Math.cos(angleB);
            double distanceSquared = (xA - xB) * (xA - xB) + (yA - yB) * (yA - yB) + (zA - zB) * (zA - zB);

            double newPoints = pendingParticles + this.particles.applyAsDouble(distanceSquared);
            int maxNewPoints = (int) newPoints;
            pendingParticles = newPoints - maxNewPoints;
            // spread out `maxNewPoints` over the line
            double maxNewPointsMinusOne = maxNewPoints - 1;
            for (int j = 0; j < maxNewPoints; j++) {
                double factorA = j / maxNewPointsMinusOne;
                double factorB = 1 - (j / maxNewPointsMinusOne);
                LOCATION_CACHE.setX(xA * factorA + xB * factorB);
                LOCATION_CACHE.setY(yA * factorA + yB * factorB);
                LOCATION_CACHE.setZ(zA * factorA + zB * factorB);
                effect.display(baseData.players, 0, 0, 0, 0, 1, LOCATION_CACHE);
            }
        }
    }

    public Location getTarget() {
        return target;
    }

    public LineEffect target(Location target) {
        this.target = target;
        return this;
    }

    @Override
    public void updateCachedData(CircleEffect baseData) {
        this.needsUpdate = false;
    }

    public LineEffect effect(@Nonnull ParticleEffect effect) {
        this.effect = new TeamBasedEffect(effect);
        return this;
    }

    public LineEffect effect(@Nonnull ParticleEffect ownTeam, @Nonnull ParticleEffect enemyTeam) {
        this.effect = new TeamBasedEffect(ownTeam, enemyTeam);
        return this;
    }

    public LineEffect particles(double particles) {
        this.particles = d -> particles;
        return this;
    }

    public LineEffect particlesPerBlock(double particles) {
        this.particles = d -> Math.sqrt(d) * particles;
        return this;
    }

    public double getPeriod() {
        return period;
    }

    public LineEffect period(double period) {
        this.period = period;
        return this;
    }
}
