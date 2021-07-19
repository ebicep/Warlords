package com.ebicep.warlords.effects.circle;

import com.ebicep.warlords.effects.AbstractBaseAreaEffect;
import com.ebicep.warlords.effects.EffectPlayer;
import com.ebicep.warlords.effects.GameTeamContainer;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.Random;

public class CircleEffect extends AbstractBaseAreaEffect<EffectPlayer<? super CircleEffect>> {

    static final Random RANDOM = new Random();
    static final Location LOCATION_CACHE = new Location(null, 0, 0, 0);
    private double radius;
    @Nonnull
    final GameTeamContainer players;

    public CircleEffect(@Nonnull WarlordsPlayer wp, @Nonnull Location center, double radius) {
        this(wp.getGame(), wp.getTeam(), center, radius);
    }

    public CircleEffect(@Nonnull Game game, @Nonnull Team team, @Nonnull Location center, double radius) {
        Validate.notNull(game, "game");
        Validate.notNull(team, "team");
        Validate.notNull(center, "center");
        center.setY(center.getBlockY() + 0.01);
        this.center = center;
        this.radius = radius;
        this.players = new GameTeamContainer(game, team);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
        for (EffectPlayer<? super CircleEffect> effect : this) {
            effect.updateCachedData(this);
        }
    }

    @Override
    public void playEffects() {
        LOCATION_CACHE.setWorld(center.getWorld());
        for (EffectPlayer<? super CircleEffect> effect : this) {
            if (effect.needsUpdate()) {
                effect.updateCachedData(this);
            }
            effect.playEffect(this);
        }
    }

}
