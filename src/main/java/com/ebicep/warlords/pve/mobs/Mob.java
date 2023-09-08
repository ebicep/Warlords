package com.ebicep.warlords.pve.mobs;

import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.pve.mobs.blaze.AbstractBlaze;
import com.ebicep.warlords.pve.mobs.blaze.BlazingKindle;
import com.ebicep.warlords.pve.mobs.bosses.Void;
import com.ebicep.warlords.pve.mobs.bosses.*;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.*;
import com.ebicep.warlords.pve.mobs.events.baneofimpurities.EventCalamityCore;
import com.ebicep.warlords.pve.mobs.events.baneofimpurities.EventExiledCore;
import com.ebicep.warlords.pve.mobs.events.baneofimpurities.EventIllumina;
import com.ebicep.warlords.pve.mobs.events.baneofimpurities.EventIllusionCore;
import com.ebicep.warlords.pve.mobs.events.boltarobonanza.EventBoltaro;
import com.ebicep.warlords.pve.mobs.events.boltarobonanza.EventBoltaroShadow;
import com.ebicep.warlords.pve.mobs.events.pharaohsrevenge.EventDjer;
import com.ebicep.warlords.pve.mobs.events.pharaohsrevenge.EventDjet;
import com.ebicep.warlords.pve.mobs.events.pharaohsrevenge.EventNarmer;
import com.ebicep.warlords.pve.mobs.events.pharaohsrevenge.EventNarmerAcolyte;
import com.ebicep.warlords.pve.mobs.events.spidersburrow.*;
import com.ebicep.warlords.pve.mobs.irongolem.AbstractIronGolem;
import com.ebicep.warlords.pve.mobs.irongolem.GolemApprentice;
import com.ebicep.warlords.pve.mobs.magmacube.AbstractMagmaCube;
import com.ebicep.warlords.pve.mobs.magmacube.Illumination;
import com.ebicep.warlords.pve.mobs.pigzombie.*;
import com.ebicep.warlords.pve.mobs.skeleton.*;
import com.ebicep.warlords.pve.mobs.slime.AbstractSlime;
import com.ebicep.warlords.pve.mobs.slime.SlimyAnomaly;
import com.ebicep.warlords.pve.mobs.slime.SlimyChess;
import com.ebicep.warlords.pve.mobs.spider.AbstractSpider;
import com.ebicep.warlords.pve.mobs.spider.ArachnoVenari;
import com.ebicep.warlords.pve.mobs.witch.AbstractWitch;
import com.ebicep.warlords.pve.mobs.witch.WitchDeacon;
import com.ebicep.warlords.pve.mobs.witherskeleton.AbstractWitherSkeleton;
import com.ebicep.warlords.pve.mobs.witherskeleton.CelestialOpus;
import com.ebicep.warlords.pve.mobs.wolf.AbstractWolf;
import com.ebicep.warlords.pve.mobs.wolf.Hound;
import com.ebicep.warlords.pve.mobs.zombie.*;
import com.ebicep.warlords.pve.mobs.zombie.berserkzombie.AdvancedWarriorBerserker;
import com.ebicep.warlords.pve.mobs.zombie.berserkzombie.BasicWarriorBerserker;
import com.ebicep.warlords.pve.mobs.zombie.berserkzombie.IntermediateWarriorBerserker;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import com.mojang.datafixers.util.Function7;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public enum Mob {

    // Basic
    ZOMBIE_LANCER(ZombieLancer.class, ZombieLancer::new, ZombieLancer::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.PURPLE_KNIGHT),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
            Weapons.STEEL_SWORD.getItem()
    )),
    BASIC_WARRIOR_BERSERKER(BasicWarriorBerserker.class, BasicWarriorBerserker::new, BasicWarriorBerserker::new, new Utils.SimpleEntityEquipment(
            ArmorManager.Helmets.SIMPLE_WARRIOR_HELMET.itemRed,
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
            new ItemStack(Material.WOODEN_SWORD)
    )),
    SKELETAL_MAGE(SkeletalMage.class, SkeletalMage::new, SkeletalMage::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.FACELESS_MAGE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
            new ItemStack(Material.BOW)
    )),
    PIG_DISCIPLE(PigDisciple.class, PigDisciple::new, PigDisciple::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SAMURAI),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
            Weapons.SILVER_PHANTASM_TRIDENT.getItem()
    )),
    SLIMY_ANOMALY(SlimyAnomaly.class, SlimyAnomaly::new, SlimyAnomaly::new, null),
    ARACHNO_VENARI(ArachnoVenari.class, ArachnoVenari::new, ArachnoVenari::new, null),

    // Intermediate
    HOUND(Hound.class, Hound::new, Hound::new, null),
    INTERMEDIATE_WARRIOR_BERSERKER(IntermediateWarriorBerserker.class, IntermediateWarriorBerserker::new, IntermediateWarriorBerserker::new, new Utils.SimpleEntityEquipment(
            ArmorManager.Helmets.GREATER_WARRIOR_HELMET.itemRed,
            new ItemStack(Material.CHAINMAIL_CHESTPLATE),
            new ItemStack(Material.CHAINMAIL_LEGGINGS),
            new ItemStack(Material.CHAINMAIL_BOOTS),
            new ItemStack(Material.PRISMARINE_SHARD)
    )),
    SKELETAL_WARLOCK(SkeletalWarlock.class, SkeletalWarlock::new, SkeletalWarlock::new, new Utils.SimpleEntityEquipment(
            new ItemStack(Material.ORANGE_CARPET),
            new ItemStack(Material.CHAINMAIL_CHESTPLATE),
            new ItemStack(Material.CHAINMAIL_LEGGINGS),
            new ItemStack(Material.CHAINMAIL_BOOTS),
            new ItemStack(Material.BOW)
    )),
    PIG_SHAMAN(PigShaman.class, PigShaman::new, PigShaman::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SAMURAI),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
            new ItemStack(Material.DIAMOND_LEGGINGS),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
            new ItemStack(Material.COOKIE)
    )),
    PIG_ALLEVIATOR(PigAlleviator.class, PigAlleviator::new, PigAlleviator::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SAMURAI),
            new ItemStack(Material.DIAMOND_HELMET),
            new ItemStack(Material.DIAMOND_LEGGINGS),
            new ItemStack(Material.DIAMOND_BOOTS),
            new ItemStack(Material.BAKED_POTATO)
    )),
    WITCH_DEACON(WitchDeacon.class, WitchDeacon::new, WitchDeacon::new, null),
    BLAZING_KINDLE(BlazingKindle.class, BlazingKindle::new, BlazingKindle::new, null),
    WANDER_KNIGHTS(WanderKnights.class, WanderKnights::new, WanderKnights::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.FANCY_CUBE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 105, 147, 158),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 105, 147, 158),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 105, 147, 158),
            Weapons.LUNAR_RELIC.getItem()
    )),
    ZOMBIE_SWORDSMAN(ZombieSwordsman.class, ZombieSwordsman::new, ZombieSwordsman::new, new Utils.SimpleEntityEquipment(
            new ItemStack(Material.WHITE_CARPET),
            new ItemStack(Material.CHAINMAIL_CHESTPLATE),
            new ItemStack(Material.CHAINMAIL_LEGGINGS),
            new ItemStack(Material.CHAINMAIL_BOOTS),
            new ItemStack(Material.PRISMARINE_SHARD)
    )),
    ZOMBIE_LAMENT(ZombieLament.class, ZombieLament::new, ZombieLament::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.BLUE_GHOST),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 69, 176),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 69, 176),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 69, 176),
            Weapons.SILVER_PHANTASM_STAFF_2.getItem()
    )),

    // Advanced
    ILLUMINATION(Illumination.class, Illumination::new, Illumination::new, null),
    GOLEM_APPRENTICE(GolemApprentice.class, GolemApprentice::new, GolemApprentice::new, null),
    SCRUPULOUS_ZOMBIE(ScrupulousZombie.class, ScrupulousZombie::new, ScrupulousZombie::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SCULK_CORRUPTION),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 10, 50, 130),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 10, 50, 130),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 10, 50, 130),
            Weapons.AMARANTH.getItem()
    )),
    SLIME_GUARD(SlimeGuard.class, SlimeGuard::new, SlimeGuard::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SLIME_BLOCK),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 106, 255, 106),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 106, 255, 106),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 106, 255, 106),
            Weapons.NEW_LEAF_SPEAR.getItem()
    )),
    CELESTIAL_BOW_WIELDER(CelestialBowWielder.class, CelestialBowWielder::new, CelestialBowWielder::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.BOW_HEAD),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 20, 20),
            Weapons.FROSTBITE.getItem()
    )),
    ZOMBIE_VANGUARD(ZombieVanguard.class, ZombieVanguard::new, ZombieVanguard::new, new Utils.SimpleEntityEquipment(
            ArmorManager.Helmets.LEGENDARY_PALADIN_HELMET.itemRed,
            new ItemStack(Material.DIAMOND_CHESTPLATE),
            new ItemStack(Material.DIAMOND_LEGGINGS),
            new ItemStack(Material.DIAMOND_BOOTS),
            Weapons.FELFLAME_BLADE.getItem()
    )),
    ADVANCED_WARRIOR_BERSERKER(AdvancedWarriorBerserker.class, AdvancedWarriorBerserker::new, AdvancedWarriorBerserker::new, new Utils.SimpleEntityEquipment(
            ArmorManager.Helmets.LEGENDARY_WARRIOR_HELMET.itemRed,
            new ItemStack(Material.DIAMOND_CHESTPLATE),
            new ItemStack(Material.DIAMOND_LEGGINGS),
            new ItemStack(Material.DIAMOND_BOOTS),
            new ItemStack(Material.COOKED_SALMON)
    )),
    VOID_ZOMBIE(VoidZombie.class, VoidZombie::new, VoidZombie::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.FACELESS_BANDIT),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 0, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 0, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 0, 0),
            Weapons.VOID_EDGE.getItem()
    )),
    ZOMBIE_KNIGHT(ZombieKnight.class, ZombieKnight::new, ZombieKnight::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.NETHERITE_HELMET),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 20, 20),
            Weapons.GEMINI.getItem()
    )),
    SLIMY_CHESS(SlimyChess.class, SlimyChess::new, SlimyChess::new, null),
    ZOMBIE_RAIDER(ZombieRaider.class, ZombieRaider::new, ZombieRaider::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.FANCY_CUBE_2),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 56, 71, 74),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 56, 71, 74),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 56, 71, 74),
            Weapons.NETHERSTEEL_KATANA.getItem()
    )),
    WANDER_WALKER(WanderWalker.class, WanderWalker::new, WanderWalker::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SCULK_MONSTER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 204, 204),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 204, 204),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 204, 204),
            Weapons.LUNAR_JUSTICE.getItem()
    )),
    SKELETAL_ENTROPY(SkeletalEntropy.class, SkeletalEntropy::new, SkeletalEntropy::new, new Utils.SimpleEntityEquipment(
            new ItemStack(Material.PINK_CARPET),
            new ItemStack(Material.DIAMOND_CHESTPLATE),
            new ItemStack(Material.DIAMOND_LEGGINGS),
            new ItemStack(Material.DIAMOND_BOOTS),
            Weapons.VOID_TWIG.getItem()
    )),
    FIRE_SPLITTER(FireSplitter.class, FireSplitter::new, FireSplitter::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.LAVA_MONSTER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 252, 170, 53),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 252, 170, 53),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 252, 170, 53),
            Weapons.SILVER_PHANTASM_SWORD_2.getItem()
    )),

    // Elite
    CELESTIAL_SWORD_WIELDER(CelestialSwordWielder.class, CelestialSwordWielder::new, CelestialSwordWielder::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SWORD_HEAD),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 20, 20),
            Weapons.DIAMONDSPARK.getItem()
    )),
    CELESTIAL_OPUS(CelestialOpus.class, CelestialOpus::new, CelestialOpus::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.CELESTIAL_GOLDOR),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 40, 40, 40),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 40, 40, 40),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 40, 40, 40),
            Weapons.SILVER_PHANTASM_SAWBLADE.getItem()
    )),
    RIFT_WALKER(RiftWalker.class, RiftWalker::new, RiftWalker::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.PURPLE_RIFT),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 229, 69, 176),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 229, 69, 176),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 229, 69, 176),
            Weapons.VORPAL_SWORD.getItem()
    )),
    OVERGROWN_ZOMBIE(OvergrownZombie.class, OvergrownZombie::new, OvergrownZombie::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.GREEN_LANCER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 130, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 130, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 130, 20),
            Weapons.NEW_LEAF_AXE.getItem()
    )),
    SKELETAL_PYROMANCER(SkeletalPyromancer.class, SkeletalPyromancer::new, SkeletalPyromancer::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.WITHER_SOUL),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 29, 49, 64),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 29, 49, 64),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 29, 49, 64),
            null
    )),
    SKELETAL_ANOMALY(SkeletalAnomaly.class, SkeletalAnomaly::new, SkeletalAnomaly::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SEEK_DOORS),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 64, 64, 64),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 64, 64, 64),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 64, 64, 64),
            Weapons.FABLED_HEROICS_SWORD.getItem()
    )),
    SKELETAL_MESMER(SkeletalMesmer.class, SkeletalMesmer::new, SkeletalMesmer::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEMON_SKELETON),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 0, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 0, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 0, 0),
            Weapons.ARMBLADE.getItem()
    )),

    // Champion
    NIGHTMARE_ZOMBIE(NightmareZombie.class, NightmareZombie::new, NightmareZombie::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SHADOW_DEMON),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 70, 50, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 70, 50, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 70, 50, 20),
            Weapons.FABLED_HEROICS_SWORD.getItem()
    )),
    PIG_PARTICLE(PigParticle.class, PigParticle::new, PigParticle::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.HOODED_KNIGHT),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 20, 20),
            Weapons.NETHERSTEEL_KATANA.getItem()
    )),
    EXTREME_ZEALOT(ExtremeZealot.class, ExtremeZealot::new, ExtremeZealot::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.FANCY_CUBE_2),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 56, 71, 74),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 56, 71, 74),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 56, 71, 74),
            Weapons.VENOMSTRIKE.getItem()
    )),
    SMART_SKELETON(SmartSkeleton.class, SmartSkeleton::new, SmartSkeleton::new, new Utils.SimpleEntityEquipment(
            null,
            null,
            null,
            null,
            new ItemStack(Material.BOW)
    )),
    SKELETAL_SORCERER(SkeletalSorcerer.class, SkeletalSorcerer::new, SkeletalSorcerer::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.WHITE_SHEKEL),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 255, 255),
            Weapons.SILVER_PHANTASM_SWORD_4.getItem()
    )),


    // Boss
    BOLTARO(Boltaro.class, Boltaro::new, Boltaro::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEMON),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 0),
            new ItemStack(Material.CHAINMAIL_LEGGINGS),
            new ItemStack(Material.CHAINMAIL_BOOTS),
            Weapons.DRAKEFANG.getItem()
    )),
    GHOULCALLER(Ghoulcaller.class, Ghoulcaller::new, Ghoulcaller::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEMON_SKELETON),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 170, 170, 170),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 170, 170, 170),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 170, 170, 170),
            Weapons.ENDERFIST.getItem()
    )),
    NARMER(Narmer.class, Narmer::new, Narmer::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.BURNING_WITHER_SKELETON),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 160, 160),
            ArmorManager.ArmorSets.GREATER_LEGGINGS.itemRed,
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 160, 160),
            Weapons.WALKING_STICK.getItem()
    )),
    MITHRA(Mithra.class, Mithra::new, Mithra::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.IRON_QUEEN),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 200, 200, 200),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 200, 200, 200),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 200, 200, 200),
            Weapons.SILVER_PHANTASM_SWORD_3.getItem()
    )),
    ZENITH(Zenith.class, Zenith::new, Zenith::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.PURPLE_ENDERMAN),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 255),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 255),
            Weapons.VORPAL_SWORD.getItem()
    )),
    CHESSKING(Chessking.class, Chessking::new, Chessking::new, null),
    ILLUMINA(Illumina.class, Illumina::new, Illumina::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEEP_DARK_WORM),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 120, 120, 200),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 120, 120, 200),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 120, 120, 200),
            Weapons.NEW_LEAF_SCYTHE.getItem()
    )),
    TORMENT(Torment.class, Torment::new, Torment::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEMON_KING),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 230, 60, 60),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 230, 60, 60),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 230, 60, 60),
            Weapons.SILVER_PHANTASM_TRIDENT.getItem()
    )),
    VOID(Void.class, Void::new, Void::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.END_MONSTER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 20, 20),
            Weapons.VOID_EDGE.getItem()
    )),
    MAGMATIC_OOZE(MagmaticOoze.class, MagmaticOoze::new, MagmaticOoze::new, null),


    // Boss minions
    BOLTARO_SHADOW(BoltaroShadow.class, BoltaroShadow::new, BoltaroShadow::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.END_MONSTER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 30, 0, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 30, 0, 0),
            Weapons.DEMONBLADE.getItem()
    )),
    BOLTARO_EXLIED(BoltaroExiled.class, BoltaroExiled::new, BoltaroExiled::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.RED_EYE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
            Weapons.GEMINI.getItem()
    )),
    TORMENTED_SOUL(TormentedSoul.class, TormentedSoul::new, TormentedSoul::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.GHOST_RED),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 140, 140, 140),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 140, 140, 15),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 140, 140, 140),
            Weapons.CLAWS.getItem()
    )),
    DEPRESSED_SOUL(DepressedSoul.class, DepressedSoul::new, DepressedSoul::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.GHOST_BLUE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 140, 140, 140),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 140, 140, 15),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 140, 140, 140),
            Weapons.CLAWS.getItem()
    )),
    FURIOUS_SOUL(FuriousSoul.class, FuriousSoul::new, FuriousSoul::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.GHOST_MAGENTA),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 140, 140, 140),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 140, 140, 15),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 140, 140, 140),
            Weapons.CLAWS.getItem()
    )),
    VOLTAIC_SOUL(VoltaicSoul.class, VoltaicSoul::new, VoltaicSoul::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.GHOST_PURPLE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 140, 140, 140),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 140, 140, 15),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 140, 140, 140),
            Weapons.CLAWS.getItem()
    )),
    AGONIZED_SOUL(AgonizedSoul.class, AgonizedSoul::new, AgonizedSoul::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.GHOST_GRAY),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 140, 140, 140),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 140, 140, 15),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 140, 140, 140),
            Weapons.CLAWS.getItem()
    )),
    NARMER_ACOLYTE(NarmerAcolyte.class, NarmerAcolyte::new, NarmerAcolyte::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.RED_EYE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 30, 0, 15),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 30, 0, 15),
            Weapons.DEMONBLADE.getItem()
    )),
    ZENITH_LEGIONNAIRE(ZenithLegionnaire.class, ZenithLegionnaire::new, ZenithLegionnaire::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.FACELESS_BANDIT),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 100, 0, 80),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 100, 0, 80),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 100, 0, 80),
            Weapons.LUNAR_JUSTICE.getItem()
    )),
    SOUL_OF_GRADIENT(SoulOfGradient.class, SoulOfGradient::new, SoulOfGradient::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.GRADIENT_SOUL),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 30, 30),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 30, 30, 30),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 30, 30, 30),
            Weapons.TENDERIZER.getItem()
    )),


    // Raid Boss
