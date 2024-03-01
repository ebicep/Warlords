package com.ebicep.warlords.pve.mobs;

import com.ebicep.warlords.game.option.towerdefense.mobs.TDSkeleton;
import com.ebicep.warlords.game.option.towerdefense.mobs.TDZombie;
import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.pve.mobs.blaze.BlazingKindle;
import com.ebicep.warlords.pve.mobs.bosses.Void;
import com.ebicep.warlords.pve.mobs.bosses.*;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.*;
import com.ebicep.warlords.pve.mobs.creeper.CreepyBomber;
import com.ebicep.warlords.pve.mobs.events.baneofimpurities.EventCalamityCore;
import com.ebicep.warlords.pve.mobs.events.baneofimpurities.EventExiledCore;
import com.ebicep.warlords.pve.mobs.events.baneofimpurities.EventIllumina;
import com.ebicep.warlords.pve.mobs.events.baneofimpurities.EventIllusionCore;
import com.ebicep.warlords.pve.mobs.events.boltarobonanza.EventBoltaro;
import com.ebicep.warlords.pve.mobs.events.boltarobonanza.EventBoltaroShadow;
import com.ebicep.warlords.pve.mobs.events.gardenofhesperides.*;
import com.ebicep.warlords.pve.mobs.events.libraryarchives.*;
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
import com.ebicep.warlords.pve.mobs.player.Animus;
import com.ebicep.warlords.pve.mobs.player.CryoPod;
import com.ebicep.warlords.pve.mobs.player.Decoy;
import com.ebicep.warlords.pve.mobs.player.TestDummy;
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
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import com.mojang.datafixers.util.Function7;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum Mob {

    // Basic
    ZOMBIE_LANCER(EntityType.ZOMBIE, ZombieLancer.class, ZombieLancer::new, ZombieLancer::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.PURPLE_KNIGHT),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
            Weapons.STEEL_SWORD.getItem()
    )),
    BASIC_WARRIOR_BERSERKER(EntityType.ZOMBIE, BasicWarriorBerserker.class, BasicWarriorBerserker::new, BasicWarriorBerserker::new, new Utils.SimpleEntityEquipment(
            ArmorManager.Helmets.SIMPLE_WARRIOR_HELMET.itemRed,
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
            new ItemStack(Material.WOODEN_SWORD)
    )),
    SKELETAL_MAGE(EntityType.SKELETON, SkeletalMage.class, SkeletalMage::new, SkeletalMage::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.FACELESS_MAGE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
            new ItemStack(Material.BOW)
    )),
    PIG_DISCIPLE(EntityType.PIGLIN, PigDisciple.class, PigDisciple::new, PigDisciple::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SAMURAI),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
            Weapons.SILVER_PHANTASM_TRIDENT.getItem()
    )),
    SLIMY_ANOMALY(EntityType.SLIME, SlimyAnomaly.class, SlimyAnomaly::new, SlimyAnomaly::new, null),
    ARACHNO_VENARI(EntityType.SPIDER, ArachnoVenari.class, ArachnoVenari::new, ArachnoVenari::new, null),

    // Intermediate
    HOUND(EntityType.WOLF, Hound.class, Hound::new, Hound::new, null),
    INTERMEDIATE_WARRIOR_BERSERKER(EntityType.ZOMBIE,
            IntermediateWarriorBerserker.class,
            IntermediateWarriorBerserker::new,
            IntermediateWarriorBerserker::new,
            new Utils.SimpleEntityEquipment(
                    ArmorManager.Helmets.GREATER_WARRIOR_HELMET.itemRed,
                    new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                    new ItemStack(Material.CHAINMAIL_LEGGINGS),
                    new ItemStack(Material.CHAINMAIL_BOOTS),
                    new ItemStack(Material.PRISMARINE_SHARD)
            )
    ),
    SKELETAL_WARLOCK(EntityType.SKELETON, SkeletalWarlock.class, SkeletalWarlock::new, SkeletalWarlock::new, new Utils.SimpleEntityEquipment(
            new ItemStack(Material.ORANGE_CARPET),
            new ItemStack(Material.CHAINMAIL_CHESTPLATE),
            new ItemStack(Material.CHAINMAIL_LEGGINGS),
            new ItemStack(Material.CHAINMAIL_BOOTS),
            new ItemStack(Material.BOW)
    )),
    PIG_SHAMAN(EntityType.PIGLIN, PigShaman.class, PigShaman::new, PigShaman::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SAMURAI),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
            new ItemStack(Material.DIAMOND_LEGGINGS),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
            new ItemStack(Material.COOKIE)
    )),
    PIG_ALLEVIATOR(EntityType.PIGLIN, PigAlleviator.class, PigAlleviator::new, PigAlleviator::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SAMURAI),
            new ItemStack(Material.DIAMOND_HELMET),
            new ItemStack(Material.DIAMOND_LEGGINGS),
            new ItemStack(Material.DIAMOND_BOOTS),
            new ItemStack(Material.BAKED_POTATO)
    )),
    WITCH_DEACON(EntityType.WITCH, WitchDeacon.class, WitchDeacon::new, WitchDeacon::new, null),
    BLAZING_KINDLE(EntityType.BLAZE, BlazingKindle.class, BlazingKindle::new, BlazingKindle::new, null),
    WANDER_KNIGHTS(EntityType.ZOMBIE, WanderKnights.class, WanderKnights::new, WanderKnights::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.FANCY_CUBE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 105, 147, 158),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 105, 147, 158),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 105, 147, 158),
            Weapons.LUNAR_RELIC.getItem()
    )),
    ZOMBIE_SWORDSMAN(EntityType.ZOMBIE, ZombieSwordsman.class, ZombieSwordsman::new, ZombieSwordsman::new, new Utils.SimpleEntityEquipment(
            new ItemStack(Material.WHITE_CARPET),
            new ItemStack(Material.CHAINMAIL_CHESTPLATE),
            new ItemStack(Material.CHAINMAIL_LEGGINGS),
            new ItemStack(Material.CHAINMAIL_BOOTS),
            new ItemStack(Material.PRISMARINE_SHARD)
    )),
    ZOMBIE_LAMENT(EntityType.ZOMBIE, ZombieLament.class, ZombieLament::new, ZombieLament::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.BLUE_GHOST),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 69, 176),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 69, 176),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 69, 176),
            Weapons.SILVER_PHANTASM_STAFF_2.getItem()
    )),

    // Advanced
    ILLUMINATION(EntityType.MAGMA_CUBE, Illumination.class, Illumination::new, Illumination::new, null),
    GOLEM_APPRENTICE(EntityType.IRON_GOLEM, GolemApprentice.class, GolemApprentice::new, GolemApprentice::new, null),
    SCRUPULOUS_ZOMBIE(EntityType.ZOMBIE, ScrupulousZombie.class, ScrupulousZombie::new, ScrupulousZombie::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SCULK_CORRUPTION),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 10, 50, 130),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 10, 50, 130),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 10, 50, 130),
            Weapons.AMARANTH.getItem()
    )),
    SLIME_GUARD(EntityType.ZOMBIE, SlimeGuard.class, SlimeGuard::new, SlimeGuard::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SLIME_BLOCK),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 106, 255, 106),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 106, 255, 106),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 106, 255, 106),
            Weapons.NEW_LEAF_SPEAR.getItem()
    )),
    CELESTIAL_BOW_WIELDER(EntityType.SKELETON, CelestialBowWielder.class, CelestialBowWielder::new, CelestialBowWielder::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.BOW_HEAD),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 20, 20),
            Weapons.FROSTBITE.getItem()
    )),
    ZOMBIE_VANGUARD(EntityType.ZOMBIE, ZombieVanguard.class, ZombieVanguard::new, ZombieVanguard::new, new Utils.SimpleEntityEquipment(
            ArmorManager.Helmets.LEGENDARY_PALADIN_HELMET.itemRed,
            new ItemStack(Material.DIAMOND_CHESTPLATE),
            new ItemStack(Material.DIAMOND_LEGGINGS),
            new ItemStack(Material.DIAMOND_BOOTS),
            Weapons.FELFLAME_BLADE.getItem()
    )),
    ADVANCED_WARRIOR_BERSERKER(EntityType.ZOMBIE, AdvancedWarriorBerserker.class, AdvancedWarriorBerserker::new, AdvancedWarriorBerserker::new, new Utils.SimpleEntityEquipment(
            ArmorManager.Helmets.LEGENDARY_WARRIOR_HELMET.itemRed,
            new ItemStack(Material.DIAMOND_CHESTPLATE),
            new ItemStack(Material.DIAMOND_LEGGINGS),
            new ItemStack(Material.DIAMOND_BOOTS),
            new ItemStack(Material.COOKED_SALMON)
    )),
    VOID_ZOMBIE(EntityType.ZOMBIE, VoidZombie.class, VoidZombie::new, VoidZombie::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.FACELESS_BANDIT),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 0, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 0, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 0, 0),
            Weapons.VOID_EDGE.getItem()
    )),
    ZOMBIE_KNIGHT(EntityType.ZOMBIE, ZombieKnight.class, ZombieKnight::new, ZombieKnight::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.NETHERITE_HELMET),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 20, 20),
            Weapons.GEMINI.getItem()
    )),
    SLIMY_CHESS(EntityType.SLIME, SlimyChess.class, SlimyChess::new, SlimyChess::new, null),
    ZOMBIE_RAIDER(EntityType.ZOMBIE, ZombieRaider.class, ZombieRaider::new, ZombieRaider::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.FANCY_CUBE_2),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 56, 71, 74),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 56, 71, 74),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 56, 71, 74),
            Weapons.NETHERSTEEL_KATANA.getItem()
    )),
    WANDER_WALKER(EntityType.ZOMBIE, WanderWalker.class, WanderWalker::new, WanderWalker::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SCULK_MONSTER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 204, 204),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 204, 204),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 204, 204),
            Weapons.LUNAR_JUSTICE.getItem()
    )),
    SKELETAL_ENTROPY(EntityType.SKELETON, SkeletalEntropy.class, SkeletalEntropy::new, SkeletalEntropy::new, new Utils.SimpleEntityEquipment(
            new ItemStack(Material.PINK_CARPET),
            new ItemStack(Material.DIAMOND_CHESTPLATE),
            new ItemStack(Material.DIAMOND_LEGGINGS),
            new ItemStack(Material.DIAMOND_BOOTS),
            Weapons.VOID_TWIG.getItem()
    )),
    FIRE_SPLITTER(EntityType.ZOMBIE, FireSplitter.class, FireSplitter::new, FireSplitter::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.LAVA_MONSTER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 252, 170, 53),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 252, 170, 53),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 252, 170, 53),
            Weapons.SILVER_PHANTASM_SWORD_2.getItem()
    )),

    // Elite
    CELESTIAL_SWORD_WIELDER(EntityType.ZOMBIE, CelestialSwordWielder.class, CelestialSwordWielder::new, CelestialSwordWielder::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SWORD_HEAD),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 20, 20),
            Weapons.DIAMONDSPARK.getItem()
    )),
    CELESTIAL_OPUS(EntityType.WITHER_SKELETON, CelestialOpus.class, CelestialOpus::new, CelestialOpus::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.CELESTIAL_GOLDOR),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 40, 40, 40),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 40, 40, 40),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 40, 40, 40),
            Weapons.SILVER_PHANTASM_SAWBLADE.getItem()
    )),
    RIFT_WALKER(EntityType.ZOMBIE, RiftWalker.class, RiftWalker::new, RiftWalker::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.PURPLE_RIFT),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 229, 69, 176),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 229, 69, 176),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 229, 69, 176),
            Weapons.VORPAL_SWORD.getItem()
    )),
    OVERGROWN_ZOMBIE(EntityType.ZOMBIE, OvergrownZombie.class, OvergrownZombie::new, OvergrownZombie::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.GREEN_LANCER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 130, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 130, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 130, 20),
            Weapons.NEW_LEAF_AXE.getItem()
    )),
    SKELETAL_PYROMANCER(EntityType.SKELETON, SkeletalPyromancer.class, SkeletalPyromancer::new, SkeletalPyromancer::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.WITHER_SOUL),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 29, 49, 64),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 29, 49, 64),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 29, 49, 64),
            null
    )),
    SKELETAL_ANOMALY(EntityType.SKELETON, SkeletalAnomaly.class, SkeletalAnomaly::new, SkeletalAnomaly::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SEEK_DOORS),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 64, 64, 64),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 64, 64, 64),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 64, 64, 64),
            Weapons.FABLED_HEROICS_SWORD.getItem()
    )),
    SKELETAL_MESMER(EntityType.SKELETON, SkeletalMesmer.class, SkeletalMesmer::new, SkeletalMesmer::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEMON_SKELETON),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 0, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 0, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 0, 0),
            Weapons.ARMBLADE.getItem()
    )),
    CREEPY_BOMBER(EntityType.CREEPER, CreepyBomber.class, CreepyBomber::new, CreepyBomber::new, null),
    SKELETAL_ARCHER(EntityType.SKELETON, SkeletalArcher.class, SkeletalArcher::new, SkeletalArcher::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SKELETON_ARCHER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 226, 226, 226),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 226, 226, 226),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 226, 226, 226),
            Weapons.FABLED_HEROICS_SCYTHE.getItem()
    )),

    // Champion
    NIGHTMARE_ZOMBIE(EntityType.ZOMBIE, NightmareZombie.class, NightmareZombie::new, NightmareZombie::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SHADOW_DEMON),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 70, 50, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 70, 50, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 70, 50, 20),
            Weapons.FABLED_HEROICS_SWORD.getItem()
    )),
    PIG_PARTICLE(EntityType.ZOMBIE, PigParticle.class, PigParticle::new, PigParticle::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.HOODED_KNIGHT),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 20, 20),
            Weapons.NETHERSTEEL_KATANA.getItem()
    )),
    EXTREME_ZEALOT(EntityType.ZOMBIE, ExtremeZealot.class, ExtremeZealot::new, ExtremeZealot::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.FANCY_CUBE_2),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 56, 71, 74),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 56, 71, 74),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 56, 71, 74),
            Weapons.VENOMSTRIKE.getItem()
    )),
    SMART_SKELETON(EntityType.ZOMBIE, SmartSkeleton.class, SmartSkeleton::new, SmartSkeleton::new, new Utils.SimpleEntityEquipment(
            null,
            null,
            null,
            null,
            new ItemStack(Material.BOW)
    )),
    SKELETAL_SORCERER(EntityType.SKELETON, SkeletalSorcerer.class, SkeletalSorcerer::new, SkeletalSorcerer::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.WHITE_SHEKEL),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 255, 255),
            Weapons.SILVER_PHANTASM_SWORD_4.getItem()
    )),


    // Boss
    BOLTARO(EntityType.ZOMBIE, Boltaro.class, Boltaro::new, Boltaro::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEMON),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 0),
            new ItemStack(Material.CHAINMAIL_LEGGINGS),
            new ItemStack(Material.CHAINMAIL_BOOTS),
            Weapons.DRAKEFANG.getItem()
    )),
    GHOULCALLER(EntityType.ZOMBIE, Ghoulcaller.class, Ghoulcaller::new, Ghoulcaller::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEMON_SKELETON),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 170, 170, 170),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 170, 170, 170),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 170, 170, 170),
            Weapons.ENDERFIST.getItem()
    )),
    NARMER(EntityType.ZOMBIE, Narmer.class, Narmer::new, Narmer::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.BURNING_WITHER_SKELETON),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 160, 160),
            ArmorManager.ArmorSets.GREATER_LEGGINGS.itemRed,
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 160, 160),
            Weapons.WALKING_STICK.getItem()
    )),
    MITHRA(EntityType.ZOMBIE, Mithra.class, Mithra::new, Mithra::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.IRON_QUEEN),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 200, 200, 200),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 200, 200, 200),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 200, 200, 200),
            Weapons.SILVER_PHANTASM_SWORD_3.getItem()
    )),
    ZENITH(EntityType.ZOMBIE, Zenith.class, Zenith::new, Zenith::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.PURPLE_ENDERMAN),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 255),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 255),
            Weapons.VORPAL_SWORD.getItem()
    )),
    CHESSKING(EntityType.SLIME, Chessking.class, Chessking::new, Chessking::new, null),
    ILLUMINA(EntityType.ZOMBIE, Illumina.class, Illumina::new, Illumina::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEEP_DARK_WORM),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 120, 120, 200),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 120, 120, 200),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 120, 120, 200),
            Weapons.NEW_LEAF_SCYTHE.getItem()
    )),
    TORMENT(EntityType.ZOMBIE, Torment.class, Torment::new, Torment::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEMON_KING),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 230, 60, 60),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 230, 60, 60),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 230, 60, 60),
            Weapons.SILVER_PHANTASM_TRIDENT.getItem()
    )),
    VOID(EntityType.SKELETON, Void.class, Void::new, Void::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.END_MONSTER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 20, 20),
            Weapons.VOID_EDGE.getItem()
    )),
    MAGMATIC_OOZE(EntityType.MAGMA_CUBE, MagmaticOoze.class, MagmaticOoze::new, MagmaticOoze::new, null),
    ENAVURIS(EntityType.ENDERMAN, Enavuris.class, Enavuris::new, Enavuris::new, null),


    // Boss minions
    BOLTARO_SHADOW(EntityType.ZOMBIE, BoltaroShadow.class, BoltaroShadow::new, BoltaroShadow::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.END_MONSTER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 30, 0, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 30, 0, 0),
            Weapons.DEMONBLADE.getItem()
    )),
    BOLTARO_EXLIED(EntityType.SKELETON, BoltaroExiled.class, BoltaroExiled::new, BoltaroExiled::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.RED_EYE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
            Weapons.GEMINI.getItem()
    )),
    TORMENTED_SOUL(EntityType.ZOMBIE, TormentedSoul.class, TormentedSoul::new, TormentedSoul::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.GHOST_RED),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 140, 140, 140),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 140, 140, 15),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 140, 140, 140),
            Weapons.CLAWS.getItem()
    )),
    DEPRESSED_SOUL(EntityType.ZOMBIE, DepressedSoul.class, DepressedSoul::new, DepressedSoul::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.GHOST_BLUE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 140, 140, 140),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 140, 140, 15),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 140, 140, 140),
            Weapons.CLAWS.getItem()
    )),
    FURIOUS_SOUL(EntityType.ZOMBIE, FuriousSoul.class, FuriousSoul::new, FuriousSoul::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.GHOST_MAGENTA),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 140, 140, 140),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 140, 140, 15),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 140, 140, 140),
            Weapons.CLAWS.getItem()
    )),
    VOLTAIC_SOUL(EntityType.ZOMBIE, VoltaicSoul.class, VoltaicSoul::new, VoltaicSoul::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.GHOST_PURPLE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 140, 140, 140),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 140, 140, 15),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 140, 140, 140),
            Weapons.CLAWS.getItem()
    )),
    AGONIZED_SOUL(EntityType.ZOMBIE, AgonizedSoul.class, AgonizedSoul::new, AgonizedSoul::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.GHOST_GRAY),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 140, 140, 140),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 140, 140, 15),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 140, 140, 140),
            Weapons.CLAWS.getItem()
    )),
    NARMER_ACOLYTE(EntityType.ZOMBIE, NarmerAcolyte.class, NarmerAcolyte::new, NarmerAcolyte::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.RED_EYE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 30, 0, 15),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 30, 0, 15),
            Weapons.DEMONBLADE.getItem()
    )),
    NARMERS_DEATH_CHARGE(EntityType.PRIMED_TNT, NarmersDeathCharge.class, NarmersDeathCharge::new, NarmersDeathCharge::new, new Utils.SimpleEntityEquipment(
            new ItemStack(Material.TNT),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 30, 0, 15),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 30, 0, 15),
            Weapons.DEMONBLADE.getItem()
    )),
    ZENITH_LEGIONNAIRE(EntityType.ZOMBIE, ZenithLegionnaire.class, ZenithLegionnaire::new, ZenithLegionnaire::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.FACELESS_BANDIT),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 100, 0, 80),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 100, 0, 80),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 100, 0, 80),
            Weapons.LUNAR_JUSTICE.getItem()
    )),
    SOUL_OF_GRADIENT(EntityType.ZOMBIE, SoulOfGradient.class, SoulOfGradient::new, SoulOfGradient::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.GRADIENT_SOUL),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 30, 30),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 30, 30, 30),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 30, 30, 30),
            Weapons.TENDERIZER.getItem()
    )),
    MITHRA_EGG_SAC(EntityType.ZOMBIE, EggSac.class, EggSac::new, EggSac::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.EGG_SAC),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 255, 255),
            null
    )),
    ARACHNO_VENERATUS(EntityType.SPIDER, ArachnoVeneratus.class, ArachnoVeneratus::new, ArachnoVeneratus::new, null),
    CURSED_PSION(EntityType.WITHER_SKELETON, CursedPsion.class, CursedPsion::new, CursedPsion::new, null),
    ENAVURITE(EntityType.ENDERMITE, Enavurite.class, Enavurite::new, Enavurite::new, null),


    // Raid Boss
