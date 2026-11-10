package com.ebicep.warlords.game.option.pve.darkdescent.objectives;

import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.darkdescent.DarkDescentOption;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;

public class DefendPointObjective implements DarkDescentObjective {

    private static final int DURATION_TICKS = 30 * GameRunnable.SECOND;
    private static final double RADIUS = 3;

    private final DarkDescentOption option;
    private final Location point;
    private final CircleEffect circleEffect;
    private int ticksDefended;

    public DefendPointObjective(DarkDescentOption option) {
        this.option = option;
        this.point = option.randomArenaLocation(18);
        this.circleEffect = new CircleEffect(option.getGame(), Team.BLUE, point, RADIUS, new CircumferenceEffect(Particle.END_ROD));
    }

    @Override
    public Component getTitle() {
        return Component.text("Defend the point for 30 seconds", NamedTextColor.YELLOW);
    }

    @Override
    public void tick() {
        if (isComplete()) {
            return;
        }
        if (option.getTicksElapsed() % 5 == 0) {
            circleEffect.playEffects();
        }
        boolean defending = option.getGame()
                                  .warlordsPlayers()
                                  .anyMatch(warlordsPlayer -> warlordsPlayer.isAlive() && warlordsPlayer.getLocation().distanceSquared(point) <= RADIUS * RADIUS);
        if (defending) {
            ticksDefended++;
        }
    }

    @Override
    public boolean isComplete() {
        return ticksDefended >= DURATION_TICKS;
    }

    @Override
    public Component getScoreboardLine() {
        return Component.text("Point Defended: ")
                        .append(Component.text(ticksDefended / GameRunnable.SECOND + "/" + DURATION_TICKS / GameRunnable.SECOND + "s",
                                isComplete() ? NamedTextColor.GREEN : NamedTextColor.YELLOW
                        ));
    }
}
