package com.ebicep.warlords.effects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class TeamBasedEffect {

    private static void display(
            Particle particle,
            double offsetX,
            double offsetY,
            double offsetZ,
            double speed,
            int amount,
            Location center,
            Iterable<? extends Player> players
    ) {
        for (Player player : players) {
            player.spawnParticle(particle, center, amount, offsetX, offsetY, offsetZ, speed, null);
        }
    }

    @Nonnull
    final Particle ownTeam;
    @Nonnull
    final Particle enemyTeam;

    public TeamBasedEffect(@Nonnull Particle effect) {
        this(effect, effect);
    }

    public TeamBasedEffect(@Nonnull Particle ownTeam, @Nonnull Particle enemyTeam) {
        this.ownTeam = ownTeam;
        this.enemyTeam = enemyTeam;
    }

    public void display(GameTeamContainer teams, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center) {
        display(ownTeam, offsetX, offsetY, offsetZ, speed, amount, center, teams.getAllyPlayers().toList());
        display(enemyTeam, offsetX, offsetY, offsetZ, speed, amount, center, teams.getEnemyPlayers().toList());
    }

    public void display(GameTeamContainer teams, Vector direction, float speed, Location center) {
        display(ownTeam, direction.getX(), direction.getY(), direction.getZ(), speed, 0, center, teams.getAllyPlayers().toList());
        display(enemyTeam, direction.getX(), direction.getY(), direction.getZ(), speed, 0, center, teams.getEnemyPlayers().toList());
    }

//    public void display(GameTeamContainer teams, Particle color, Location center) {
//        ownTeam.display(color, center, teams.getAllyPlayers().toList());
//        enemyTeam.display(color, center, teams.getEnemyPlayers().toList());
//    }
//
//    public void display(
//            GameTeamContainer teams,
//            Particle.ParticleData data,
//            float offsetX,
//            float offsetY,
//            float offsetZ,
//            float speed,
//            int amount,
//            Location center
//    ) {
//        ownTeam.display(data, offsetX, offsetY, offsetZ, speed, amount, center, teams.getAllyPlayers().toList());
//        enemyTeam.display(data, offsetX, offsetY, offsetZ, speed, amount, center, teams.getEnemyPlayers().toList());
//    }
//
//    public void display(GameTeamContainer teams, Particle.ParticleData data, Vector direction, float speed, Location center) {
//        ownTeam.display(data, direction, speed, center, teams.getAllyPlayers().toList());
//        enemyTeam.display(data, direction, speed, center, teams.getEnemyPlayers().toList());
//    }

    @Override
    public String toString() {
        return "TeamBasedEffect{" + "ownTeam=" + ownTeam + ", enemyTeam=" + enemyTeam + '}';
    }

}
