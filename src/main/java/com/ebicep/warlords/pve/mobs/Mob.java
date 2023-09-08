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
import com.mojang.datafixers.util.Function7;
import org.bukkit.Location;

import java.util.function.Function;

public enum Mob {

    // Basic
    ZOMBIE_LANCER(ZombieLancer.class, ZombieLancer::new, ZombieLancer::new),
    BASIC_WARRIOR_BERSERKER(BasicWarriorBerserker.class, BasicWarriorBerserker::new, BasicWarriorBerserker::new),
    SKELETAL_MAGE(SkeletalMage.class, SkeletalMage::new, SkeletalMage::new),
    PIG_DISCIPLE(PigDisciple.class, PigDisciple::new, PigDisciple::new),
    SLIMY_ANOMALY(SlimyAnomaly.class, SlimyAnomaly::new, SlimyAnomaly::new),
    ARACHNO_VENARI(ArachnoVenari.class, ArachnoVenari::new, ArachnoVenari::new),

    // Intermediate
    HOUND(Hound.class, Hound::new, Hound::new),
    INTERMEDIATE_WARRIOR_BERSERKER(IntermediateWarriorBerserker.class, IntermediateWarriorBerserker::new, IntermediateWarriorBerserker::new),
    SKELETAL_WARLOCK(SkeletalWarlock.class, SkeletalWarlock::new, SkeletalWarlock::new),
    PIG_SHAMAN(PigShaman.class, PigShaman::new, PigShaman::new),
    PIG_ALLEVIATOR(PigAlleviator.class, PigAlleviator::new, PigAlleviator::new),
    WITCH_DEACON(WitchDeacon.class, WitchDeacon::new, WitchDeacon::new),
    BLAZING_KINDLE(BlazingKindle.class, BlazingKindle::new, BlazingKindle::new),
    WANDER_KNIGHTS(WanderKnights.class, WanderKnights::new, WanderKnights::new),
    ZOMBIE_SWORDSMAN(ZombieSwordsman.class, ZombieSwordsman::new, ZombieSwordsman::new),
    ZOMBIE_LAMENT(ZombieLament.class, ZombieLament::new, ZombieLament::new),

    // Advanced
    ILLUMINATION(Illumination.class, Illumination::new, Illumination::new),
    GOLEM_APPRENTICE(GolemApprentice.class, GolemApprentice::new, GolemApprentice::new),
    SCRUPULOUS_ZOMBIE(ScrupulousZombie.class, ScrupulousZombie::new, ScrupulousZombie::new),
    SLIME_GUARD(SlimeGuard.class, SlimeGuard::new, SlimeGuard::new),
    CELESTIAL_BOW_WIELDER(CelestialBowWielder.class, CelestialBowWielder::new, CelestialBowWielder::new),
    ZOMBIE_VANGUARD(ZombieVanguard.class, ZombieVanguard::new, ZombieVanguard::new),
    ADVANCED_WARRIOR_BERSERKER(AdvancedWarriorBerserker.class, AdvancedWarriorBerserker::new, AdvancedWarriorBerserker::new),
    VOID_ZOMBIE(VoidZombie.class, VoidZombie::new, VoidZombie::new),
    ZOMBIE_KNIGHT(ZombieKnight.class, ZombieKnight::new, ZombieKnight::new),
    SLIMY_CHESS(SlimyChess.class, SlimyChess::new, SlimyChess::new),
    VOID_RAIDER(ZombieRaider.class, ZombieRaider::new, ZombieRaider::new),
    WANDER_WALKER(WanderWalker.class, WanderWalker::new, WanderWalker::new),
    SKELETAL_ENTROPY(SkeletalEntropy.class, SkeletalEntropy::new, SkeletalEntropy::new),
    FIRE_SPLITTER(FireSplitter.class, FireSplitter::new, FireSplitter::new),

