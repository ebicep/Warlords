package com.ebicep.warlords.game.option.pve.darkdescent.objectives;

import com.ebicep.warlords.game.option.pve.darkdescent.DarkDescentOption;
import net.kyori.adventure.text.Component;

import java.util.concurrent.ThreadLocalRandom;

public interface DarkDescentObjective {

    static DarkDescentObjective random(DarkDescentOption option) {
        return switch (ThreadLocalRandom.current().nextInt(3)) {
            case 0 -> new DefendPointObjective(option);
            case 1 -> new LightOrbObjective(option);
            default -> new MiniBossObjective(option);
        };
    }

    Component getTitle();

    void tick();

    boolean isComplete();

    Component getScoreboardLine();

    default void cleanup() {
    }
}
