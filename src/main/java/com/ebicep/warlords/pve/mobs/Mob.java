package com.ebicep.warlords.pve.mobs;

import com.ebicep.warlords.game.option.towerdefense.mobs.*;
import com.ebicep.warlords.game.option.towerdefense.towers.*;
import com.ebicep.warlords.game.option.whackamole.moles.MoleArmorStand;
import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.pve.mobs.blaze.BlazingKindle;
import com.ebicep.warlords.pve.mobs.bosses.*;
import com.ebicep.warlords.pve.mobs.bosses.Void;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.*;
import com.ebicep.warlords.pve.mobs.bosses.raidbosses.RaidMithra;
import com.ebicep.warlords.pve.mobs.creaking.SovereignGuardian;
import com.ebicep.warlords.pve.mobs.creeper.CreepyBomber;
import com.ebicep.warlords.pve.mobs.enderman.EndermanAnomaly;
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
import com.ebicep.warlords.pve.mobs.husk.SandstriderWraith;
import com.ebicep.warlords.pve.mobs.husk.UndeadWarrior;
import com.ebicep.warlords.pve.mobs.irongolem.EnhancerMechan;
import com.ebicep.warlords.pve.mobs.irongolem.GolemApprentice;
import com.ebicep.warlords.pve.mobs.magmacube.Illumination;
import com.ebicep.warlords.pve.mobs.pigzombie.PigAlleviator;
import com.ebicep.warlords.pve.mobs.pigzombie.PigDisciple;
import com.ebicep.warlords.pve.mobs.pigzombie.PigParticle;
import com.ebicep.warlords.pve.mobs.pigzombie.PigShaman;
import com.ebicep.warlords.pve.mobs.player.*;
import com.ebicep.warlords.pve.mobs.skeleton.*;
import com.ebicep.warlords.pve.mobs.slime.LurkingSlime;
import com.ebicep.warlords.pve.mobs.slime.SlimyAnomaly;
import com.ebicep.warlords.pve.mobs.slime.SlimyChess;
import com.ebicep.warlords.pve.mobs.spider.ArachnoVenari;
import com.ebicep.warlords.pve.mobs.stray.FallenStray;
import com.ebicep.warlords.pve.mobs.stray.Stray;
import com.ebicep.warlords.pve.mobs.vex.SpectralThief;
import com.ebicep.warlords.pve.mobs.vindicator.AncientDynasty;
import com.ebicep.warlords.pve.mobs.witch.WitchDeacon;
import com.ebicep.warlords.pve.mobs.witherskeleton.*;
import com.ebicep.warlords.pve.mobs.wolf.Hound;
import com.ebicep.warlords.pve.mobs.zombie.*;
import com.ebicep.warlords.pve.mobs.zombie.berserkzombie.AdvancedWarriorBerserker;
import com.ebicep.warlords.pve.mobs.zombie.berserkzombie.BasicWarriorBerserker;
import com.ebicep.warlords.pve.mobs.zombie.berserkzombie.IntermediateWarriorBerserker;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.pve.VanillaHeads;
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
    )
    ),
    BASIC_WARRIOR_BERSERKER(EntityType.ZOMBIE, BasicWarriorBerserker.class, BasicWarriorBerserker::new, BasicWarriorBerserker::new, new Utils.SimpleEntityEquipment(
            ArmorManager.Helmets.SIMPLE_WARRIOR_HELMET.itemRed,
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
            new ItemStack(Material.WOODEN_SWORD)
    )
    ),
    SKELETAL_MAGE(EntityType.SKELETON, SkeletalMage.class, SkeletalMage::new, SkeletalMage::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.FACELESS_MAGE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
            new ItemStack(Material.BOW)
    )
    ),
    PIG_DISCIPLE(EntityType.PIGLIN, PigDisciple.class, PigDisciple::new, PigDisciple::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SAMURAI),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
            Weapons.SILVER_PHANTASM_TRIDENT.getItem()
    )
    ),
    SLIMY_ANOMALY(EntityType.SLIME, SlimyAnomaly.class, SlimyAnomaly::new, SlimyAnomaly::new, null),
    ARACHNO_VENARI(EntityType.SPIDER, ArachnoVenari.class, ArachnoVenari::new, ArachnoVenari::new, null),
    IVORY_KNIGHT(EntityType.SKELETON, IvoryKnight.class, IvoryKnight::new, IvoryKnight::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmM1Yzg2Y2Y2YzdhOGNhNmFkNzkzMzNlMGE4ZGI1ZmUyNGJmZTEwMTY1MzQ4ZGJiOGIxY2ZhMmM1ZDY2ODNmIn19fQ=="),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 245, 245, 245),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 235, 235, 235),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 225, 225, 225),
            Weapons.WARLORDS_II_DIVINE_JUSTICE.getItem()
    )
    ),

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
            ArmorManager.Helmets.GREATER_MAGE_HELMET.itemRed,
            new ItemStack(Material.CHAINMAIL_CHESTPLATE),
            new ItemStack(Material.CHAINMAIL_LEGGINGS),
            new ItemStack(Material.CHAINMAIL_BOOTS),
            new ItemStack(Material.BOW)
    )
    ),
    PIG_SHAMAN(EntityType.PIGLIN, PigShaman.class, PigShaman::new, PigShaman::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SAMURAI),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
            new ItemStack(Material.DIAMOND_LEGGINGS),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
            new ItemStack(Material.COOKIE)
    )
    ),
    PIG_ALLEVIATOR(EntityType.PIGLIN, PigAlleviator.class, PigAlleviator::new, PigAlleviator::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SAMURAI),
            new ItemStack(Material.DIAMOND_HELMET),
            new ItemStack(Material.DIAMOND_LEGGINGS),
            new ItemStack(Material.DIAMOND_BOOTS),
            new ItemStack(Material.BAKED_POTATO)
    )
    ),
    WITCH_DEACON(EntityType.WITCH, WitchDeacon.class, WitchDeacon::new, WitchDeacon::new, null),
    BLAZING_KINDLE(EntityType.BLAZE, BlazingKindle.class, BlazingKindle::new, BlazingKindle::new, null),
    WANDER_KNIGHTS(EntityType.ZOMBIE, WanderKnights.class, WanderKnights::new, WanderKnights::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.FANCY_CUBE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 105, 147, 158),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 105, 147, 158),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 105, 147, 158),
            Weapons.LUNAR_RELIC.getItem()
    )
    ),
    ZOMBIE_SWORDSMAN(EntityType.ZOMBIE, ZombieSwordsman.class, ZombieSwordsman::new, ZombieSwordsman::new, new Utils.SimpleEntityEquipment(
            new ItemStack(Material.RESIN_CLUMP),
            new ItemStack(Material.CHAINMAIL_CHESTPLATE),
            new ItemStack(Material.CHAINMAIL_LEGGINGS),
            new ItemStack(Material.CHAINMAIL_BOOTS),
            new ItemStack(Material.PRISMARINE_SHARD)
    )
    ),
    ZOMBIE_LAMENT(EntityType.ZOMBIE, ZombieLament.class, ZombieLament::new, ZombieLament::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.BLUE_GHOST),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 69, 176),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 69, 176),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 69, 176),
            Weapons.SILVER_PHANTASM_STAFF_2.getItem()
    )
    ),
    IVORY_RONIN(EntityType.SKELETON, IvoryRonin.class, IvoryRonin::new, IvoryRonin::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGM2YTRkMjJkNDA3ZTc0YTg4YjJiMzQzMGZjYjg0OTM0NGU4ZTg4NDVlNjk3YzgwNDdhYmQwM2IzZDkxOGQ1YiJ9fX0="),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 250, 250, 250),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 238, 238, 238),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 225, 225, 225),
            Weapons.WARLORDS_II_ASHURA_S_BLADE.getItem()
    )
    ),

    // Advanced
    ILLUMINATION(EntityType.MAGMA_CUBE, Illumination.class, Illumination::new, Illumination::new, null),
    GOLEM_APPRENTICE(EntityType.IRON_GOLEM, GolemApprentice.class, GolemApprentice::new, GolemApprentice::new, null),
    SCRUPULOUS_ZOMBIE(EntityType.ZOMBIE, ScrupulousZombie.class, ScrupulousZombie::new, ScrupulousZombie::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SCULK_CORRUPTION),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 10, 50, 130),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 10, 50, 130),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 10, 50, 130),
            Weapons.AMARANTH.getItem()
    )
    ),
    SLIME_GUARD(EntityType.ZOMBIE, SlimeGuard.class, SlimeGuard::new, SlimeGuard::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SLIME_BLOCK),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 106, 255, 106),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 106, 255, 106),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 106, 255, 106),
            Weapons.NEW_LEAF_SPEAR.getItem()
    )
    ),
    CELESTIAL_BOW_WIELDER(EntityType.SKELETON, CelestialBowWielder.class, CelestialBowWielder::new, CelestialBowWielder::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.BOW_HEAD),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 20, 20),
            Weapons.FROSTBITE.getItem()
    )
    ),
    ZOMBIE_VANGUARD(EntityType.ZOMBIE, ZombieVanguard.class, ZombieVanguard::new, ZombieVanguard::new, new Utils.SimpleEntityEquipment(
            ArmorManager.Helmets.LEGENDARY_PALADIN_HELMET.itemRed,
            new ItemStack(Material.DIAMOND_CHESTPLATE),
            new ItemStack(Material.DIAMOND_LEGGINGS),
            new ItemStack(Material.DIAMOND_BOOTS),
            Weapons.FELFLAME_BLADE.getItem()
    )
    ),
    ADVANCED_WARRIOR_BERSERKER(EntityType.ZOMBIE, AdvancedWarriorBerserker.class, AdvancedWarriorBerserker::new, AdvancedWarriorBerserker::new, new Utils.SimpleEntityEquipment(
            ArmorManager.Helmets.LEGENDARY_WARRIOR_HELMET.itemRed,
            new ItemStack(Material.DIAMOND_CHESTPLATE),
            new ItemStack(Material.DIAMOND_LEGGINGS),
            new ItemStack(Material.DIAMOND_BOOTS),
            new ItemStack(Material.COOKED_SALMON)
    )
    ),
    VOID_ZOMBIE(EntityType.ZOMBIE, VoidZombie.class, VoidZombie::new, VoidZombie::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.FACELESS_BANDIT),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 0, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 0, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 0, 0),
            Weapons.VOID_EDGE.getItem()
    )
    ),
    ZOMBIE_KNIGHT(EntityType.ZOMBIE, ZombieKnight.class, ZombieKnight::new, ZombieKnight::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.NETHERITE_HELMET),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 20, 20),
            Weapons.GEMINI.getItem()
    )
    ),
    SLIMY_CHESS(EntityType.SLIME, SlimyChess.class, SlimyChess::new, SlimyChess::new, null),
    ZOMBIE_RAIDER(EntityType.ZOMBIE, ZombieRaider.class, ZombieRaider::new, ZombieRaider::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.FANCY_CUBE_2),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 56, 71, 74),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 56, 71, 74),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 56, 71, 74),
            Weapons.NETHERSTEEL_KATANA.getItem()
    )
    ),
    WANDER_WALKER(EntityType.ZOMBIE, WanderWalker.class, WanderWalker::new, WanderWalker::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SCULK_MONSTER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 204, 204),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 204, 204),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 204, 204),
            Weapons.LUNAR_JUSTICE.getItem()
    )
    ),
    SKELETAL_ENTROPY(EntityType.SKELETON, SkeletalEntropy.class, SkeletalEntropy::new, SkeletalEntropy::new, new Utils.SimpleEntityEquipment(
            new ItemStack(Material.GRAY_CANDLE),
            new ItemStack(Material.DIAMOND_CHESTPLATE),
            new ItemStack(Material.DIAMOND_LEGGINGS),
            new ItemStack(Material.DIAMOND_BOOTS),
            Weapons.VOID_TWIG.getItem()
    )
    ),
    FIRE_SPLITTER(EntityType.ZOMBIE, FireSplitter.class, FireSplitter::new, FireSplitter::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.LAVA_MONSTER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 252, 170, 53),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 252, 170, 53),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 252, 170, 53),
            Weapons.SILVER_PHANTASM_SWORD_2.getItem()
    )
    ),
    PALE_SERAPH(EntityType.WITHER_SKELETON, PaleSeraph.class, PaleSeraph::new, PaleSeraph::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWUxNjU0NjA5MWI1ZWZiYTY4ZmFkZWQ2ZmI1NDhjNWRkNzg4ZGEzOGFkOWRhNGExMTM2ZGY3MTUwYzUwNThiZCJ9fX0="),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 245, 245, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 235, 235, 245),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 225, 225, 235),
            Weapons.WARLORDS_II_DIVINE_REAPER.getItem()
    )
    ),

    // Elite
    CELESTIAL_SWORD_WIELDER(EntityType.ZOMBIE, CelestialSwordWielder.class, CelestialSwordWielder::new, CelestialSwordWielder::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SWORD_HEAD),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 20, 20),
            Weapons.DIAMONDSPARK.getItem()
    )
    ),
    CELESTIAL_OPUS(EntityType.WITHER_SKELETON, CelestialOpus.class, CelestialOpus::new, CelestialOpus::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.CELESTIAL_GOLDOR),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 40, 40, 40),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 40, 40, 40),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 40, 40, 40),
            Weapons.SILVER_PHANTASM_SAWBLADE.getItem()
    )
    ),
    RIFT_WALKER(EntityType.ZOMBIE, RiftWalker.class, RiftWalker::new, RiftWalker::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.PURPLE_RIFT),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 229, 69, 176),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 229, 69, 176),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 229, 69, 176),
            Weapons.VORPAL_SWORD.getItem()
    )
    ),
    OVERGROWN_ZOMBIE(EntityType.ZOMBIE, OvergrownZombie.class, OvergrownZombie::new, OvergrownZombie::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.GREEN_LANCER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 130, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 130, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 130, 20),
            Weapons.NEW_LEAF_AXE.getItem()
    )
    ),
    SKELETAL_PYROMANCER(EntityType.SKELETON, SkeletalPyromancer.class, SkeletalPyromancer::new, SkeletalPyromancer::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.WITHER_SOUL),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 29, 49, 64),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 29, 49, 64),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 29, 49, 64),
            null
    )
    ),
    SKELETAL_ANOMALY(EntityType.SKELETON, SkeletalAnomaly.class, SkeletalAnomaly::new, SkeletalAnomaly::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SEEK_DOORS),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 64, 64, 64),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 64, 64, 64),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 64, 64, 64),
            Weapons.FABLED_HEROICS_SWORD.getItem()
    )
    ),
    SKELETAL_MESMER(EntityType.SKELETON, SkeletalMesmer.class, SkeletalMesmer::new, SkeletalMesmer::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEMON_SKELETON),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 0, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 0, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 0, 0),
            Weapons.ARMBLADE.getItem()
    )
    ),
    CREEPY_BOMBER(EntityType.CREEPER, CreepyBomber.class, CreepyBomber::new, CreepyBomber::new, null),
    SKELETAL_ARCHER(EntityType.SKELETON, SkeletalArcher.class, SkeletalArcher::new, SkeletalArcher::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SKELETON_ARCHER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 226, 226, 226),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 226, 226, 226),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 226, 226, 226),
            Weapons.FABLED_HEROICS_SCYTHE.getItem()
    )
    ),
    REQUIEM_GUARDIAN(EntityType.ZOMBIE, RequiemGuardian.class, RequiemGuardian::new, RequiemGuardian::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.PURPLE_KNIGHT),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
            Weapons.SOUL_REAVER.getItem(),
            new ItemStack(Material.SHIELD)
    )
    ),
    ANCIENT_DYNASTY(EntityType.VINDICATOR, AncientDynasty.class, AncientDynasty::new, AncientDynasty::new, new Utils.SimpleEntityEquipment(
            null,
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 255, 255),
            Weapons.NETHERSTEEL_KATANA.getItem()
    )
    ),
    BOUND_ARCHER(EntityType.SKELETON, BoundArcher.class, BoundArcher::new, BoundArcher::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEEP_DARK_CRAWLER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 45, 20, 75),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 35, 15, 60),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 25, 10, 45),
            new ItemStack(Material.BOW)
    )
    ),

    // Champion
    NIGHTMARE_ZOMBIE(EntityType.ZOMBIE, NightmareZombie.class, NightmareZombie::new, NightmareZombie::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SHADOW_DEMON),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 70, 50, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 70, 50, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 70, 50, 20),
            Weapons.FABLED_HEROICS_SWORD.getItem()
    )
    ),
    PIG_PARTICLE(EntityType.ZOMBIE, PigParticle.class, PigParticle::new, PigParticle::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.HOODED_KNIGHT),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 20, 20),
            Weapons.NETHERSTEEL_KATANA.getItem()
    )
    ),
    EXTREME_ZEALOT(EntityType.ZOMBIE, ExtremeZealot.class, ExtremeZealot::new, ExtremeZealot::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.FANCY_CUBE_2),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 56, 71, 74),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 56, 71, 74),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 56, 71, 74),
            Weapons.VENOMSTRIKE.getItem()
    )
    ),
    SMART_SKELETON(EntityType.ZOMBIE, SmartSkeleton.class, SmartSkeleton::new, SmartSkeleton::new, new Utils.SimpleEntityEquipment(
            null,
            null,
            null,
            null,
            new ItemStack(Material.BOW)
    )
    ),
    SKELETAL_SORCERER(EntityType.SKELETON, SkeletalSorcerer.class, SkeletalSorcerer::new, SkeletalSorcerer::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.WHITE_SHEKEL),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 255, 255),
            Weapons.SILVER_PHANTASM_SWORD_4.getItem()
    )
    ),
    OBSIDIAN_SENTINEL(EntityType.WITHER_SKELETON, ObsidianSentinel.class, ObsidianSentinel::new, ObsidianSentinel::new, new Utils.SimpleEntityEquipment(
                    SkullUtils.getSkullFrom(SkullID.OBSIDIAN_SENTINEL),
                    Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 15, 30),
                    Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 15, 30),
                    Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 15, 30),
                    new ItemStack(Material.NETHERITE_SWORD)
    )
    ),
    SANDSTRIDER_WRAITH(EntityType.HUSK, SandstriderWraith.class, SandstriderWraith::new, SandstriderWraith::new,
            new Utils.SimpleEntityEquipment(
                    SkullUtils.getSkullFrom(SkullID.MUMMY),
                    Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 194, 178, 128),
                    Utils.applyColorTo(Material.LEATHER_LEGGINGS, 194, 178, 128),
                    Utils.applyColorTo(Material.LEATHER_BOOTS, 120, 90, 50),
                    new ItemStack(Material.GOLDEN_HOE)
            )
    ),
    ZOMBIE_END(EntityType.STRAY, ZombieEnd.class, ZombieEnd::new, ZombieEnd::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.BLOCK_ENDERMAN),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 255, 255),
            Weapons.SOUL_REAVER.getItem()
    )
    ),
    ENDERMAN_ANOMALY(EntityType.ENDERMAN, EndermanAnomaly.class, EndermanAnomaly::new, EndermanAnomaly::new, null),
    ZOMBIE_WARPED(EntityType.STRAY, ZombieWarped.class, ZombieWarped::new, ZombieWarped::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SCULK_ENDERMAN),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 255, 255),
            Weapons.VORPAL_SWORD.getItem()
    )
    ),
    SKELETON_END(EntityType.WITHER_SKELETON, SkeletonEnd.class, SkeletonEnd::new, SkeletonEnd::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.END_REDSTONE_LAMP),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 255, 255),
            Weapons.FABLED_HEROICS_SCYTHE.getItem()
    )
    ),
    SOVEREIGN_GUARDIAN(EntityType.CREAKING, SovereignGuardian.class, SovereignGuardian::new, SovereignGuardian::new, null),
    ABYSS_WATCHER(EntityType.WITHER_SKELETON, AbyssWatcher.class, AbyssWatcher::new, AbyssWatcher::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SCULK_CORRUPTION),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 25, 15, 45),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 10, 35),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 15, 5, 25),
            Weapons.VOID_TWIG.getItem()
    )
    ),
    LANTERN_DREDGER(EntityType.WITHER_SKELETON, LanternDredger.class, LanternDredger::new, LanternDredger::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.LANTERN),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 15, 35, 45),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 10, 25, 35),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 5, 15, 25),
            Weapons.SILVER_PHANTASM_STAFF_2.getItem()
    )
    ),
    BARNACLE_BRUTE(EntityType.DROWNED, BarnacleBrute.class, BarnacleBrute::new, BarnacleBrute::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DARK_CAGE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 45, 55),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 15, 35, 45),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 10, 25, 35),
            new ItemStack(Material.FISHING_ROD)
    )
    ),
    SILTSTALKER(EntityType.DROWNED, Siltstalker.class, Siltstalker::new, Siltstalker::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.BLUE_ASSASSIN),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 65, 45, 45),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 65, 35, 35),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 60, 25, 25),
            Weapons.DEMONBLADE.getItem()
    )
    ),
    VOID_JAILER(EntityType.WITHER_SKELETON, VoidJailer.class, VoidJailer::new, VoidJailer::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.VOID_KNIGHT),
            new ItemStack(Material.NETHERITE_CHESTPLATE),
            new ItemStack(Material.NETHERITE_LEGGINGS),
            new ItemStack(Material.NETHERITE_BOOTS),
            new ItemStack(Material.AMETHYST_CLUSTER),
            new ItemStack(Material.AMETHYST_CLUSTER)
    )
    ),
    SOULBINDER(EntityType.WITHER_SKELETON, Soulbinder.class, Soulbinder::new, Soulbinder::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SPIDER_SPIRIT),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 25, 5, 45),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 5, 35),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 15, 0, 25),
            Weapons.TIDEBREAKER.getItem(),
            new ItemStack(Material.FILLED_MAP)
    )
    ),
    DEVOURING_IDOL(EntityType.WITHER_SKELETON, DevouringIdol.class, DevouringIdol::new, DevouringIdol::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.TALISMAN),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 95, 105),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 95, 105),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 10, 95, 105),
            new ItemStack(Material.RESPAWN_ANCHOR)
    )
    ),

    // Boss
    BOLTARO(EntityType.ZOMBIE, Boltaro.class, Boltaro::new, Boltaro::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEMON),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 0),
            new ItemStack(Material.CHAINMAIL_LEGGINGS),
            new ItemStack(Material.CHAINMAIL_BOOTS),
            Weapons.DRAKEFANG.getItem()
    )
    ),
    GHOULCALLER(EntityType.ZOMBIE, Ghoulcaller.class, Ghoulcaller::new, Ghoulcaller::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEMON_SKELETON),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 170, 170, 170),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 170, 170, 170),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 170, 170, 170),
            Weapons.ENDERFIST.getItem()
    )
    ),
    NARMER(EntityType.ZOMBIE, Narmer.class, Narmer::new, Narmer::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.BURNING_WITHER_SKELETON),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 160, 160),
            ArmorManager.ArmorSets.GREATER_LEGGINGS.itemRed,
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 160, 160),
            Weapons.WALKING_STICK.getItem()
    )
    ),
    MITHRA(EntityType.ZOMBIE, Mithra.class, Mithra::new, Mithra::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.IRON_QUEEN),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 200, 200, 200),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 200, 200, 200),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 200, 200, 200),
            Weapons.SILVER_PHANTASM_SWORD_3.getItem()
    )
    ),
    ZENITH(EntityType.ZOMBIE, Zenith.class, Zenith::new, Zenith::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.PURPLE_ENDERMAN),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 255),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 255),
            Weapons.VORPAL_SWORD.getItem()
    )
    ),
    CHESSKING(EntityType.SLIME, Chessking.class, Chessking::new, Chessking::new, null),
    ILLUMINA(EntityType.ZOMBIE, Illumina.class, Illumina::new, Illumina::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEEP_DARK_WORM),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 120, 120, 200),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 120, 120, 200),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 120, 120, 200),
            Weapons.NEW_LEAF_SCYTHE.getItem()
    )
    ),
    TORMENT(EntityType.WITHER_SKELETON, Torment.class, Torment::new, Torment::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEMON_KING),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 230, 60, 60),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 230, 60, 60),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 230, 60, 60),
            Weapons.SILVER_PHANTASM_TRIDENT.getItem()
    )
    ),
    VOID(EntityType.SKELETON, Void.class, Void::new, Void::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.END_MONSTER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 20, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 20, 20),
            Weapons.VOID_EDGE.getItem()
    )
    ),
    PHYSIRA(EntityType.WITHER_SKELETON, Physira.class, Physira::new, Physira::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SEEK_DOORS),
            new ItemStack(Material.NETHERITE_CHESTPLATE),
            new ItemStack(Material.NETHERITE_LEGGINGS),
            new ItemStack(Material.NETHERITE_BOOTS),
            Weapons.SOUL_REAVER.getItem()
    )
    ),
    ONE_OF_NINE(EntityType.WITHER_SKELETON, OneOfNine.class, OneOfNine::new, OneOfNine::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.GHOST_PURPLE),
            new ItemStack(Material.NETHERITE_CHESTPLATE),
            new ItemStack(Material.NETHERITE_LEGGINGS),
            new ItemStack(Material.NETHERITE_BOOTS),
            Weapons.SOUL_REAVER.getItem()
    )
    ),
    ORBYZ(EntityType.STRAY, Orbyz.class, Orbyz::new, Orbyz::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.ABYSSAL_KUUDRA),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 20, 120),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 20, 120),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 20, 120),
            Weapons.FROSTBITE.getItem()
    )
    ),
    LILIUM(EntityType.WITHER_SKELETON, Lilium.class, Lilium::new, Lilium::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.LILIUM_HAT),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 220, 20, 120),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 220, 20, 120),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 220, 20, 120),
            Weapons.SILVER_PHANTASM_SWORD_4.getItem(),
            Weapons.SILVER_PHANTASM_SWORD_4.getItem()
    )
    ),
    VEILKEEPER(EntityType.WITHER_SKELETON, Veilkeeper.class, Veilkeeper::new, Veilkeeper::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SILVER_KNIGHT),
            new ItemStack(Material.NETHERITE_CHESTPLATE),
            new ItemStack(Material.NETHERITE_LEGGINGS),
            new ItemStack(Material.NETHERITE_BOOTS),
            Weapons.FELFLAME_BLADE.getItem(),
            new ItemStack(Material.SHIELD)
    )
    ),
    CENTURION(EntityType.WITHER_SKELETON, Centurion.class, Centurion::new, Centurion::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.ROTTEN_CORPSE),
            new ItemStack(Material.NETHERITE_CHESTPLATE),
            new ItemStack(Material.NETHERITE_LEGGINGS),
            new ItemStack(Material.NETHERITE_BOOTS),
            Weapons.VOID_TWIG.getItem(),
            Weapons.VOID_TWIG.getItem()
    )
    ),
    VANGUARD(EntityType.GIANT, Vanguard.class, Vanguard::new, Vanguard::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SILVER_KNIGHT),
            new ItemStack(Material.NETHERITE_CHESTPLATE),
            new ItemStack(Material.NETHERITE_LEGGINGS),
            new ItemStack(Material.NETHERITE_BOOTS),
            Weapons.FELFLAME_BLADE.getItem(),
            new ItemStack(Material.SHIELD)
    )
    ),
    RAID_MITHRA(EntityType.WITHER_SKELETON, RaidMithra.class, RaidMithra::new, RaidMithra::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.IRON_QUEEN),
            new ItemStack(Material.NETHERITE_CHESTPLATE),
            new ItemStack(Material.NETHERITE_LEGGINGS),
            new ItemStack(Material.NETHERITE_BOOTS),
            Weapons.FELFLAME_BLADE.getItem()
    )
    ),
    MAGMATIC_OOZE(EntityType.MAGMA_CUBE, MagmaticOoze.class, MagmaticOoze::new, MagmaticOoze::new, null),
    ENAVURIS(EntityType.ENDERMAN, Enavuris.class, Enavuris::new, Enavuris::new, null),

    // Boss minions
    BOLTARO_SHADOW(EntityType.ZOMBIE, BoltaroShadow.class, BoltaroShadow::new, BoltaroShadow::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.END_MONSTER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 30, 0, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 30, 0, 0),
            Weapons.DEMONBLADE.getItem()
    )
    ),
    BOLTARO_EXLIED(EntityType.SKELETON, BoltaroExiled.class, BoltaroExiled::new, BoltaroExiled::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.RED_EYE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
            Weapons.GEMINI.getItem()
    )
    ),
    TORMENTED_SOUL(EntityType.ZOMBIE, TormentedSoul.class, TormentedSoul::new, TormentedSoul::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.GHOST_RED),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 140, 140, 140),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 140, 140, 15),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 140, 140, 140),
            Weapons.CLAWS.getItem()
    )
    ),
    DEPRESSED_SOUL(EntityType.ZOMBIE, DepressedSoul.class, DepressedSoul::new, DepressedSoul::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.GHOST_BLUE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 140, 140, 140),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 140, 140, 15),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 140, 140, 140),
            Weapons.CLAWS.getItem()
    )
    ),
    FURIOUS_SOUL(EntityType.ZOMBIE, FuriousSoul.class, FuriousSoul::new, FuriousSoul::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.GHOST_MAGENTA),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 140, 140, 140),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 140, 140, 15),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 140, 140, 140),
            Weapons.CLAWS.getItem()
    )
    ),
    VOLTAIC_SOUL(EntityType.ZOMBIE, VoltaicSoul.class, VoltaicSoul::new, VoltaicSoul::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.GHOST_PURPLE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 140, 140, 140),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 140, 140, 15),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 140, 140, 140),
            Weapons.CLAWS.getItem()
    )
    ),
    AGONIZED_SOUL(EntityType.ZOMBIE, AgonizedSoul.class, AgonizedSoul::new, AgonizedSoul::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.GHOST_GRAY),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 140, 140, 140),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 140, 140, 15),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 140, 140, 140),
            Weapons.CLAWS.getItem()
    )
    ),
    NARMER_ACOLYTE(EntityType.ZOMBIE, NarmerAcolyte.class, NarmerAcolyte::new, NarmerAcolyte::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.RED_EYE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 30, 0, 15),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 30, 0, 15),
            Weapons.DEMONBLADE.getItem()
    )
    ),
    NARMERS_DEATH_CHARGE(EntityType.TNT, NarmersDeathCharge.class, NarmersDeathCharge::new, NarmersDeathCharge::new, new Utils.SimpleEntityEquipment(
            new ItemStack(Material.TNT),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 30, 0, 15),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 30, 0, 15),
            Weapons.DEMONBLADE.getItem()
    )
    ),
    ZENITH_LEGIONNAIRE(EntityType.ZOMBIE, ZenithLegionnaire.class, ZenithLegionnaire::new, ZenithLegionnaire::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.FACELESS_BANDIT),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 100, 0, 80),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 100, 0, 80),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 100, 0, 80),
            Weapons.LUNAR_JUSTICE.getItem()
    )
    ),
    SOUL_OF_GRADIENT(EntityType.ZOMBIE, SoulOfGradient.class, SoulOfGradient::new, SoulOfGradient::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.GRADIENT_SOUL),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 30, 30),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 30, 30, 30),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 30, 30, 30),
            Weapons.TENDERIZER.getItem()
    )
    ),
    ECHO_OF_BLADES(EntityType.STRAY, EchoOfBlades.class, EchoOfBlades::new, EchoOfBlades::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.WITHER_SOUL),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 0, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 0, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 0, 0),
            Weapons.SOUL_REAVER.getItem()
    )
    ),
    SOUL_REAVER(EntityType.DROWNED, SoulReaver.class, SoulReaver::new, SoulReaver::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.CELESTIAL_GOLDOR),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 200, 200, 200),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 200, 200, 200),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 200, 200, 200),
            Weapons.TIDEBREAKER.getItem()
    )
    ),
    FROST_VEIL(EntityType.WITHER_SKELETON, FrostVeil.class, FrostVeil::new, FrostVeil::new, new Utils.SimpleEntityEquipment(
            new ItemStack(Material.PACKED_ICE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 30, 120),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 30, 30, 120),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 30, 30, 120),
            Weapons.FROSTBITE.getItem()
    )
    ),
    CRYSTALLINE_PETAL(EntityType.BOGGED, CrystallinePetal.class, CrystallinePetal::new, CrystallinePetal::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.STONE_ORB_PINK),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 220, 30, 90),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 220, 30, 90),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 220, 30, 90),
            Weapons.SILVER_PHANTASM_SWORD_4.getItem()
    )
    ),
    ECHO_OF_LILIUM(EntityType.BOGGED, EchoOfLilium.class, EchoOfLilium::new, EchoOfLilium::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.LILIUM_HAT),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 120, 20, 220),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 120, 20, 220),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 220, 20, 220),
            Weapons.SILVER_PHANTASM_SWORD_4.getItem(),
            new ItemStack(Material.SHIELD)
    )
    ),
    MITHRA_EGG_SAC(EntityType.ARMOR_STAND, EggSac.class, EggSac::new, EggSac::new, new Utils.SimpleEntityEquipment(
            EggSac.EGG_SAC_ITEM,
            null,
            null,
            null,
            null
    )
    ),
    NINE_CRYSTAL(EntityType.BREEZE, NineCrystal.class, NineCrystal::new, NineCrystal::new, null),
    SKY_CRYSTAL(EntityType.END_CRYSTAL, SkyCrystal.class, SkyCrystal::new, SkyCrystal::new, null),
    PETAL_CRYSTAL(EntityType.END_CRYSTAL, PetalCrystal.class, PetalCrystal::new, PetalCrystal::new, null),
    LILIATH_ENIGMA(EntityType.EVOKER, LiliathEngima.class, LiliathEngima::new, LiliathEngima::new, null),
    ARACHNO_VENERATUS(EntityType.SPIDER, ArachnoVeneratus.class, ArachnoVeneratus::new, ArachnoVeneratus::new, null),
    CURSED_PSION(EntityType.WITHER_SKELETON, CursedPsion.class, CursedPsion::new, CursedPsion::new, null),
    ENAVURITE(EntityType.ENDERMITE, Enavurite.class, Enavurite::new, Enavurite::new, null),
    VANISHING_ENAVURITE(EntityType.ENDERMITE, VanishingEnavurite.class, VanishingEnavurite::new, VanishingEnavurite::new, null),

    //EVENTS
    EVENT_BOLTARO(EntityType.ZOMBIE, EventBoltaro.class, EventBoltaro::new, EventBoltaro::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEMON),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 0),
            new ItemStack(Material.CHAINMAIL_LEGGINGS),
            new ItemStack(Material.CHAINMAIL_BOOTS),
            Weapons.DRAKEFANG.getItem()
    )
    ),
    EVENT_BOLTARO_SHADOW(EntityType.ZOMBIE, EventBoltaroShadow.class, EventBoltaroShadow::new, EventBoltaroShadow::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.END_MONSTER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 30, 0, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 30, 0, 0),
            Weapons.DEMONBLADE.getItem()
    )
    ),
    EVENT_NARMER(EntityType.ZOMBIE, EventNarmer.class, EventNarmer::new, EventNarmer::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.BURNING_WITHER_SKELETON),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 160, 160),
            ArmorManager.ArmorSets.GREATER_LEGGINGS.itemRed,
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 160, 160),
            Weapons.WALKING_STICK.getItem()
    )
    ),
    EVENT_NARMER_ACOLYTE(EntityType.ZOMBIE, EventNarmerAcolyte.class, EventNarmerAcolyte::new, EventNarmerAcolyte::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.RED_EYE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 30, 0, 15),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 30, 0, 15),
            Weapons.DEMONBLADE.getItem()
    )
    ),
    EVENT_NARMER_DJER(EntityType.ZOMBIE, EventDjer.class, EventDjer::new, EventDjer::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.ETHEREAL_WITHER_SKULL),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 160, 160),
            ArmorManager.ArmorSets.GREATER_LEGGINGS.itemRed,
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 160, 160),
            Weapons.WALKING_STICK.getItem()
    )
    ),
    EVENT_NARMER_DJET(EntityType.ZOMBIE, EventDjet.class, EventDjet::new, EventDjet::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.ETHEREAL_WITHER_SKULL),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 160, 160),
            ArmorManager.ArmorSets.GREATER_LEGGINGS.itemRed,
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 160, 160),
            Weapons.WALKING_STICK.getItem()
    )
    ),
    EVENT_MITHRA(EntityType.ZOMBIE, EventMithra.class, EventMithra::new, EventMithra::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.IRON_QUEEN),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 200, 200, 200),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 200, 200, 200),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 200, 200, 200),
            Weapons.SILVER_PHANTASM_SWORD_3.getItem()
    )
    ),
    EVENT_MITHRA_FORSAKEN_FROST(EntityType.ZOMBIE, EventForsakenFrost.class, EventForsakenFrost::new, EventForsakenFrost::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.WHITE_SPIDER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 255, 255),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 255, 255),
            Weapons.FROSTBITE.getItem()
    )
    ),
    EVENT_MITHRA_FORSAKEN_FOLIAGE(EntityType.ZOMBIE, EventForsakenFoliage.class, EventForsakenFoliage::new, EventForsakenFoliage::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.JUNGLE_SPIDER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 14, 87, 9),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 14, 87, 9),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 14, 87, 9),
            Weapons.NEW_LEAF_SPEAR.getItem()
    )
    ),
    EVENT_MITHRA_FORSAKEN_SHRIEKER(EntityType.ZOMBIE, EventForsakenShrieker.class, EventForsakenShrieker::new, EventForsakenShrieker::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEEP_DARK_CRAWLER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 87, 9, 86),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 87, 9, 86),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 87, 9, 86),
            Weapons.SILVER_PHANTASM_SWORD_3.getItem()
    )
    ),
    EVENT_MITHRA_FORSAKEN_RESPITE(EntityType.ZOMBIE, EventForsakenRespite.class, EventForsakenRespite::new, EventForsakenRespite::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SPIDER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 120, 120, 120),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 120, 120, 120),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 120, 120, 120),
            Weapons.NOMEGUSTA.getItem()
    )
    ),
    EVENT_MITHRA_FORSAKEN_CRUOR(EntityType.ZOMBIE, EventForsakenCruor.class, EventForsakenCruor::new, EventForsakenCruor::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.BLOOD_SPIDER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 20, 20),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 20, 20),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 20, 20),
            Weapons.ARMBLADE.getItem()
    )
    ),
    EVENT_MITHRA_FORSAKEN_DEGRADER(EntityType.ZOMBIE, EventForsakenDegrader.class, EventForsakenDegrader::new, EventForsakenDegrader::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DECAPITATED_SPIDER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 5, 5, 5),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 5, 5, 5),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 5, 5, 5),
            Weapons.DRAKEFANG.getItem()
    )
    ),
    EVENT_MITHRA_FORSAKEN_APPARITION(EntityType.ZOMBIE, EventForsakenApparition.class, EventForsakenApparition::new, EventForsakenApparition::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SPIDER_SPIRIT),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 64, 140, 255),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 64, 140, 255),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 64, 140, 255),
            Weapons.SILVER_PHANTASM_SWORD_4.getItem()
    )
    ),
    EVENT_MITHRA_POISONOUS_SPIDER(EntityType.ZOMBIE, EventPoisonousSpider.class, EventPoisonousSpider::new, EventPoisonousSpider::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.CAVE_SPIDER),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 0, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 0, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 0, 0),
            null
    )
    ),
    EVENT_MITHRA_EGG_SAC(EntityType.ARMOR_STAND, EventEggSac.class, EventEggSac::new, EventEggSac::new, new Utils.SimpleEntityEquipment(
            EggSac.EGG_SAC_ITEM,
            null,
            null,
            null,
            null
    )
    ),
    EVENT_ILLUSION_CORE(EntityType.ARMOR_STAND, EventIllusionCore.class, EventIllusionCore::new, EventIllusionCore::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.ENCHANTMENT_CUBE),
            null,
            null,
            null
    )
    ),
    EVENT_EXILED_CORE(EntityType.ARMOR_STAND, EventExiledCore.class, EventExiledCore::new, EventExiledCore::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.FANCY_CUBE_3),
            null,
            null,
            null
    )
    ),
    EVENT_CALAMITY_CORE(EntityType.ARMOR_STAND, EventCalamityCore.class, EventCalamityCore::new, EventCalamityCore::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.EXPLOSION),
            null,
            null,
            null
    )
    ),
    EVENT_ILLUMINA(EntityType.ZOMBIE, EventIllumina.class, EventIllumina::new, EventIllumina::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DEEP_DARK_WORM),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 120, 120, 200),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 120, 120, 200),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 120, 120, 200),
            Weapons.NEW_LEAF_SCYTHE.getItem()
    )
    ),
    EVENT_APOLLO(EntityType.SKELETON, EventApollo.class, EventApollo::new, EventApollo::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.APOLLO),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 140, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 140, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 140, 0),
            new ItemBuilder(Material.BOW)
                    .enchant(Enchantment.RESPIRATION, 1)
                    .get()
    )
    ),
    EVENT_ARES(EntityType.ZOMBIE, EventAres.class, EventAres::new, EventAres::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.ARES),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 140, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 140, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 140, 0),
            Weapons.VOID_TWIG.getItem()
    )
    ),
    EVENT_PROMETHEUS(EntityType.ZOMBIE, EventPrometheus.class, EventPrometheus::new, EventPrometheus::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.HERMES),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 140, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 140, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 140, 0),
            Weapons.ARMBLADE.getItem()
    )
    ),
    EVENT_ATHENA(EntityType.ZOMBIE, EventAthena.class, EventAthena::new, EventAthena::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.APHRODITE),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 140, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 140, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 140, 0),
            Weapons.NEW_LEAF_AXE.getItem()
    )
    ),
    EVENT_CRONUS(EntityType.ZOMBIE, EventCronus.class, EventCronus::new, EventCronus::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.BUST_ZEUS),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 140, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 140, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 140, 0),
            Weapons.LUNAR_JUSTICE.getItem()
    )
    ),
    EVENT_ZEUS(EntityType.ZOMBIE, EventZeus.class, EventZeus::new, EventZeus::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.ZEUS),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 236, 236, 236),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 236, 236, 236),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 236, 236, 236),
            Weapons.SEVENTH.getItem()
    )
    ),
    EVENT_POSEIDON(EntityType.ZOMBIE, EventPoseidon.class, EventPoseidon::new, EventPoseidon::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.POSEIDON),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 0, 205),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 0, 205),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 0, 205),
            Weapons.SILVER_PHANTASM_TRIDENT.getItem()
    )
    ),
    EVENT_HADES(EntityType.ZOMBIE, EventHades.class, EventHades::new, EventHades::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.DARK_WRAITH),
            new ItemStack((Material.NETHERITE_CHESTPLATE)),
            new ItemStack(Material.NETHERITE_LEGGINGS),
            new ItemStack(Material.NETHERITE_BOOTS),
            Weapons.FABLED_HEROICS_SCYTHE.getItem()
    )
    ),
    EVENT_TERAS_MINOTAUR(EntityType.ZOMBIE, EventTerasMinotaur.class, EventTerasMinotaur::new, EventTerasMinotaur::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.MINOTAUR),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 51, 102),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 51, 102),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 51, 102),
            Weapons.NOMEGUSTA.getItem()
    )
    ),
    EVENT_TERAS_CYCLOPS(EntityType.ZOMBIE, EventTerasCyclops.class, EventTerasCyclops::new, EventTerasCyclops::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.CYCLOPS),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 102, 51, 0),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 102, 51, 0),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 102, 51, 0),
            Weapons.HAMMER_OF_LIGHT.getItem()
    )
    ),
    EVENT_TERAS_SIREN(EntityType.ZOMBIE, EventTerasSiren.class, EventTerasSiren::new, EventTerasSiren::new, new Utils.SimpleEntityEquipment(
            SkullUtils.getSkullFrom(SkullID.SIREN),
            Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 102, 0, 51),
            Utils.applyColorTo(Material.LEATHER_LEGGINGS, 102, 0, 51),
            Utils.applyColorTo(Material.LEATHER_BOOTS, 102, 0, 51),
            Weapons.FABLED_HEROICS_SWORD.getItem()
    )
    ),
    EVENT_TERAS_DRYAD(EntityType.ZOMBIE, EventTerasDryad.class, EventTerasDryad::new, EventTerasDryad::new, null),

    EVENT_UNPUBLISHED_GRIMOIRE(EntityType.PLAYER, EventUnpublishedGrimoire.class, EventUnpublishedGrimoire::new, EventUnpublishedGrimoire::new, new Utils.SimpleEntityEquipment(
            null,
            null,
            null,
            null,
            Weapons.TENDERIZER.getItem()
    )
    ),
    EVENT_EMBELLISHED_GRIMOIRE(EntityType.PLAYER, EventEmbellishedGrimoire.class, EventEmbellishedGrimoire::new, EventEmbellishedGrimoire::new, new Utils.SimpleEntityEquipment(
            null,
            null,
            null,
            null,
            Weapons.ZWEIREAPER.getItem()
    )
    ),
    EVENT_SCRIPTED_GRIMOIRE(EntityType.PLAYER, EventScriptedGrimoire.class, EventScriptedGrimoire::new, EventScriptedGrimoire::new, new Utils.SimpleEntityEquipment(
            null,
            null,
            null,
            null,
            Weapons.GEMCRUSHER.getItem()
    )
    ),
    EVENT_ROUGE_GRIMOIRE(EntityType.PLAYER, EventRougeGrimoire.class, EventRougeGrimoire::new, EventRougeGrimoire::new, new Utils.SimpleEntityEquipment(
            null,
            null,
            null,
            null,
            Weapons.RUBY_THORN.getItem()
    )
    ),
    EVENT_VIOLETTE_GRIMOIRE(EntityType.PLAYER, EventVioletteGrimoire.class, EventVioletteGrimoire::new, EventVioletteGrimoire::new, new Utils.SimpleEntityEquipment(
            null,
            null,
            null,
            null,
            Weapons.FROSTBITE.getItem()
    )
    ),
    EVENT_BLEUE_GRIMOIRE(EntityType.PLAYER, EventBleueGrimoire.class, EventBleueGrimoire::new, EventBleueGrimoire::new, new Utils.SimpleEntityEquipment(
            null,
            null,
            null,
            null,
            Weapons.LUNAR_JUSTICE.getItem()
    )
    ),
    EVENT_ORANGE_GRIMOIRE(EntityType.PLAYER, EventOrangeGrimoire.class, EventOrangeGrimoire::new, EventOrangeGrimoire::new, new Utils.SimpleEntityEquipment(
            null,
            null,
            null,
            null,
            Weapons.FABLED_HEROICS_SWORD_2.getItem()
    )
    ),

    EVENT_NECRONOMICON_GRIMOIRE(EntityType.PLAYER, EventNecronomiconGrimoire.class, EventNecronomiconGrimoire::new, EventNecronomiconGrimoire::new, new Utils.SimpleEntityEquipment(
            null,
            null,
            null,
            null,
            Weapons.SILVER_PHANTASM_SWORD_2.getItem()
    )
    ),
    EVENT_THE_ARCHIVIST(EntityType.PLAYER, EventTheArchivist.class, EventTheArchivist::new, EventTheArchivist::new, new Utils.SimpleEntityEquipment(
            null,
            null,
            null,
            null,
            Weapons.SILVER_PHANTASM_STAFF_2.getItem()
    )
    ),
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
    TD_ZOMBIE_BABY(EntityType.ZOMBIE, TDZombieBaby.class, TDZombieBaby::new, TDZombieBaby::new, null),
    TD_ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER, TDZombieVillager.class, TDZombieVillager::new, TDZombieVillager::new, null),
    TD_HUSK(EntityType.HUSK, TDHusk.class, TDHusk::new, TDHusk::new, null),
    TD_SKELETON(EntityType.SKELETON, TDSkeleton.class, TDSkeleton::new, TDSkeleton::new, null),
    TD_STRAY(EntityType.STRAY, TDStray.class, TDStray::new, TDStray::new, null),
    TD_SPIDER(EntityType.SPIDER, TDSpider.class, TDSpider::new, TDSpider::new, null),
    TD_CAVE_SPIDER(EntityType.CAVE_SPIDER, TDCaveSpider.class, TDCaveSpider::new, TDCaveSpider::new, null),
    TD_SILVERFISH(EntityType.SILVERFISH, TDSilverfish.class, TDSilverfish::new, TDSilverfish::new, null),
    TD_ENDERMITE(EntityType.ENDERMITE, TDEndermite.class, TDEndermite::new, TDEndermite::new, null),
    TD_WITCH(EntityType.WITCH, TDWitch.class, TDWitch::new, TDWitch::new, null),
    TD_ENDERMAN(EntityType.ENDERMAN, TDEnderman.class, TDEnderman::new, TDEnderman::new, null),
    TD_WITHER_SKELETON(EntityType.WITHER_SKELETON, TDWitherSkeleton.class, TDWitherSkeleton::new, TDWitherSkeleton::new, null),
    TD_GHAST(EntityType.GHAST, TDGhast.class, TDGhast::new, TDGhast::new, null),
    TD_BLAZE(EntityType.BLAZE, TDBlaze.class, TDBlaze::new, TDBlaze::new, null),
    TD_PIGLIN(EntityType.PIGLIN, TDPiglin.class, TDPiglin::new, TDPiglin::new, null),
    TD_ZOMBIFIED_PIGLIN(EntityType.ZOMBIFIED_PIGLIN, TDZombifiedPiglin.class, TDZombifiedPiglin::new, TDZombifiedPiglin::new, null),
    TD_PIGLIN_BRUTE(EntityType.PIGLIN_BRUTE, TDPiglinBrute.class, TDPiglinBrute::new, TDPiglinBrute::new, null),
    TD_HOGLIN(EntityType.HOGLIN, TDHoglin.class, TDHoglin::new, TDHoglin::new, null),
    TD_ZOGLIN(EntityType.ZOGLIN, TDZoglin.class, TDZoglin::new, TDZoglin::new, null),
    TD_EVOKER(EntityType.EVOKER, TDEvoker.class, TDEvoker::new, TDEvoker::new, null),
    TD_VINDICATOR(EntityType.VINDICATOR, TDVindicator.class, TDVindicator::new, TDVindicator::new, null),
    TD_PILLAGER(EntityType.PILLAGER, TDPillager.class, TDPillager::new, TDPillager::new, null),
    TD_RAVAGER(EntityType.RAVAGER, TDRavager.class, TDRavager::new, TDRavager::new, null),
    TD_ILLUSIONER(EntityType.ILLUSIONER, TDIllusioner.class, TDIllusioner::new, TDIllusioner::new, null),
    TD_VEX(EntityType.VEX, TDVex.class, TDVex::new, TDVex::new, null),
    TD_CREEPER(EntityType.CREEPER, TDCreeper.class, TDCreeper::new, TDCreeper::new, null),
    TD_CREEPER_CHARGED(EntityType.CREEPER, TDCreeperCharged.class, TDCreeperCharged::new, TDCreeperCharged::new, null),
    TD_SLIME(EntityType.SLIME, TDSlime.class, TDSlime::new, TDSlime::new, null),
    TD_MAGMA_CUBE(EntityType.MAGMA_CUBE, TDMagmaCube.class, TDMagmaCube::new, TDMagmaCube::new, null),
    TD_PHANTOM(EntityType.PHANTOM, TDPhantom.class, TDPhantom::new, TDPhantom::new, null),
    TD_DROWNED(EntityType.DROWNED, TDDrowned.class, TDDrowned::new, TDDrowned::new, null),
    TD_GUARDIAN(EntityType.GUARDIAN, TDGuardian.class, TDGuardian::new, TDGuardian::new, null),
    TD_ELDER_GUARDIAN(EntityType.ELDER_GUARDIAN, TDElderGuardian.class, TDElderGuardian::new, TDElderGuardian::new, null),
    TD_WARDEN(EntityType.WARDEN, TDWarden.class, TDWarden::new, TDWarden::new, null),
    TD_ENDER_DRAGON(EntityType.ENDER_DRAGON, TDEnderDragon.class, TDEnderDragon::new, TDEnderDragon::new, null),
    TD_WITHER(EntityType.WITHER, TDWither.class, TDWither::new, TDWither::new, null),
    TD_GIANT(EntityType.GIANT, TDGiant.class, TDGiant::new, TDGiant::new, null),
    // tower defense tower mobs

    TD_TOWER_AVENGER(EntityType.ZOMBIE, AvengerTower.TDTowerAvenger.class, AvengerTower.TDTowerAvenger::new, AvengerTower.TDTowerAvenger::new, null),
    TD_TOWER_PROTECTOR(EntityType.ZOMBIE, ProtectorTower.TDTowerProtector.class, ProtectorTower.TDTowerProtector::new, ProtectorTower.TDTowerProtector::new, null),
    TD_TOWER_DEFENDER(EntityType.ZOMBIE, DefenderTower.TDTowerDefender.class, DefenderTower.TDTowerDefender::new, DefenderTower.TDTowerDefender::new, null),
    TD_TOWER_REVENANT(EntityType.ZOMBIE, RevenantTower.TDTowerRevenant.class, RevenantTower.TDTowerRevenant::new, RevenantTower.TDTowerRevenant::new, null),
    TD_TOWER_SPIRITGUARD(EntityType.ALLAY, SpiritguardTower.TDTowerSpiritguard.class, SpiritguardTower.TDTowerSpiritguard::new, SpiritguardTower.TDTowerSpiritguard::new, null),
    TD_TOWER_EARTHWARDEN(EntityType.ZOMBIE, EarthwardenTower.TDTowerEarthwarden.class, EarthwardenTower.TDTowerEarthwarden::new, EarthwardenTower.TDTowerEarthwarden::new, null),
    TD_TOWER_ASSASSIN(EntityType.ZOMBIE, AssassinTower.TDTowerAssassin.class, AssassinTower.TDTowerAssassin::new, AssassinTower.TDTowerAssassin::new, null),

    // whack a mole
    WHACK_A_MOLE_ARMOR_STAND(EntityType.ARMOR_STAND, MoleArmorStand.class, MoleArmorStand::new, MoleArmorStand::new, new Utils.SimpleEntityEquipment(
            null,
            null,
            null,
            null,
            null
    )
    ),
    // PVP
    TRICKSTER_DUMMY(EntityType.PLAYER, Decoy.class, Decoy::new, Decoy::new, null),

    // EFFIGY TRIALS
    GHOUL(EntityType.ZOMBIE, Ghoul.class, Ghoul::new, Ghoul::new, null),
    FALLEN_GHOUL(EntityType.ZOMBIE, FallenGhoul.class, FallenGhoul::new, FallenGhoul::new, null),
    STRAY(EntityType.STRAY, Stray.class, Stray::new, Stray::new, null),
    FALLEN_STRAY(EntityType.STRAY, FallenStray.class, FallenStray::new, FallenStray::new, null),
    ENHANCER_MECHAN(EntityType.IRON_GOLEM, EnhancerMechan.class, EnhancerMechan::new, EnhancerMechan::new, null),
    SPECTRAL_THIEF(EntityType.VEX, SpectralThief.class, SpectralThief::new, SpectralThief::new, null),
    LURKING_SLIME(EntityType.SLIME, LurkingSlime.class, LurkingSlime::new, LurkingSlime::new, null),
    DESECRATED_PALADIN(EntityType.ZOMBIE, DesecratedPaladin.class, DesecratedPaladin::new, DesecratedPaladin::new, null),
    UNDEAD_WARRIOR(EntityType.HUSK, UndeadWarrior.class, UndeadWarrior::new, UndeadWarrior::new, null)

    ;

    public static final Mob[] VALUES = values();
    public static final Mob[] BASIC = {
            ZOMBIE_LANCER, BASIC_WARRIOR_BERSERKER, SKELETAL_MAGE, PIG_DISCIPLE, SLIMY_ANOMALY,
            ARACHNO_VENARI, IVORY_KNIGHT
    };
    public static final Mob[] INTERMEDIATE = {
            HOUND, INTERMEDIATE_WARRIOR_BERSERKER, SKELETAL_WARLOCK, PIG_SHAMAN,
            BLAZING_KINDLE, WANDER_KNIGHTS, ZOMBIE_SWORDSMAN, ZOMBIE_LAMENT, IVORY_RONIN
    };
    public static final Mob[] ADVANCED = {
            ILLUMINATION, GOLEM_APPRENTICE, SCRUPULOUS_ZOMBIE, CELESTIAL_BOW_WIELDER,
            ZOMBIE_VANGUARD, ADVANCED_WARRIOR_BERSERKER,
            ZOMBIE_RAIDER, SKELETAL_ENTROPY, WITCH_DEACON, PIG_ALLEVIATOR, PALE_SERAPH
    };
    public static final Mob[] ELITE = {
            CELESTIAL_SWORD_WIELDER,
            RIFT_WALKER,
            OVERGROWN_ZOMBIE,
            SKELETAL_PYROMANCER,
            SKELETAL_ANOMALY,
            SKELETAL_ARCHER,
            CREEPY_BOMBER,
            SKELETAL_MESMER,
            ZOMBIE_KNIGHT,
            VOID_ZOMBIE,
            WANDER_WALKER,
            SLIME_GUARD,
            FIRE_SPLITTER
    };
    public static final Mob[] CHAMPION = {
            NIGHTMARE_ZOMBIE,
            PIG_PARTICLE,
            EXTREME_ZEALOT,
            SMART_SKELETON,
            SKELETAL_SORCERER,
            CELESTIAL_OPUS,
            OBSIDIAN_SENTINEL,
            SLIMY_CHESS,
            SOVEREIGN_GUARDIAN,
            ABYSS_WATCHER
    };
    public static final Mob[] BOSS_MINIONS = {
            BOLTARO_SHADOW,
            BOLTARO_EXLIED,
            TORMENTED_SOUL,
            DEPRESSED_SOUL,
            FURIOUS_SOUL,
            VOLTAIC_SOUL,
            AGONIZED_SOUL,
            NARMER_ACOLYTE,
            NARMERS_DEATH_CHARGE,
            ZENITH_LEGIONNAIRE,
            SOUL_OF_GRADIENT,
            MITHRA_EGG_SAC,
            ARACHNO_VENERATUS,
            SOUL_REAVER,
            ECHO_OF_BLADES,
            FROST_VEIL
    };
    public static final Mob[] BOSSES = {
            BOLTARO,
            GHOULCALLER,
            NARMER,
            MITHRA,
            ZENITH,
            MAGMATIC_OOZE,
            ILLUMINA,
            VOID,
            TORMENT,
            ONE_OF_NINE,
            ORBYZ,
            LILIUM,
            VEILKEEPER,
            CENTURION,
            VANGUARD
    };
    public static final Mob[] EVENT_BOSSES = {
            EVENT_BOLTARO, EVENT_NARMER, EVENT_MITHRA, EVENT_ILLUSION_CORE, EVENT_EXILED_CORE, EVENT_CALAMITY_CORE, EVENT_ILLUMINA,
            EVENT_APOLLO, EVENT_ARES, EVENT_PROMETHEUS, EVENT_ATHENA, EVENT_CRONUS, EVENT_ZEUS, EVENT_POSEIDON, EVENT_HADES,
            EVENT_THE_ARCHIVIST, EVENT_INQUISITEUR_EWA, EVENT_INQUISITEUR_EGA, EVENT_INQUISITEUR_VPA
    };
    public static final Mob[] EVENT_BOSS_MINIONS = {
            EVENT_BOLTARO_SHADOW,
            EVENT_NARMER_ACOLYTE,
            EVENT_NARMER_DJER,
            EVENT_NARMER_DJET,
            EVENT_MITHRA_FORSAKEN_FROST,
            EVENT_MITHRA_FORSAKEN_FOLIAGE,
            EVENT_MITHRA_FORSAKEN_SHRIEKER,
            EVENT_MITHRA_FORSAKEN_RESPITE,
            EVENT_MITHRA_FORSAKEN_CRUOR,
            EVENT_MITHRA_FORSAKEN_DEGRADER,
            EVENT_MITHRA_FORSAKEN_APPARITION,
            EVENT_MITHRA_POISONOUS_SPIDER,
            EVENT_MITHRA_EGG_SAC,
            EVENT_TERAS_MINOTAUR,
            EVENT_TERAS_CYCLOPS,
            EVENT_TERAS_SIREN,
            EVENT_TERAS_DRYAD,
            EVENT_UNPUBLISHED_GRIMOIRE,
            EVENT_EMBELLISHED_GRIMOIRE,
            EVENT_SCRIPTED_GRIMOIRE,
            EVENT_ROUGE_GRIMOIRE,
            EVENT_VIOLETTE_GRIMOIRE,
            EVENT_BLEUE_GRIMOIRE,
            EVENT_ORANGE_GRIMOIRE,
            EVENT_NECRONOMICON_GRIMOIRE
    };

    private static final Map<EntityType, ItemStack> MOB_HEADS = new HashMap<>() {{
        put(EntityType.ZOMBIE, new ItemStack(Material.ZOMBIE_HEAD));
        put(EntityType.SKELETON, new ItemStack(Material.SKELETON_SKULL));
        put(EntityType.SPIDER, SkullUtils.getSkullFrom(VanillaHeads.SPIDER));
        put(EntityType.SLIME, SkullUtils.getSkullFrom(VanillaHeads.SLIME));
        put(EntityType.MAGMA_CUBE, SkullUtils.getSkullFrom(VanillaHeads.MAGMACUBE));
        put(EntityType.BLAZE, SkullUtils.getSkullFrom(VanillaHeads.BLAZE));
        put(EntityType.WITCH, SkullUtils.getSkullFrom(VanillaHeads.WITCH));
        put(EntityType.IRON_GOLEM, SkullUtils.getSkullFrom(VanillaHeads.GOLEM));
        put(EntityType.WITHER_SKELETON, new ItemStack(Material.WITHER_SKELETON_SKULL));
        put(EntityType.PIGLIN, SkullUtils.getSkullFrom(VanillaHeads.PIGLIN));
        put(EntityType.WOLF, SkullUtils.getSkullFrom(VanillaHeads.ANGRY_WOLF));
        put(EntityType.ZOMBIE_VILLAGER, SkullUtils.getSkullFrom(VanillaHeads.ZOMBIE_VILLAGER));
        put(EntityType.HUSK, SkullUtils.getSkullFrom(VanillaHeads.HUSK));
        put(EntityType.STRAY, SkullUtils.getSkullFrom(VanillaHeads.STRAY));
        put(EntityType.CAVE_SPIDER, SkullUtils.getSkullFrom(VanillaHeads.CAVE_SPIDER));
        put(EntityType.SILVERFISH, SkullUtils.getSkullFrom(VanillaHeads.SILVERFISH));
        put(EntityType.ENDERMITE, SkullUtils.getSkullFrom(VanillaHeads.ENDERMITE));
        put(EntityType.ENDERMAN, SkullUtils.getSkullFrom(VanillaHeads.ENDERMAN));
        put(EntityType.GHAST, SkullUtils.getSkullFrom(VanillaHeads.GHAST));
        put(EntityType.ZOMBIFIED_PIGLIN, SkullUtils.getSkullFrom(VanillaHeads.ZOMBIFIED_PIGLIN));
        put(EntityType.PIGLIN_BRUTE, SkullUtils.getSkullFrom(VanillaHeads.PIGLIN_BRUTE));
        put(EntityType.HOGLIN, SkullUtils.getSkullFrom(VanillaHeads.HOGLIN));
        put(EntityType.ZOGLIN, SkullUtils.getSkullFrom(VanillaHeads.ZOGLIN));
        put(EntityType.EVOKER, SkullUtils.getSkullFrom(VanillaHeads.EVOKER));
        put(EntityType.VINDICATOR, SkullUtils.getSkullFrom(VanillaHeads.VINDICATOR));
        put(EntityType.PILLAGER, SkullUtils.getSkullFrom(VanillaHeads.PILLAGER));
        put(EntityType.RAVAGER, SkullUtils.getSkullFrom(VanillaHeads.RAVAGER));
        put(EntityType.ILLUSIONER, SkullUtils.getSkullFrom(VanillaHeads.ILLUSIONER));
        put(EntityType.VEX, SkullUtils.getSkullFrom(VanillaHeads.VEX));
        put(EntityType.CREEPER, new ItemStack(Material.CREEPER_HEAD));
        put(EntityType.PHANTOM, SkullUtils.getSkullFrom(VanillaHeads.PHANTOM));
        put(EntityType.DROWNED, SkullUtils.getSkullFrom(VanillaHeads.DROWNED));
        put(EntityType.GUARDIAN, SkullUtils.getSkullFrom(VanillaHeads.GUARDIAN));
        put(EntityType.ELDER_GUARDIAN, SkullUtils.getSkullFrom(VanillaHeads.ELDER_GUARDIAN));
        put(EntityType.WARDEN, SkullUtils.getSkullFrom(VanillaHeads.WARDEN));
        put(EntityType.ENDER_DRAGON, new ItemStack(Material.DRAGON_HEAD));
        put(EntityType.WITHER, SkullUtils.getSkullFrom(VanillaHeads.WITHER));
        put(EntityType.GIANT, new ItemStack(Material.ZOMBIE_HEAD));
    }};

    public final EntityType entityType;
    public final Class<?> mobClass;
    @Deprecated
    public final Function<Location, AbstractMob> createMobLegacy;
    public final Function7<Location, String, Integer, Float, Float, Float, Float, AbstractMob> createMobFunction;
    public final EntityEquipment equipment;
    public String name;
    public int maxHealth;
    public float walkSpeed;
    public float damageResistance;
    public float minMeleeDamage;
    public float maxMeleeDamage;

    Mob(
            EntityType entityType,
            Class<?> mobClass,
            Function<Location, AbstractMob> createMobLegacy,
            Function7<Location, String, Integer, Float, Float, Float, Float, AbstractMob> createMobFunction,
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
        EVENT_BOSS_MINIONS(Mob.EVENT_BOSS_MINIONS, "Event Boss Minion", NamedTextColor.RED, SkullUtils.getSkullFrom(SkullID.PINK_6)),
        EVENT_BOSSES(Mob.EVENT_BOSSES, "Event Boss", NamedTextColor.DARK_RED, SkullUtils.getSkullFrom(SkullID.RED_7)),

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
