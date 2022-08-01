package com.ebicep.warlords.game.option.wavedefense2.mobs2;

import com.ebicep.warlords.game.option.wavedefense2.mobs2.mobs.zombie.BasicZombie;
import com.ebicep.warlords.game.option.wavedefense2.mobs2.mobs.zombie.Narmer;
import org.bukkit.Location;

import java.util.function.Function;

public enum Mobs {

    BASIC_ZOMBIE(BasicZombie::new),
    NARMER(Narmer::new),

    ;

    public final Function<Location, AbstractMob<?>> createMob;

    Mobs(Function<Location, AbstractMob<?>> createMob) {
        this.createMob = createMob;
    }
}