    // Elite
    CELESTIAL_SWORD_WIELDER(CelestialSwordWielder.class, CelestialSwordWielder::new, CelestialSwordWielder::new),
    CELESTIAL_OPUS(CelestialOpus.class, CelestialOpus::new, CelestialOpus::new),
    RIFT_WALKER(RiftWalker.class, RiftWalker::new, RiftWalker::new),
    OVERGROWN_ZOMBIE(OvergrownZombie.class, OvergrownZombie::new, OvergrownZombie::new),
    SKELETAL_PYROMANCER(SkeletalPyromancer.class, SkeletalPyromancer::new, SkeletalPyromancer::new),
    SKELETAL_ANOMALY(SkeletalAnomaly.class, SkeletalAnomaly::new, SkeletalAnomaly::new),
    SKELETAL_MESMER(SkeletalMesmer.class, SkeletalMesmer::new, SkeletalMesmer::new),

    // Champion
    NIGHTMARE_ZOMBIE(NightmareZombie.class, NightmareZombie::new, NightmareZombie::new),
    PIG_PARTICLE(PigParticle.class, PigParticle::new, PigParticle::new),
    EXTREME_ZEALOT(ExtremeZealot.class, ExtremeZealot::new, ExtremeZealot::new),
    SMART_SKELETON(SmartSkeleton.class, SmartSkeleton::new, SmartSkeleton::new),
    SKELETAL_SORCERER(SkeletalSorcerer.class, SkeletalSorcerer::new, SkeletalSorcerer::new),


    // Boss
    BOLTARO(Boltaro.class, Boltaro::new, Boltaro::new),
    GHOULCALLER(Ghoulcaller.class, Ghoulcaller::new, Ghoulcaller::new),
    NARMER(Narmer.class, Narmer::new, Narmer::new),
    MITHRA(Mithra.class, Mithra::new, Mithra::new),
    ZENITH(Zenith.class, Zenith::new, Zenith::new),
    CHESSKING(Chessking.class, Chessking::new, Chessking::new),
    ILLUMINA(Illumina.class, Illumina::new, Illumina::new),
    TORMENT(Torment.class, Torment::new, Torment::new),
    VOID(Void.class, Void::new, Void::new),
    MAGMATIC_OOZE(MagmaticOoze.class, MagmaticOoze::new, MagmaticOoze::new),


    // Boss minions
    BOLTARO_SHADOW(BoltaroShadow.class, BoltaroShadow::new, BoltaroShadow::new),
    BOLTARO_EXLIED(BoltaroExiled.class, BoltaroExiled::new, BoltaroExiled::new),
    TORMENTED_SOUL(TormentedSoul.class, TormentedSoul::new, TormentedSoul::new),
    NARMER_ACOLYTE(NarmerAcolyte.class, NarmerAcolyte::new, NarmerAcolyte::new),
    ZENITH_LEGIONNAIRE(ZenithLegionnaire.class, ZenithLegionnaire::new, ZenithLegionnaire::new),


    // Raid Boss
    PHYSIRA(Physira.class, Physira::new, Physira::new),

    //EVENTS
    EVENT_BOLTARO(EventBoltaro.class, EventBoltaro::new, EventBoltaro::new),
    EVENT_NARMER(EventNarmer.class, EventNarmer::new, EventNarmer::new),
    EVENT_NARMER_ACOLYTE(EventNarmerAcolyte.class, EventNarmerAcolyte::new, EventNarmerAcolyte::new),
    EVENT_NARMER_DJER(EventDjer.class, EventDjer::new, EventDjer::new),
    EVENT_NARMER_DJET(EventDjet.class, EventDjet::new, EventDjet::new),
    EVENT_MITHRA(EventMithra.class, EventMithra::new, EventMithra::new),
    EVENT_MITHRA_FORSAKEN_FROST(EventForsakenFrost.class, EventForsakenFrost::new, EventForsakenFrost::new),
    EVENT_MITHRA_FORSAKEN_FOLIAGE(EventForsakenFoliage.class, EventForsakenFoliage::new, EventForsakenFoliage::new),
    EVENT_MITHRA_FORSAKEN_SHRIEKER(EventForsakenShrieker.class, EventForsakenShrieker::new, EventForsakenShrieker::new),
    EVENT_MITHRA_FORSAKEN_RESPITE(EventForsakenRespite.class, EventForsakenRespite::new, EventForsakenRespite::new),
    EVENT_MITHRA_FORSAKEN_CRUOR(EventForsakenCruor.class, EventForsakenCruor::new, EventForsakenCruor::new),
    EVENT_MITHRA_FORSAKEN_DEGRADER(EventForsakenDegrader.class, EventForsakenDegrader::new, EventForsakenDegrader::new),
    EVENT_MITHRA_FORSAKEN_APPARITION(EventForsakenApparition.class, EventForsakenApparition::new, EventForsakenApparition::new),
    EVENT_MITHRA_POISONOUS_SPIDER(EventPoisonousSpider.class, EventPoisonousSpider::new, EventPoisonousSpider::new),
    EVENT_MITHRA_EGG_SAC(EventEggSac.class, EventEggSac::new, EventEggSac::new),
    EVENT_ILLUSION_CORE(EventIllusionCore.class, EventIllusionCore::new, EventIllusionCore::new),
    EVENT_EXILED_CORE(EventExiledCore.class, EventExiledCore::new, EventExiledCore::new),
    EVENT_CALAMITY_CORE(EventCalamityCore.class, EventCalamityCore::new, EventCalamityCore::new),
    EVENT_ILLUMINA(EventIllumina.class, EventIllumina::new, EventIllumina::new),