//    PHYSIRA(Physira.class, Physira::new, Physira::new, new Utils.SimpleEntityEquipment(
//            SkullUtils.getSkullFrom(SkullID.GRADIENT_SOUL),
//            new ItemStack(Material.NETHERITE_CHESTPLATE),
//            new ItemStack(Material.NETHERITE_LEGGINGS),
//            new ItemStack(Material.NETHERITE_BOOTS),
//            Weapons.VIRIDIAN_BLADE.getItem()
//    )),

    //EVENTS
    EVENT_BOLTARO(EventBoltaro.class, EventBoltaro::new, EventBoltaro::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEMON),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 0),
            new ItemStack(Material.CHAINMAIL_LEGGINGS),
            new ItemStack(Material.CHAINMAIL_BOOTS),
            Weapons.DRAKEFANG.getItem()
    )),
    EVENT_BOLTARO_SHADOW(EventBoltaroShadow.class, EventBoltaroShadow::new, EventBoltaroShadow::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.END_MONSTER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 30, 0, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 30, 0, 0),
            Weapons.DEMONBLADE.getItem()
    )),
    EVENT_NARMER(EventNarmer.class, EventNarmer::new, EventNarmer::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.BURNING_WITHER_SKELETON),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 160, 160),
            ArmorManager.ArmorSets.GREATER_LEGGINGS.itemRed,
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 160, 160),
            Weapons.WALKING_STICK.getItem()
    )),
    EVENT_NARMER_ACOLYTE(EventNarmerAcolyte.class, EventNarmerAcolyte::new, EventNarmerAcolyte::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.RED_EYE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 30, 0, 15),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 30, 0, 15),
            Weapons.DEMONBLADE.getItem()
    )),
    EVENT_NARMER_DJER(EventDjer.class, EventDjer::new, EventDjer::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.ETHEREAL_WITHER_SKULL),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 160, 160),
            ArmorManager.ArmorSets.GREATER_LEGGINGS.itemRed,
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 160, 160),
            Weapons.WALKING_STICK.getItem()
    )),
    EVENT_NARMER_DJET(EventDjet.class, EventDjet::new, EventDjet::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.ETHEREAL_WITHER_SKULL),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 160, 160),
            ArmorManager.ArmorSets.GREATER_LEGGINGS.itemRed,
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 160, 160),
            Weapons.WALKING_STICK.getItem()
    )),
    EVENT_MITHRA(EventMithra.class, EventMithra::new, EventMithra::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.IRON_QUEEN),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 200, 200, 200),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 200, 200, 200),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 200, 200, 200),
            Weapons.SILVER_PHANTASM_SWORD_3.getItem()
    )),
    EVENT_MITHRA_FORSAKEN_FROST(EventForsakenFrost.class, EventForsakenFrost::new, EventForsakenFrost::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.WHITE_SPIDER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 255, 255),
            Weapons.FROSTBITE.getItem()
    )),
    EVENT_MITHRA_FORSAKEN_FOLIAGE(EventForsakenFoliage.class, EventForsakenFoliage::new, EventForsakenFoliage::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.JUNGLE_SPIDER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 14, 87, 9),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 14, 87, 9),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 14, 87, 9),
            Weapons.NEW_LEAF_SPEAR.getItem()
    )),
    EVENT_MITHRA_FORSAKEN_SHRIEKER(EventForsakenShrieker.class, EventForsakenShrieker::new, EventForsakenShrieker::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEEP_DARK_CRAWLER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 87, 9, 86),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 87, 9, 86),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 87, 9, 86),
            Weapons.SILVER_PHANTASM_SWORD_3.getItem()
    )),
    EVENT_MITHRA_FORSAKEN_RESPITE(EventForsakenRespite.class, EventForsakenRespite::new, EventForsakenRespite::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SPIDER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 120, 120, 120),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 120, 120, 120),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 120, 120, 120),
            Weapons.NOMEGUSTA.getItem()
    )),
    EVENT_MITHRA_FORSAKEN_CRUOR(EventForsakenCruor.class, EventForsakenCruor::new, EventForsakenCruor::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.BLOOD_SPIDER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 20, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 20, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 20, 20),
            Weapons.ARMBLADE.getItem()
    )),
    EVENT_MITHRA_FORSAKEN_DEGRADER(EventForsakenDegrader.class, EventForsakenDegrader::new, EventForsakenDegrader::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DECAPITATED_SPIDER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 5, 5, 5),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 5, 5, 5),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 5, 5, 5),
            Weapons.DRAKEFANG.getItem()
    )),
    EVENT_MITHRA_FORSAKEN_APPARITION(EventForsakenApparition.class, EventForsakenApparition::new, EventForsakenApparition::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SPIDER_SPIRIT),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 64, 140, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 64, 140, 255),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 64, 140, 255),
            Weapons.SILVER_PHANTASM_SWORD_4.getItem()
    )),
    EVENT_MITHRA_POISONOUS_SPIDER(EventPoisonousSpider.class, EventPoisonousSpider::new, EventPoisonousSpider::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.CAVE_SPIDER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 0, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 0, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 0, 0),
            null
    )),
    EVENT_MITHRA_EGG_SAC(EventEggSac.class, EventEggSac::new, EventEggSac::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.EGG_SAC),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 255, 255),
            null
    )),
    EVENT_ILLUSION_CORE(EventIllusionCore.class, EventIllusionCore::new, EventIllusionCore::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.ENCHANTMENT_CUBE),
            null,
            null,
            null
    )),
    EVENT_EXILED_CORE(EventExiledCore.class, EventExiledCore::new, EventExiledCore::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.FANCY_CUBE_3),
            null,
            null,
            null
    )),
    EVENT_CALAMITY_CORE(EventCalamityCore.class, EventCalamityCore::new, EventCalamityCore::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.EXPLOSION),
            null,
            null,
            null
    )),
    EVENT_ILLUMINA(EventIllumina.class, EventIllumina::new, EventIllumina::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEEP_DARK_WORM),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 120, 120, 200),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 120, 120, 200),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 120, 120, 200),
            Weapons.NEW_LEAF_SCYTHE.getItem()
    )),

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
            ZOMBIE_RAIDER, WANDER_WALKER, SKELETAL_ENTROPY, FIRE_SPLITTER
    };
    public static final Mob[] ELITE = {
            CELESTIAL_SWORD_WIELDER, CELESTIAL_OPUS, RIFT_WALKER, OVERGROWN_ZOMBIE,
            SKELETAL_PYROMANCER, SKELETAL_ANOMALY, SKELETAL_MESMER
    };
    public static final Mob[] CHAMPION = {
            NIGHTMARE_ZOMBIE, PIG_PARTICLE, EXTREME_ZEALOT, SMART_SKELETON
    };
    public static final Mob[] BOSS_MINIONS = {
            BOLTARO_SHADOW, BOLTARO_EXLIED,
            TORMENTED_SOUL, DEPRESSED_SOUL, FURIOUS_SOUL, VOLTAIC_SOUL, AGONIZED_SOUL,
            NARMER_ACOLYTE,
            ZENITH_LEGIONNAIRE,
            SOUL_OF_GRADIENT
    };
    public static final Mob[] BOSSES = {
            BOLTARO, GHOULCALLER, NARMER, MITHRA, ZENITH,
            CHESSKING, ILLUMINA, TORMENT, VOID, MAGMATIC_OOZE
    };
    public final Class<?> mobClass;
    @Deprecated
    public final Function<Location, AbstractMob<?>> createMobLegacy;
    public final Function7<Location, String, Integer, Float, Integer, Float, Float, AbstractMob<?>> createMobFunction;
    public final EntityEquipment equipment;
    public String name;
    public int maxHealth;
    public float walkSpeed;
    public int damageResistance;
    public float minMeleeDamage;
    public float maxMeleeDamage;

    Mob(
            Class<?> mobClass,
            Function<Location, AbstractMob<?>> createMobLegacy,
            Function7<Location, String, Integer, Float, Integer, Float, Float, AbstractMob<?>> createMobFunction,
            EntityEquipment equipment
    ) {
        this.createMobLegacy = createMobLegacy;
        this.createMobFunction = createMobFunction;
        this.mobClass = mobClass;
        this.equipment = equipment;
    }

    public ItemStack getHead() {
        if (equipment != null && equipment.getHelmet() != null) {
            return equipment.getHelmet();
        } else {
            if (AbstractZombie.class.isAssignableFrom(mobClass)) {
                return new ItemStack(Material.ZOMBIE_HEAD);
            } else if (AbstractSkeleton.class.isAssignableFrom(mobClass)) {
                return new ItemStack(Material.SKELETON_SKULL);
            } else if (AbstractSpider.class.isAssignableFrom(mobClass)) {
                return SkullUtils.getSkullFrom(SkullID.MC_SPIDER);
            } else if (AbstractSlime.class.isAssignableFrom(mobClass)) {
                return SkullUtils.getSkullFrom(SkullID.MC_SLIME);
            } else if (AbstractMagmaCube.class.isAssignableFrom(mobClass)) {
                return SkullUtils.getSkullFrom(SkullID.MC_MAGMACUBE);
            } else if (AbstractBlaze.class.isAssignableFrom(mobClass)) {
                return SkullUtils.getSkullFrom(SkullID.MC_BLAZE);
            } else if (AbstractWitch.class.isAssignableFrom(mobClass)) {
                return SkullUtils.getSkullFrom(SkullID.MC_WITCH);
            } else if (AbstractIronGolem.class.isAssignableFrom(mobClass)) {
                return SkullUtils.getSkullFrom(SkullID.MC_GOLEM);
            } else if (AbstractWitherSkeleton.class.isAssignableFrom(mobClass)) {
                return new ItemStack(Material.WITHER_SKELETON_SKULL);
            } else if (AbstractPigZombie.class.isAssignableFrom(mobClass)) {
                return SkullUtils.getSkullFrom(SkullID.MC_PIGLIN);
            } else if (AbstractWolf.class.isAssignableFrom(mobClass)) {
                return SkullUtils.getSkullFrom(SkullID.MC_ANGRY_WOLF);
            }
            return new ItemStack(Material.BARRIER);
        }
    }

    public AbstractMob<?> createMob(Location spawnLocation) {
        return createMobFunction.apply(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    public enum MobGroup {
        BASIC(Mob.BASIC, "Basic", NamedTextColor.YELLOW, SkullUtils.getSkullFrom(SkullID.YELLOW_1)),
        INTERMEDIATE(Mob.INTERMEDIATE, "Intermediate", NamedTextColor.GOLD, SkullUtils.getSkullFrom(SkullID.GOLD_2)),
        ADVANCED(Mob.ADVANCED, "Advanced", NamedTextColor.GREEN, SkullUtils.getSkullFrom(SkullID.LIME_3)),
        ELITE(Mob.ELITE, "Elite", NamedTextColor.DARK_GREEN, SkullUtils.getSkullFrom(SkullID.GREEN_4)),
        CHAMPION(Mob.CHAMPION, "Champion", NamedTextColor.BLUE, SkullUtils.getSkullFrom(SkullID.BLUE_5)),
        BOSS_MINIONS(Mob.BOSS_MINIONS, "Boss Minion", NamedTextColor.RED, SkullUtils.getSkullFrom(SkullID.PINK_6)),
        BOSSES(Mob.BOSSES, "Boss", NamedTextColor.DARK_RED, SkullUtils.getSkullFrom(SkullID.RED_7)),
        ALL(Mob.VALUES, "All", NamedTextColor.BLACK, SkullUtils.getSkullFrom(SkullID.YELLOW_1)),

        ;

        public static final MobGroup[] VALUES = values();

        public final Mob[] mobs;
        public final String name;
        public final TextColor textColor;
        public final ItemStack head;

        MobGroup(Mob[] mobs, String name, TextColor textColor, ItemStack head) {
            this.mobs = mobs;
            this.name = name;
            this.textColor = textColor;
            this.head = head;
        }
    }
}
