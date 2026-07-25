package com.ebicep.warlords.effects;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TeamBasedEffect {

    @Nonnull
    final Particle ownTeam;
    @Nonnull
    final Particle enemyTeam;
    @Nullable
    final Object ownTeamData;
    @Nullable
    final Object enemyTeamData;

    public TeamBasedEffect(@Nonnull Particle effect) {
        this(effect, (Object) null);
    }

    public TeamBasedEffect(@Nonnull Particle ownTeam, @Nonnull Particle enemyTeam) {
        this(ownTeam, null, enemyTeam, null);
    }

    public TeamBasedEffect(@Nonnull Particle effect, @Nullable Object data) {
        this(effect, data, effect, data);
    }

    public TeamBasedEffect(@Nonnull Particle ownTeam, @Nullable Object ownTeamData, @Nonnull Particle enemyTeam, @Nullable Object enemyTeamData) {
        this.ownTeam = ownTeam;
        this.ownTeamData = ownTeam == Particle.DUST && ownTeamData == null ? new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1) : ownTeamData;
        this.enemyTeam = enemyTeam;
        this.enemyTeamData = enemyTeam == Particle.DUST && enemyTeamData == null ? new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1) : enemyTeamData;
    }

    public void display(List<Player> allies, List<Player> enemies, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center) {
        display(ownTeam, ownTeamData, offsetX, offsetY, offsetZ, speed, amount, center, allies);
        display(enemyTeam, enemyTeamData, offsetX, offsetY, offsetZ, speed, amount, center, enemies);
    }

    private static <T> void display(
            Particle particle,
            T data,
            double offsetX,
            double offsetY,
            double offsetZ,
            double speed,
            int amount,
            Location center,
            Iterable<? extends Player> players
    ) {
        for (Player player : players) {
            EffectUtils.displayParticle(player, particle, center, amount, offsetX, offsetY, offsetZ, speed, data);
        }
    }

    public void display(List<Player> allies, List<Player> enemies, Vector direction, float speed, Location center) {
        display(ownTeam, ownTeamData, direction.getX(), direction.getY(), direction.getZ(), speed, 0, center, allies);
        display(enemyTeam, enemyTeamData, direction.getX(), direction.getY(), direction.getZ(), speed, 0, center, enemies);
    }

    @Override
    public String toString() {
        return "TeamBasedEffect{" + "ownTeam=" + ownTeam + ", enemyTeam=" + enemyTeam + '}';
    }

}
