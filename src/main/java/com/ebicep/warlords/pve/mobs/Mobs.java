package com.ebicep.warlords.pve.mobs;

import com.ebicep.warlords.pve.mobs.blaze.Blaze;
import com.ebicep.warlords.pve.mobs.bosses.Void;
import com.ebicep.warlords.pve.mobs.bosses.*;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.BoltaroExiled;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.BoltaroShadow;
import com.ebicep.warlords.pve.mobs.events.boltarobonanza.EventBoltaro;
import com.ebicep.warlords.pve.mobs.events.pharaohsrevenge.EventDjer;
import com.ebicep.warlords.pve.mobs.events.pharaohsrevenge.EventDjet;
import com.ebicep.warlords.pve.mobs.events.pharaohsrevenge.EventNarmer;
import com.ebicep.warlords.pve.mobs.events.pharaohsrevenge.EventNarmerAcolyte;
import com.ebicep.warlords.pve.mobs.events.spidersburrow.*;
import com.ebicep.warlords.pve.mobs.irongolem.IronGolem;
import com.ebicep.warlords.pve.mobs.magmacube.MagmaCube;
import com.ebicep.warlords.pve.mobs.pigzombie.BasicPigZombie;
import com.ebicep.warlords.pve.mobs.pigzombie.ElitePigZombie;
import com.ebicep.warlords.pve.mobs.pigzombie.EnvoyPigZombie;
import com.ebicep.warlords.pve.mobs.pigzombie.VoidPigZombie;
import com.ebicep.warlords.pve.mobs.skeleton.*;
import com.ebicep.warlords.pve.mobs.slime.BasicSlime;
import com.ebicep.warlords.pve.mobs.slime.VoidSlime;
import com.ebicep.warlords.pve.mobs.spider.Spider;
import com.ebicep.warlords.pve.mobs.witch.Witch;
import com.ebicep.warlords.pve.mobs.witherskeleton.WitherWarrior;
import com.ebicep.warlords.pve.mobs.wolf.Wolf;
import com.ebicep.warlords.pve.mobs.zombie.*;
import com.ebicep.warlords.pve.mobs.zombie.berserkzombie.BasicBerserkZombie;
import com.ebicep.warlords.pve.mobs.zombie.berserkzombie.EliteBerserkZombie;
import com.ebicep.warlords.pve.mobs.zombie.berserkzombie.EnvoyBerserkZombie;
import org.bukkit.Location;

import java.util.function.Function;

public enum Mobs {
    // Base
    BASIC_ZOMBIE(BasicZombie.class, BasicZombie::new),
    BASIC_BERSERK_ZOMBIE(BasicBerserkZombie.class, BasicBerserkZombie::new),
    BASIC_SKELETON(BasicSkeleton.class, BasicSkeleton::new),
    BASIC_PIG_ZOMBIE(BasicPigZombie.class, BasicPigZombie::new),
    BASIC_SLIME(BasicSlime.class, BasicSlime::new),
    SPIDER(Spider.class, Spider::new),
    WOLF(Wolf.class, Wolf::new),

    // Elite Tier 1 - Elite
    ELITE_ZOMBIE(EliteZombie.class, EliteZombie::new),
    GHOST_ZOMBIE(GhostZombie.class, GhostZombie::new),
    SLIME_ZOMBIE(SlimeZombie.class, SlimeZombie::new),
    MELEE_ONLY_ZOMBIE(MeleeOnlyZombie.class, MeleeOnlyZombie::new),
    RANGE_ONLY_SKELETON(RangeOnlySkeleton.class, RangeOnlySkeleton::new),
    ELITE_BERSERK_ZOMBIE(EliteBerserkZombie.class, EliteBerserkZombie::new),
    ELITE_SKELETON(EliteSkeleton.class, EliteSkeleton::new),
    ELITE_PIG_ZOMBIE(ElitePigZombie.class, ElitePigZombie::new),
    MAGMA_CUBE(MagmaCube.class, MagmaCube::new),
    IRON_GOLEM(IronGolem.class, IronGolem::new),
    BLAZE(Blaze.class, Blaze::new),
    WITCH(Witch.class, Witch::new),
    WITHER_SKELETON(WitherWarrior.class, WitherWarrior::new),

