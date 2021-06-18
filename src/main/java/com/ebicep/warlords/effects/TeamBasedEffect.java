package com.ebicep.warlords.effects;

import com.ebicep.warlords.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class TeamBasedEffect {

    @Nonnull
    final ParticleEffect ownTeam;
    @Nonnull
    final ParticleEffect enemyTeam;

    public TeamBasedEffect(@Nonnull ParticleEffect effect) {
        this(effect, effect);
    }

    public TeamBasedEffect(@Nonnull ParticleEffect ownTeam, @Nonnull ParticleEffect enemyTeam) {
        this.ownTeam = ownTeam;
        this.enemyTeam = enemyTeam;
    }

    public void display(GameTeamContainer teams, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center) {
        ownTeam.display(offsetX, offsetY, offsetZ, speed, amount, center, teams.getAllyPlayers());
        enemyTeam.display(offsetX, offsetY, offsetZ, speed, amount, center, teams.getEnemyPlayers());
    }

    public void display(GameTeamContainer teams, Vector direction, float speed, Location center) {
        ownTeam.display(direction, speed, center, teams.getAllyPlayers());
        enemyTeam.display(direction, speed, center, teams.getEnemyPlayers());
    }

    public void display(GameTeamContainer teams, ParticleEffect.ParticleColor color, Location center) {
        ownTeam.display(color, center, teams.getAllyPlayers());
        enemyTeam.display(color, center, teams.getEnemyPlayers());
    }

    public void display(GameTeamContainer teams, ParticleEffect.ParticleData data, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center) {
        ownTeam.display(data, offsetX, offsetY, offsetZ, speed, amount, center, teams.getAllyPlayers());
        enemyTeam.display(data, offsetX, offsetY, offsetZ, speed, amount, center, teams.getEnemyPlayers());
    }

    public void display(GameTeamContainer teams, ParticleEffect.ParticleData data, Vector direction, float speed, Location center) {
        ownTeam.display(data, direction, speed, center, teams.getAllyPlayers());
        enemyTeam.display(data, direction, speed, center, teams.getEnemyPlayers());
    }

    @Override
    public String toString() {
        return "TeamBasedEffect{" + "ownTeam=" + ownTeam + ", enemyTeam=" + enemyTeam + '}';
    }

}
