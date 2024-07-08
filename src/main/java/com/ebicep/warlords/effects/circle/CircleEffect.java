package com.ebicep.warlords.effects.circle;

import com.ebicep.warlords.effects.AbstractBaseAreaEffect;
import com.ebicep.warlords.effects.EffectPlayer;
import com.ebicep.warlords.effects.GameTeamContainer;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class CircleEffect extends AbstractBaseAreaEffect<EffectPlayer<? super CircleEffect>> {

    static final Random RANDOM = new Random();
    static final Location LOCATION_CACHE = new Location(null, 0, 0, 0);
    private double radius;
    @Nonnull
    final GameTeamContainer players;

    @SafeVarargs
    public CircleEffect(@Nonnull WarlordsEntity wp, @Nonnull Location center, double radius, EffectPlayer<? super CircleEffect>... effects) {
        this(wp.getGame(), wp.getTeam(), center, radius, effects);
    }

    @SafeVarargs
    public CircleEffect(@Nonnull Game game, @Nullable Team team, @Nonnull Location center, double radius, EffectPlayer<? super CircleEffect>... effects) {
        this(game, team, center, radius, 0, effects);
    }

    @SafeVarargs
    public CircleEffect(@Nonnull Game game, @Nullable Team team, @Nonnull Location center, double radius, double additionalYOffset, EffectPlayer<? super CircleEffect>... effects) {
        Validate.notNull(game, "game");
        Validate.notNull(center, "center");
        center.setY(center.getBlockY() + 0.02 + additionalYOffset);
        this.center = center;
        this.radius = radius;
        this.additionalYOffset = additionalYOffset;
        this.players = new GameTeamContainer(game, team);
        this.addEffects(effects);
    }

    public Team getTeam() {
        return players.getTeam();
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
