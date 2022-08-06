package com.ebicep.warlords.game.option.wavedefense.mobs;

import com.ebicep.warlords.game.option.wavedefense.mobs.bosses.*;
import com.ebicep.warlords.game.option.wavedefense.mobs.irongolem.IronGolem;
import com.ebicep.warlords.game.option.wavedefense.mobs.magmacube.MagmaCube;
import com.ebicep.warlords.game.option.wavedefense.mobs.pigzombie.BasicPigZombie;
import com.ebicep.warlords.game.option.wavedefense.mobs.pigzombie.ElitePigZombie;
import com.ebicep.warlords.game.option.wavedefense.mobs.pigzombie.EnvoyPigZombie;
import com.ebicep.warlords.game.option.wavedefense.mobs.skeleton.BasicSkeleton;
import com.ebicep.warlords.game.option.wavedefense.mobs.skeleton.EliteSkeleton;
import com.ebicep.warlords.game.option.wavedefense.mobs.skeleton.EnvoySkeleton;
import com.ebicep.warlords.game.option.wavedefense.mobs.slime.BasicSlime;
import com.ebicep.warlords.game.option.wavedefense.mobs.spider.Spider;
import com.ebicep.warlords.game.option.wavedefense.mobs.wolf.Wolf;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.BasicZombie;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.EliteZombie;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.EnvoyZombie;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.VoidZombie;
import org.bukkit.Location;

import java.util.function.Function;

public enum Mobs {
    // Base
    BASIC_ZOMBIE(BasicZombie::new),
    BASIC_SKELETON(BasicSkeleton::new),
    BASIC_PIG_ZOMBIE(BasicPigZombie::new),
    BASIC_SLIME(BasicSlime::new),
    SPIDER(Spider::new),
    WOLF(Wolf::new),

    // Elite Tier 2
    ELITE_ZOMBIE(EliteZombie::new),
    ELITE_SKELETON(EliteSkeleton::new),
    ELITE_PIG_ZOMBIE(ElitePigZombie::new),
    MAGMA_CUBE(MagmaCube::new),
    IRON_GOLEM(IronGolem::new),

    // Elite Tier 2 - Envoy
    ENVOY_ZOMBIE(EnvoyZombie::new),
    ENVOY_SKELETON(EnvoySkeleton::new),
    ENVOY_PIG_ZOMBIE(EnvoyPigZombie::new),

    // Elite Tier 3 - Void
    VOID_ZOMBIE(VoidZombie::new),

    // Boss
    NARMER(Narmer::new),
    BOLTARO(Boltaro::new),
    MITHRA(Mithra::new),
    PHYSIRA(Physira::new),
    ZENITH(Zenith::new)

    ;

    public final Function<Location, AbstractMob<?>> createMob;

    Mobs(Function<Location, AbstractMob<?>> createMob) {
        this.createMob = createMob;
    }
}
