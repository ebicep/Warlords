package com.ebicep.warlords.game.option.wavedefense.mobs;

import com.ebicep.warlords.game.option.wavedefense.mobs.blaze.Blaze;
import com.ebicep.warlords.game.option.wavedefense.mobs.bosses.Void;
import com.ebicep.warlords.game.option.wavedefense.mobs.bosses.*;
import com.ebicep.warlords.game.option.wavedefense.mobs.events.boltarobonanza.EventBoltaro;
import com.ebicep.warlords.game.option.wavedefense.mobs.irongolem.IronGolem;
import com.ebicep.warlords.game.option.wavedefense.mobs.magmacube.MagmaCube;
import com.ebicep.warlords.game.option.wavedefense.mobs.pigzombie.BasicPigZombie;
import com.ebicep.warlords.game.option.wavedefense.mobs.pigzombie.ElitePigZombie;
import com.ebicep.warlords.game.option.wavedefense.mobs.pigzombie.EnvoyPigZombie;
import com.ebicep.warlords.game.option.wavedefense.mobs.pigzombie.VoidPigZombie;
import com.ebicep.warlords.game.option.wavedefense.mobs.skeleton.*;
import com.ebicep.warlords.game.option.wavedefense.mobs.slime.BasicSlime;
import com.ebicep.warlords.game.option.wavedefense.mobs.slime.VoidSlime;
import com.ebicep.warlords.game.option.wavedefense.mobs.spider.Spider;
import com.ebicep.warlords.game.option.wavedefense.mobs.witch.Witch;
import com.ebicep.warlords.game.option.wavedefense.mobs.wolf.Wolf;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.*;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.berserkzombie.BasicBerserkZombie;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.berserkzombie.EliteBerserkZombie;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.berserkzombie.EnvoyBerserkZombie;
import org.bukkit.Location;

import java.util.function.Function;

public enum Mobs {
    // Base
    BASIC_ZOMBIE(BasicZombie::new),
    BASIC_BERSERK_ZOMBIE(BasicBerserkZombie::new),
    BASIC_SKELETON(BasicSkeleton::new),
    BASIC_PIG_ZOMBIE(BasicPigZombie::new),
    BASIC_SLIME(BasicSlime::new),
    SPIDER(Spider::new),
    WOLF(Wolf::new),

    // Elite Tier 1 - Elite
    ELITE_ZOMBIE(EliteZombie::new),
    GHOST_ZOMBIE(GhostZombie::new),
    SLIME_ZOMBIE(SlimeZombie::new),
    ELITE_BERSERK_ZOMBIE(EliteBerserkZombie::new),
    ELITE_SKELETON(EliteSkeleton::new),
    ELITE_PIG_ZOMBIE(ElitePigZombie::new),
    MAGMA_CUBE(MagmaCube::new),
    IRON_GOLEM(IronGolem::new),
    BLAZE(Blaze::new),
    WITCH(Witch::new),

    // Elite Tier 2 - Envoy
    ENVOY_ZOMBIE(EnvoyZombie::new),
    ENVOY_SKELETON(EnvoySkeleton::new),
    ENVOY_PIG_ZOMBIE(EnvoyPigZombie::new),
    ENVOY_BERSERKER_ZOMBIE(EnvoyBerserkZombie::new),

    // Elite Tier 3 - Void
    VOID_ZOMBIE(VoidZombie::new),
    VOID_SKELETON(VoidSkeleton::new),
    VOID_PIG_ZOMBIE(VoidPigZombie::new),
    VOID_SLIME(VoidSlime::new),

    // Elite Tier 4 - Exiled
    EXILED_ZOMBIE(ExiledZombie::new),
    EXILED_SKELETON(ExiledSkeleton::new),
    EXILED_VOID_LANCER(NetheriteZombie::new),
    EXILED_ZOMBIE_RIFT(RiftZombie::new),
    EXILED_ZOMBIE_LAVA(LavaZombie::new),

    // Elite Tier 5 - Forgotten
    FORGOTTEN_ZOMBIE(ForgottenZombie::new),
    FORGOTTEN_LANCER(OvergrownZombie::new),

    // Boss
    BOLTARO(Boltaro::new),
    GHOULCALLER(Ghoulcaller::new),
    NARMER(Narmer::new),
    MITHRA(Mithra::new),
    ZENITH(Zenith::new),
    ILLUMINA(Illumina::new),
    CHESSKING(Chessking::new),
    VOID(Void::new),

    // Raid Boss
    PHYSIRA(Physira::new),

    //EVENTS
    EVENT_BOLTARO_BONANZA(EventBoltaro::new),

    ;

    public final Function<Location, AbstractMob<?>> createMob;

    Mobs(Function<Location, AbstractMob<?>> createMob) {
        this.createMob = createMob;
    }
}
