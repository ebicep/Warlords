package com.ebicep.warlords.pve.mobs;

import com.ebicep.warlords.game.option.raid.bosses.Physira;
import com.ebicep.warlords.pve.mobs.blaze.BlazingKindle;
import com.ebicep.warlords.pve.mobs.bosses.Void;
import com.ebicep.warlords.pve.mobs.bosses.*;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.*;
import com.ebicep.warlords.pve.mobs.events.baneofimpurities.EventCalamityCore;
import com.ebicep.warlords.pve.mobs.events.baneofimpurities.EventExiledCore;
import com.ebicep.warlords.pve.mobs.events.baneofimpurities.EventIllumina;
import com.ebicep.warlords.pve.mobs.events.baneofimpurities.EventIllusionCore;
import com.ebicep.warlords.pve.mobs.events.boltarobonanza.EventBoltaro;
import com.ebicep.warlords.pve.mobs.events.pharaohsrevenge.EventDjer;
import com.ebicep.warlords.pve.mobs.events.pharaohsrevenge.EventDjet;
import com.ebicep.warlords.pve.mobs.events.pharaohsrevenge.EventNarmer;
import com.ebicep.warlords.pve.mobs.events.pharaohsrevenge.EventNarmerAcolyte;
import com.ebicep.warlords.pve.mobs.events.spidersburrow.*;
import com.ebicep.warlords.pve.mobs.irongolem.GolemApprentice;
import com.ebicep.warlords.pve.mobs.magmacube.Illumination;
import com.ebicep.warlords.pve.mobs.pigzombie.PigAlleviator;
import com.ebicep.warlords.pve.mobs.pigzombie.PigDisciple;
import com.ebicep.warlords.pve.mobs.pigzombie.PigParticle;
import com.ebicep.warlords.pve.mobs.pigzombie.PigShaman;
import com.ebicep.warlords.pve.mobs.skeleton.*;
import com.ebicep.warlords.pve.mobs.slime.SlimyAnomaly;
import com.ebicep.warlords.pve.mobs.slime.SlimyChess;
import com.ebicep.warlords.pve.mobs.spider.ArachnoVenari;
import com.ebicep.warlords.pve.mobs.witch.WitchDeacon;
import com.ebicep.warlords.pve.mobs.witherskeleton.CelestialOpus;
import com.ebicep.warlords.pve.mobs.wolf.Hound;
import com.ebicep.warlords.pve.mobs.zombie.*;
import com.ebicep.warlords.pve.mobs.zombie.berserkzombie.AdvancedWarriorBerserker;
import com.ebicep.warlords.pve.mobs.zombie.berserkzombie.BasicWarriorBerserker;
import com.ebicep.warlords.pve.mobs.zombie.berserkzombie.IntermediateWarriorBerserker;
import org.bukkit.Location;

import java.util.function.Function;

public enum Mob {

    // Basic
    ZOMBIE_LANCER(ZombieLancer.class, ZombieLancer::new),
    BAIC_WARRIOR_BERSERKER(BasicWarriorBerserker.class, BasicWarriorBerserker::new),
    SKELETAL_MAGE(SkeletalMage.class, SkeletalMage::new),
    PIG_DISCIPLE(PigDisciple.class, PigDisciple::new),
    SLIMY_ANOMALY(SlimyAnomaly.class, SlimyAnomaly::new),
    ARACHNO_VENARI(ArachnoVenari.class, ArachnoVenari::new),

    // Intermediate
    HOUND(Hound.class, Hound::new),
    INTERMEDIATE_WARRIOR_BERSERKER(IntermediateWarriorBerserker.class, IntermediateWarriorBerserker::new),
    SKELETAL_WARLOCK(SkeletalWarlock.class, SkeletalWarlock::new),
    PIG_SHAMAN(PigShaman.class, PigShaman::new),
    ILLUMINATION(Illumination.class, Illumination::new),
    GOLEM_APPRENTICE(GolemApprentice.class, GolemApprentice::new),
    WITCH_DEACON(WitchDeacon.class, WitchDeacon::new),
    SCRUPULOUS_ZOMBIE(ScrupulousZombie.class, ScrupulousZombie::new),
    BLAZING_KINDLE(BlazingKindle.class, BlazingKindle::new), //TODO
    WANDER_KNIGHTS(WanderKnights.class, WanderKnights::new),

    // Advanced
    ZOMBIE_SWORDSMAN(ZombieSwordsman.class, ZombieSwordsman::new),
    ZOMBIE_LAMENT(ZombieLament.class, ZombieLament::new),
    SLIME_GUARD(SlimeGuard.class, SlimeGuard::new),
    CELESTIAL_BOW_WIELDER(CelestialBowWielder.class, CelestialBowWielder::new),
    ZOMBIE_VANGUARD(ZombieVanguard.class, ZombieVanguard::new),
    ADVANCED_WARRIOR_BERSERKER(AdvancedWarriorBerserker.class, AdvancedWarriorBerserker::new),
    VOID_ZOMBIE(VoidZombie.class, VoidZombie::new),
    ZOMBIE_KNIGHT(ZombieKnight.class, ZombieKnight::new),
    SLIMY_CHESS(SlimyChess.class, SlimyChess::new),
    VOID_RAIDER(ZombieRaider.class, ZombieRaider::new),
    WANDER_WALKER(WanderWalker.class, WanderWalker::new),

