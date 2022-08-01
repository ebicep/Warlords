package com.ebicep.warlords.game.option.wavedefense.mobs;

import com.ebicep.warlords.game.option.wavedefense.mobs.irongolem.IronGolem;
import com.ebicep.warlords.game.option.wavedefense.mobs.pigzombie.BasicPigZombie;
import com.ebicep.warlords.game.option.wavedefense.mobs.pigzombie.ElitePigZombie;
import com.ebicep.warlords.game.option.wavedefense.mobs.skeleton.BasicSkeleton;
import com.ebicep.warlords.game.option.wavedefense.mobs.slime.BasicSlime;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.BasicZombie;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.EliteZombie;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.Narmer;
import org.bukkit.Location;

import java.util.function.Function;

public enum Mobs {
    // Base
    BASIC_ZOMBIE(BasicZombie::new),
    BASIC_SKELETON(BasicSkeleton::new),
    BASIC_PIG_ZOMBIE(BasicPigZombie::new),
    BASIC_SLIME(BasicSlime::new),

    // Elite
    ELITE_ZOMBIE(EliteZombie::new),
    ELITE_PIG_ZOMBIE(ElitePigZombie::new),
    IRON_GOLEM(IronGolem::new),

    // Boss
    NARMER(Narmer::new),

    ;

    public final Function<Location, AbstractMob<?>> createMob;

    Mobs(Function<Location, AbstractMob<?>> createMob) {
        this.createMob = createMob;
    }
}