    ;

    public static final Mob[] VALUES = values();
    public static final Mob[] BASIC = {
            ZOMBIE_LANCER, BASIC_WARRIOR_BERSERKER, SKELETAL_MAGE, PIG_DISCIPLE, SLIMY_ANOMALY,
            ARACHNO_VENARI
    };
    public static final Mob[] INTERMEDIATE = {
            HOUND, INTERMEDIATE_WARRIOR_BERSERKER, SKELETAL_WARLOCK, PIG_SHAMAN, PIG_ALLEVIATOR,
            WITCH_DEACON, BLAZING_KINDLE, WANDER_KNIGHTS, ZOMBIE_SWORDSMAN, ZOMBIE_LAMENT
    };
    public static final Mob[] ADVANCED = {
            ILLUMINATION, GOLEM_APPRENTICE, SCRUPULOUS_ZOMBIE, SLIME_GUARD, CELESTIAL_BOW_WIELDER,
            ZOMBIE_VANGUARD, ADVANCED_WARRIOR_BERSERKER, VOID_ZOMBIE, ZOMBIE_KNIGHT, SLIMY_CHESS,
            VOID_RAIDER, WANDER_WALKER, SKELETAL_ENTROPY, FIRE_SPLITTER
    };
    public static final Mob[] ELITE = {
            CELESTIAL_SWORD_WIELDER, CELESTIAL_OPUS, RIFT_WALKER, OVERGROWN_ZOMBIE,
            SKELETAL_PYROMANCER, SKELETAL_ANOMALY, SKELETAL_MESMER
    };
    public static final Mob[] CHAMPION = {
            NIGHTMARE_ZOMBIE, PIG_PARTICLE, EXTREME_ZEALOT, SMART_SKELETON
    };
    public static final Mob[] BOSSES = {
            BOLTARO, GHOULCALLER, NARMER, MITHRA, ZENITH,
            CHESSKING, ILLUMINA, TORMENT, VOID, MAGMATIC_OOZE,
            PHYSIRA
    };
    public final Class<?> mobClass;
    @Deprecated
    public final Function<Location, AbstractMob<?>> createMobLegacy;
    public final Function7<Location, String, Integer, Float, Integer, Float, Float, AbstractMob<?>> createMobFunction;
    public String name;
    public int maxHealth;
    public float walkSpeed;
    public int damageResistance;
    public float minMeleeDamage;
    public float maxMeleeDamage;

    Mob(
            Class<?> mobClass,
            Function<Location, AbstractMob<?>> createMobLegacy,
            Function7<Location, String, Integer, Float, Integer, Float, Float, AbstractMob<?>> createMobFunction
    ) {
        this.createMobLegacy = createMobLegacy;
        this.createMobFunction = createMobFunction;
        this.mobClass = mobClass;
    }

    public AbstractMob<?> createMob(Location spawnLocation) {
        return createMobFunction.apply(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    public enum MobGroup {
        BASIC(Mob.BASIC),
        INTERMEDIATE(Mob.INTERMEDIATE),
        ADVANCED(Mob.ADVANCED),
        ELITE(Mob.ELITE),
        CHAMPION(Mob.CHAMPION),
        BOSSES(Mob.BOSSES),
        ALL(Mob.VALUES);

        public final Mob[] mobs;

        MobGroup(Mob[] mobs) {
            this.mobs = mobs;
        }
    }
}