    // Elite
    CELESTIAL_SWORD_WIELDER(CelestialSwordWielder.class, CelestialSwordWielder::new),
    CELESTIAL_OPUS(CelestialOpus.class, CelestialOpus::new),
    SKELETAL_ENTROPY(SkeletalEntropy.class, SkeletalEntropy::new),
    PIG_ALLEVIATOR(PigAlleviator.class, PigAlleviator::new),
    SKELETAL_SORCERER(SkeletalSorcerer.class, SkeletalSorcerer::new),
    RIFT_WALKER(RiftWalker.class, RiftWalker::new),
    FIRE_SPLITTER(FireSplitter.class, FireSplitter::new),
    OVERGROWN_ZOMBIE(OvergrownZombie.class, OvergrownZombie::new),
    SKELETAL_PYROMANCER(SkeletalPyromancer.class, SkeletalPyromancer::new),
    VOID_ANOMALY(VoidAnomaly.class, VoidAnomaly::new),

    // Champion
    NIGHTMARE_ZOMBIE(NightmareZombie.class, NightmareZombie::new),
    VOID_SKELETON(VoidSkeleton.class, VoidSkeleton::new),
    PIG_PARTICLE(PigParticle.class, PigParticle::new),
    EXTREME_ZEALOT(ExtremeZealot.class, ExtremeZealot::new),
    SMART_SKELETON(SmartSkeleton.class, SmartSkeleton::new),


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
    MAGMATIC_OOZE(MagmaticOoze.class, MagmaticOoze::new),

    // Boss minions
    BOLTARO_SHADOW(BoltaroShadow.class, BoltaroShadow::new),
    BOLTARO_EXLIED(BoltaroExiled.class, BoltaroExiled::new),
    TORMENTED_SOUL(TormentedSoul.class, TormentedSoul::new),
    NARMER_ACOLYTE(NarmerAcolyte.class, NarmerAcolyte::new),
    ZENITH_LEGIONNAIRE(ZenithLegionnaire.class, ZenithLegionnaire::new),

    // Raid Boss
    PHYSIRA(Physira.class, Physira::new),

    //EVENTS
    EVENT_BOLTARO(EventBoltaro.class, EventBoltaro::new),
    EVENT_NARMER(EventNarmer.class, EventNarmer::new),
    EVENT_NARMER_ACOLYTE(EventNarmerAcolyte.class, EventNarmerAcolyte::new),
    EVENT_NARMER_DJER(EventDjer.class, EventDjer::new),
    EVENT_NARMER_DJET(EventDjet.class, EventDjet::new),
    EVENT_MITHRA(EventMithra.class, EventMithra::new),
    EVENT_MITHRA_FORSAKEN_FROST(EventForsakenFrost.class, EventForsakenFrost::new),
    EVENT_MITHRA_FORSAKEN_FOLIAGE(EventForsakenFoliage.class, EventForsakenFoliage::new),
    EVENT_MITHRA_FORSAKEN_SHRIEKER(EventForsakenShrieker.class, EventForsakenShrieker::new),
    EVENT_MITHRA_FORSAKEN_RESPITE(EventForsakenRespite.class, EventForsakenRespite::new),
    EVENT_MITHRA_FORSAKEN_CRUOR(EventForsakenCruor.class, EventForsakenCruor::new),
    EVENT_MITHRA_FORSAKEN_DEGRADER(EventForsakenDegrader.class, EventForsakenDegrader::new),
    EVENT_MITHRA_FORSAKEN_APPARITION(EventForsakenApparition.class, EventForsakenApparition::new),
    EVENT_MITHRA_POISONOUS_SPIDER(EventPoisonousSpider.class, EventPoisonousSpider::new),
    EVENT_MITHRA_EGG_SAC(EventEggSac.class, EventEggSac::new),
    EVENT_ILLUSION_CORE(EventIllusionCore.class, EventIllusionCore::new),
    EVENT_EXILED_CORE(EventExiledCore.class, EventExiledCore::new),
    EVENT_CALAMITY_CORE(EventCalamityCore.class, EventCalamityCore::new),
    EVENT_ILLUMINA(EventIllumina.class, EventIllumina::new),

    ;

    public static final Mob[] MOBS = values();
    public static final Mob[] BOSSES = {BOLTARO, GHOULCALLER, NARMER, MITHRA, ZENITH, CHESSKING, ILLUMINA, TORMENT, VOID, PHYSIRA};
    public final Class<?> mobClass;
    public final Function<Location, AbstractMob<?>> createMob;

    Mob(Class<?> mobClass, Function<Location, AbstractMob<?>> createMob) {
        this.createMob = createMob;
        this.mobClass = mobClass;
    }
}