//    PHYSIRA(Physira.class, Physira::new, Physira::new, new Utils.SimpleEntityEquipment(
//            SkullUtils.getSkullFrom(SkullID.GRADIENT_SOUL),
//            new ItemStack(Material.NETHERITE_CHESTPLATE),
//            new ItemStack(Material.NETHERITE_LEGGINGS),
//            new ItemStack(Material.NETHERITE_BOOTS),
//            Weapons.VIRIDIAN_BLADE.getItem()
//    )),

    //EVENTS
    EVENT_BOLTARO(EntityType.ZOMBIE, EventBoltaro.class, EventBoltaro::new, EventBoltaro::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEMON),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 0),
            new ItemStack(Material.CHAINMAIL_LEGGINGS),
            new ItemStack(Material.CHAINMAIL_BOOTS),
            Weapons.DRAKEFANG.getItem()
    )),
    EVENT_BOLTARO_SHADOW(EntityType.ZOMBIE, EventBoltaroShadow.class, EventBoltaroShadow::new, EventBoltaroShadow::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.END_MONSTER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 30, 0, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 30, 0, 0),
            Weapons.DEMONBLADE.getItem()
    )),
    EVENT_NARMER(EntityType.ZOMBIE, EventNarmer.class, EventNarmer::new, EventNarmer::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.BURNING_WITHER_SKELETON),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 160, 160),
            ArmorManager.ArmorSets.GREATER_LEGGINGS.itemRed,
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 160, 160),
            Weapons.WALKING_STICK.getItem()
    )),
    EVENT_NARMER_ACOLYTE(EntityType.ZOMBIE, EventNarmerAcolyte.class, EventNarmerAcolyte::new, EventNarmerAcolyte::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.RED_EYE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 30, 0, 15),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 30, 0, 15),
            Weapons.DEMONBLADE.getItem()
    )),
    EVENT_NARMER_DJER(EntityType.ZOMBIE, EventDjer.class, EventDjer::new, EventDjer::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.ETHEREAL_WITHER_SKULL),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 160, 160),
            ArmorManager.ArmorSets.GREATER_LEGGINGS.itemRed,
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 160, 160),
            Weapons.WALKING_STICK.getItem()
    )),
    EVENT_NARMER_DJET(EntityType.ZOMBIE, EventDjet.class, EventDjet::new, EventDjet::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.ETHEREAL_WITHER_SKULL),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 160, 160),
            ArmorManager.ArmorSets.GREATER_LEGGINGS.itemRed,
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 160, 160),
            Weapons.WALKING_STICK.getItem()
    )),
    EVENT_MITHRA(EntityType.ZOMBIE, EventMithra.class, EventMithra::new, EventMithra::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.IRON_QUEEN),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 200, 200, 200),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 200, 200, 200),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 200, 200, 200),
            Weapons.SILVER_PHANTASM_SWORD_3.getItem()
    )),
    EVENT_MITHRA_FORSAKEN_FROST(EntityType.ZOMBIE, EventForsakenFrost.class, EventForsakenFrost::new, EventForsakenFrost::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.WHITE_SPIDER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 255, 255),
            Weapons.FROSTBITE.getItem()
    )),
    EVENT_MITHRA_FORSAKEN_FOLIAGE(EntityType.ZOMBIE, EventForsakenFoliage.class, EventForsakenFoliage::new, EventForsakenFoliage::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.JUNGLE_SPIDER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 14, 87, 9),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 14, 87, 9),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 14, 87, 9),
            Weapons.NEW_LEAF_SPEAR.getItem()
    )),
    EVENT_MITHRA_FORSAKEN_SHRIEKER(EntityType.ZOMBIE, EventForsakenShrieker.class, EventForsakenShrieker::new, EventForsakenShrieker::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEEP_DARK_CRAWLER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 87, 9, 86),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 87, 9, 86),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 87, 9, 86),
            Weapons.SILVER_PHANTASM_SWORD_3.getItem()
    )),
    EVENT_MITHRA_FORSAKEN_RESPITE(EntityType.ZOMBIE, EventForsakenRespite.class, EventForsakenRespite::new, EventForsakenRespite::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SPIDER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 120, 120, 120),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 120, 120, 120),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 120, 120, 120),
            Weapons.NOMEGUSTA.getItem()
    )),
    EVENT_MITHRA_FORSAKEN_CRUOR(EntityType.ZOMBIE, EventForsakenCruor.class, EventForsakenCruor::new, EventForsakenCruor::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.BLOOD_SPIDER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 20, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 20, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 20, 20),
            Weapons.ARMBLADE.getItem()
    )),
    EVENT_MITHRA_FORSAKEN_DEGRADER(EntityType.ZOMBIE, EventForsakenDegrader.class, EventForsakenDegrader::new, EventForsakenDegrader::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DECAPITATED_SPIDER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 5, 5, 5),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 5, 5, 5),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 5, 5, 5),
            Weapons.DRAKEFANG.getItem()
    )),
    EVENT_MITHRA_FORSAKEN_APPARITION(EntityType.ZOMBIE, EventForsakenApparition.class, EventForsakenApparition::new, EventForsakenApparition::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SPIDER_SPIRIT),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 64, 140, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 64, 140, 255),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 64, 140, 255),
            Weapons.SILVER_PHANTASM_SWORD_4.getItem()
    )),
    EVENT_MITHRA_POISONOUS_SPIDER(EntityType.ZOMBIE, EventPoisonousSpider.class, EventPoisonousSpider::new, EventPoisonousSpider::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.CAVE_SPIDER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 0, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 0, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 0, 0),
            null
    )),
    EVENT_MITHRA_EGG_SAC(EntityType.ARMOR_STAND, EventEggSac.class, EventEggSac::new, EventEggSac::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.EGG_SAC),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 255, 255),
            null
    )),
    EVENT_ILLUSION_CORE(EntityType.ARMOR_STAND, EventIllusionCore.class, EventIllusionCore::new, EventIllusionCore::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.ENCHANTMENT_CUBE),
            null,
            null,
            null
    )),
    EVENT_EXILED_CORE(EntityType.ARMOR_STAND, EventExiledCore.class, EventExiledCore::new, EventExiledCore::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.FANCY_CUBE_3),
            null,
            null,
            null
    )),
    EVENT_CALAMITY_CORE(EntityType.ARMOR_STAND, EventCalamityCore.class, EventCalamityCore::new, EventCalamityCore::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.EXPLOSION),
            null,
            null,
            null
    )),
    EVENT_ILLUMINA(EntityType.ZOMBIE, EventIllumina.class, EventIllumina::new, EventIllumina::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEEP_DARK_WORM),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 120, 120, 200),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 120, 120, 200),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 120, 120, 200),
            Weapons.NEW_LEAF_SCYTHE.getItem()
    )),
    EVENT_APOLLO(EntityType.SKELETON, EventApollo.class, EventApollo::new, EventApollo::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.APOLLO),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 140, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 140, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 140, 0),
            new ItemBuilder(Material.BOW)
                    .enchant(Enchantment.OXYGEN, 1)
                    .get()
    )),
    EVENT_ARES(EntityType.ZOMBIE, EventAres.class, EventAres::new, EventAres::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.ARES),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 140, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 140, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 140, 0),
            Weapons.VOID_TWIG.getItem()
    )),
    EVENT_PROMETHEUS(EntityType.ZOMBIE, EventPrometheus.class, EventPrometheus::new, EventPrometheus::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.HERMES),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 140, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 140, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 140, 0),
            Weapons.ARMBLADE.getItem()
    )),
    EVENT_ATHENA(EntityType.ZOMBIE, EventAthena.class, EventAthena::new, EventAthena::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.APHRODITE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 140, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 140, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 140, 0),
            Weapons.NEW_LEAF_AXE.getItem()
    )),
    EVENT_CRONUS(EntityType.ZOMBIE, EventCronus.class, EventCronus::new, EventCronus::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.BUST_ZEUS),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 140, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 140, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 140, 0),
            Weapons.LUNAR_JUSTICE.getItem()
    )),
    EVENT_ZEUS(EntityType.ZOMBIE, EventZeus.class, EventZeus::new, EventZeus::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.ZEUS),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 236, 236, 236),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 236, 236, 236),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 236, 236, 236),
            Weapons.SEVENTH.getItem()
    )),
    EVENT_POSEIDON(EntityType.ZOMBIE, EventPoseidon.class, EventPoseidon::new, EventPoseidon::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.POSEIDON),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 0, 205),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 0, 205),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 0, 205),
            Weapons.SILVER_PHANTASM_TRIDENT.getItem()
    )),
    EVENT_HADES(EntityType.ZOMBIE, EventHades.class, EventHades::new, EventHades::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DARK_WRAITH),
            new ItemStack((Material.NETHERITE_CHESTPLATE)),
            new ItemStack(Material.NETHERITE_LEGGINGS),
            new ItemStack(Material.NETHERITE_BOOTS),
            Weapons.FABLED_HEROICS_SCYTHE.getItem()
    )),
    EVENT_TERAS_MINOTAUR(EntityType.ZOMBIE, EventTerasMinotaur.class, EventTerasMinotaur::new, EventTerasMinotaur::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.MINOTAUR),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 51, 102),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 51, 102),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 51, 102),
            Weapons.NOMEGUSTA.getItem()
    )),
    EVENT_TERAS_CYCLOPS(EntityType.ZOMBIE, EventTerasCyclops.class, EventTerasCyclops::new, EventTerasCyclops::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.CYCLOPS),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 102, 51, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 102, 51, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 102, 51, 0),
            Weapons.HAMMER_OF_LIGHT.getItem()
    )),
    EVENT_TERAS_SIREN(EntityType.ZOMBIE, EventTerasSiren.class, EventTerasSiren::new, EventTerasSiren::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SIREN),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 102, 0, 51),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 102, 0, 51),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 102, 0, 51),
            Weapons.FABLED_HEROICS_SWORD.getItem()
    )),
    EVENT_TERAS_DRYAD(EntityType.ZOMBIE, EventTerasDryad.class, EventTerasDryad::new, EventTerasDryad::new, null),

    EVENT_UNPUBLISHED_GRIMOIRE(EntityType.PLAYER, EventUnpublishedGrimoire.class, EventUnpublishedGrimoire::new, EventUnpublishedGrimoire::new, new Utils.SimpleEntityEquipment(
            null,
            null,
            null,
            null,
            Weapons.TENDERIZER.getItem()
    )),
    EVENT_EMBELLISHED_GRIMOIRE(EntityType.PLAYER, EventEmbellishedGrimoire.class, EventEmbellishedGrimoire::new, EventEmbellishedGrimoire::new, new Utils.SimpleEntityEquipment(
            null,
            null,
            null,
            null,
            Weapons.ZWEIREAPER.getItem()
    )),
    EVENT_SCRIPTED_GRIMOIRE(EntityType.PLAYER, EventScriptedGrimoire.class, EventScriptedGrimoire::new, EventScriptedGrimoire::new, new Utils.SimpleEntityEquipment(
            null,
            null,
            null,
            null,
            Weapons.GEMCRUSHER.getItem()
    )),
    EVENT_ROUGE_GRIMOIRE(EntityType.PLAYER, EventRougeGrimoire.class, EventRougeGrimoire::new, EventRougeGrimoire::new, new Utils.SimpleEntityEquipment(
            null,
            null,
            null,
            null,
            Weapons.RUBY_THORN.getItem()
    )),
    EVENT_VIOLETTE_GRIMOIRE(EntityType.PLAYER, EventVioletteGrimoire.class, EventVioletteGrimoire::new, EventVioletteGrimoire::new, new Utils.SimpleEntityEquipment(
            null,
            null,
            null,
            null,
            Weapons.FROSTBITE.getItem()
    )),
    EVENT_BLEUE_GRIMOIRE(EntityType.PLAYER, EventBleueGrimoire.class, EventBleueGrimoire::new, EventBleueGrimoire::new, new Utils.SimpleEntityEquipment(
            null,
            null,
            null,
            null,
            Weapons.LUNAR_JUSTICE.getItem()
    )),
    EVENT_ORANGE_GRIMOIRE(EntityType.PLAYER, EventOrangeGrimoire.class, EventOrangeGrimoire::new, EventOrangeGrimoire::new, new Utils.SimpleEntityEquipment(
            null,
            null,
            null,
            null,
            Weapons.FABLED_HEROICS_SWORD_2.getItem()
    )),

    EVENT_NECRONOMICON_GRIMOIRE(EntityType.PLAYER, EventNecronomiconGrimoire.class, EventNecronomiconGrimoire::new, EventNecronomiconGrimoire::new, new Utils.SimpleEntityEquipment(
            null,
            null,
            null,
            null,
            Weapons.SILVER_PHANTASM_SWORD_2.getItem()
    )),
    EVENT_THE_ARCHIVIST(EntityType.PLAYER, EventTheArchivist.class, EventTheArchivist::new, EventTheArchivist::new, new Utils.SimpleEntityEquipment(
            null,
            null,
            null,
            null,
            Weapons.SILVER_PHANTASM_STAFF_2.getItem()
    )),
    EVENT_INQUISITEUR_EWA(EntityType.IRON_GOLEM, EventInquisiteurEWA.class, EventInquisiteurEWA::new, EventInquisiteurEWA::new, null),
    EVENT_INQUISITEUR_EGA(EntityType.IRON_GOLEM, EventInquisiteurEGA.class, EventInquisiteurEGA::new, EventInquisiteurEGA::new, null),
    EVENT_INQUISITEUR_VPA(EntityType.IRON_GOLEM, EventInquisiteurVPA.class, EventInquisiteurVPA::new, EventInquisiteurVPA::new, null),

    //misc
    TEST_DUMMY(EntityType.ZOMBIE, TestDummy.class, TestDummy::new, TestDummy::new, null),
    CRYOPOD(EntityType.ARMOR_STAND, CryoPod.class, CryoPod::new, CryoPod::new, null),
    DECOY(EntityType.ARMOR_STAND, Decoy.class, Decoy::new, Decoy::new, null),
    ANIMUS(EntityType.PLAYER, Animus.class, Animus::new, Animus::new, null),

    // tower defense
    TD_ZOMBIE(EntityType.ZOMBIE, TDZombie.class, TDZombie::new, TDZombie::new, null),
    TD_SKELETON(EntityType.SKELETON, TDSkeleton.class, TDSkeleton::new, TDSkeleton::new, null),

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

    private static final Map<EntityType, ItemStack> MOB_HEADS = new HashMap<>() {{
        put(EntityType.ZOMBIE, new ItemStack(Material.ZOMBIE_HEAD));
        put(EntityType.SKELETON, new ItemStack(Material.SKELETON_SKULL));
        put(EntityType.SPIDER, SkullUtils.getSkullFrom(SkullID.MC_SPIDER));
        put(EntityType.SLIME, SkullUtils.getSkullFrom(SkullID.MC_SLIME));
        put(EntityType.MAGMA_CUBE, SkullUtils.getSkullFrom(SkullID.MC_MAGMACUBE));
        put(EntityType.BLAZE, SkullUtils.getSkullFrom(SkullID.MC_BLAZE));
        put(EntityType.WITCH, SkullUtils.getSkullFrom(SkullID.MC_WITCH));
        put(EntityType.IRON_GOLEM, SkullUtils.getSkullFrom(SkullID.MC_GOLEM));
        put(EntityType.WITHER_SKELETON, new ItemStack(Material.WITHER_SKELETON_SKULL));
        put(EntityType.PIGLIN, SkullUtils.getSkullFrom(SkullID.MC_PIGLIN));
        put(EntityType.WOLF, SkullUtils.getSkullFrom(SkullID.MC_ANGRY_WOLF));
    }};
    public final EntityType entityType;
    public final Class<?> mobClass;
    @Deprecated
    public final Function<Location, AbstractMob> createMobLegacy;
    public final Function7<Location, String, Integer, Float, Integer, Float, Float, AbstractMob> createMobFunction;
    public final EntityEquipment equipment;
    public String name;
    public int maxHealth;
    public float walkSpeed;
    public int damageResistance;
    public float minMeleeDamage;
    public float maxMeleeDamage;

    Mob(
            EntityType entityType,
            Class<?> mobClass,
            Function<Location, AbstractMob> createMobLegacy,
            Function7<Location, String, Integer, Float, Integer, Float, Float, AbstractMob> createMobFunction,
            EntityEquipment equipment
    ) {
        this.entityType = entityType;
        this.createMobLegacy = createMobLegacy;
        this.createMobFunction = createMobFunction;
        this.mobClass = mobClass;
        this.equipment = equipment;
    }

    public ItemStack getHead() {
        if (equipment != null && equipment.getHelmet() != null) {
            return equipment.getHelmet();
        } else {
            return MOB_HEADS.getOrDefault(entityType, new ItemStack(Material.BARRIER));
        }
    }

    public AbstractMob createMob(Location spawnLocation) {
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