    // Elite Tier 2 - Envoy
    ENVOY_ZOMBIE(EnvoyZombie.class, EnvoyZombie::new),
    ENVOY_SKELETON(EnvoySkeleton.class, EnvoySkeleton::new),
    ENVOY_PIG_ZOMBIE(EnvoyPigZombie.class, EnvoyPigZombie::new),
    ENVOY_BERSERKER_ZOMBIE(EnvoyBerserkZombie.class, EnvoyBerserkZombie::new),

    // Elite Tier 3 - Void
    VOID_ZOMBIE(VoidZombie.class, VoidZombie::new),
    VOID_SKELETON(VoidSkeleton.class, VoidSkeleton::new),
    VOID_PIG_ZOMBIE(VoidPigZombie.class, VoidPigZombie::new),
    VOID_SLIME(VoidSlime.class, VoidSlime::new),

    // Elite Tier 4 - Exiled
    EXILED_ZOMBIE(ExiledZombie.class, ExiledZombie::new),
    EXILED_SKELETON(ExiledSkeleton.class, ExiledSkeleton::new),
    EXILED_VOID_LANCER(NetheriteZombie.class, NetheriteZombie::new),
    EXILED_ZOMBIE_RIFT(RiftZombie.class, RiftZombie::new),
    EXILED_ZOMBIE_LAVA(LavaZombie.class, LavaZombie::new),

    // Elite Tier 5 - Forgotten
    FORGOTTEN_ZOMBIE(ForgottenZombie.class, ForgottenZombie::new),
    FORGOTTEN_LANCER(OvergrownZombie.class, OvergrownZombie::new),

    // Boss
    BOLTARO(Boltaro.class, Boltaro::new),
    GHOULCALLER(Ghoulcaller.class, Ghoulcaller::new),
    NARMER(Narmer.class, Narmer::new),
    MITHRA(Mithra.class, Mithra::new),
    ZENITH(Zenith.class, Zenith::new),
    CHESSKING(Chessking.class, Chessking::new),
    ILLUMINA(Illumina.class, Illumina::new),
    TORMENT(Torment.class, Torment::new),
    VOID(Void.class, Void::new),

    // Boss minions
    BOLTARO_SHADOW(BoltaroShadow.class, BoltaroShadow::new),
    BOLTARO_EXLIED(BoltaroExiled.class, BoltaroExiled::new),

    // Raid Boss
    PHYSIRA(Physira.class, Physira::new),

    //EVENTS
    EVENT_BOLTARO_BONANZA(EventBoltaro.class, EventBoltaro::new),
    EVENT_NARMER(EventNarmer.class, EventNarmer::new),
    EVENT_NARMER_ACOLYTE(EventNarmerAcolyte.class, EventNarmerAcolyte::new),
    EVENT_NARMER_DJER(EventDjer.class, EventDjer::new),
    EVENT_NARMER_DJET(EventDjet.class, EventDjet::new),
    EVENT_MITHRA_FORSAKEN_FROST(EventForsakenFrost.class, EventForsakenFrost::new),
    EVENT_MITHRA_FORSAKEN_FOLIAGE(EventForsakenFoliage.class, EventForsakenFoliage::new),
    EVENT_MITHRA_FORSAKEN_SHRIEKER(EventForsakenShrieker.class, EventForsakenShrieker::new),
    EVENT_MITHRA_FORSAKEN_RESPITE(EventForsakenRespite.class, EventForsakenRespite::new),
    EVENT_MITHRA_FORSAKEN_CRUOR(EventForsakenCruor.class, EventForsakenCruor::new),
    EVENT_MITHRA_FORSAKEN_DEGRADER(EventForsakenDegrader.class, EventForsakenDegrader::new),
    EVENT_MITHRA_FORSAKEN_ABERRATION(EventForsakenAberration.class, EventForsakenAberration::new),


    ;

    public final Class<?> mobClass;
    public final Function<Location, AbstractMob<?>> createMob;

    Mobs(Class<?> mobClass, Function<Location, AbstractMob<?>> createMob) {
        this.createMob = createMob;
        this.mobClass = mobClass;
    }
}
