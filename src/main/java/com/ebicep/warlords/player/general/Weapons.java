package com.ebicep.warlords.player.general;

import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.ebicep.warlords.player.general.WeaponsRarity.*;

public enum Weapons {

    // common
    BLUDGEON("Bludgeon", new ItemStack(Material.RABBIT_STEW), COMMON, WeaponsPvE.COMMON),
    TRAINING_SWORD("Training Sword", new ItemStack(Material.STONE_AXE), COMMON, WeaponsPvE.COMMON),
    CLAWS("Claws", new ItemStack(Material.MUTTON), COMMON, WeaponsPvE.COMMON),
    SCIMITAR("Scimitar", new ItemStack(Material.SALMON), COMMON, WeaponsPvE.COMMON),
    ORC_AXE("Orc Axe", new ItemStack(Material.PUMPKIN_PIE), COMMON, WeaponsPvE.COMMON),
    HATCHET("Hatchet", new ItemStack(Material.GOLDEN_HOE), COMMON, WeaponsPvE.COMMON),
    PIKE("Pike", new ItemStack(Material.ROTTEN_FLESH), COMMON, WeaponsPvE.COMMON),
    HAMMER("Hammer", new ItemStack(Material.IRON_SHOVEL), COMMON, WeaponsPvE.COMMON),
    WALKING_STICK("Walking Stick", new ItemStack(Material.STONE_PICKAXE), COMMON, WeaponsPvE.COMMON),
    STEEL_SWORD("Steel Sword", new ItemStack(Material.WOODEN_AXE), COMMON, WeaponsPvE.COMMON),

    // rare
    WORLD_TREE_BRANCH("World Tree Branch", new ItemStack(Material.IRON_PICKAXE), RARE, WeaponsPvE.RARE),
    GEM_AXE("Gem Axe", new ItemStack(Material.DIAMOND_HOE), RARE, WeaponsPvE.RARE),
    DOUBLEAXE("Doubleaxe", new ItemStack(Material.COOKED_COD), RARE, WeaponsPvE.RARE),
    MANDIBLES("Mandibles", new ItemStack(Material.PORKCHOP), RARE, WeaponsPvE.RARE),
    GOLDEN_GLADIUS("Golden Gladius", new ItemStack(Material.PUFFERFISH), RARE, WeaponsPvE.RARE),
    STONE_MALLET("Stone Mallet", new ItemStack(Material.GOLDEN_SHOVEL), RARE, WeaponsPvE.RARE),
    CUDGEL("Cudgel", new ItemStack(Material.COOKED_RABBIT), RARE, WeaponsPvE.RARE),
    VENOMSTRIKE("Venomstrike", new ItemStack(Material.GOLDEN_AXE), RARE, WeaponsPvE.RARE),
    HALBERD("Halberd", new ItemStack(Material.POTATO), RARE, WeaponsPvE.RARE),
    DEMONBLADE("Demonblade", new ItemStack(Material.IRON_AXE), RARE, WeaponsPvE.RARE),

    // epic
    RUNEBLADE("Runeblade", new ItemStack(Material.STONE_HOE), EPIC, WeaponsPvE.EPIC),
    KATAR("Katar", new ItemStack(Material.BEEF), EPIC, WeaponsPvE.EPIC),
    TENDERIZER("Tenderizer", new ItemStack(Material.COOKED_CHICKEN), EPIC, WeaponsPvE.EPIC),
    FLAMEWEAVER("Flameweaver", new ItemStack(Material.GOLDEN_PICKAXE), EPIC, WeaponsPvE.EPIC),
    NETHERSTEEL_KATANA("Nethersteel Katana", new ItemStack(Material.CHICKEN), EPIC, WeaponsPvE.EPIC),
    RUNIC_AXE("Runic Axe", new ItemStack(Material.BREAD), EPIC, WeaponsPvE.EPIC),
    NOMEGUSTA("Nomegusta", new ItemStack(Material.WOODEN_SHOVEL), EPIC, WeaponsPvE.EPIC),
    LUNAR_RELIC("Lunar Relic", new ItemStack(Material.MUSHROOM_STEW), EPIC, WeaponsPvE.EPIC),
    DIVINE_REACH("Divine Reach", new ItemStack(Material.BEETROOT_SEEDS), EPIC, WeaponsPvE.EPIC),
    GEMCRUSHER("Gemcrusher", new ItemStack(Material.DIAMOND_SHOVEL), EPIC, WeaponsPvE.EPIC),
    ELVEN_GREATSWORD("Elven Greatsword", new ItemStack(Material.IRON_HOE), EPIC, WeaponsPvE.EPIC),
    HAMMER_OF_LIGHT("Hammer of Light", new ItemStack(Material.STRING), EPIC, WeaponsPvE.EPIC),
    MAGMASWORD("Magmasword", new ItemStack(Material.TROPICAL_FISH), EPIC, WeaponsPvE.EPIC),
    DIAMONDSPARK("Diamondspark", new ItemStack(Material.DIAMOND_AXE), EPIC, WeaponsPvE.EPIC),
    ZWEIREAPER("Zweireaper", new ItemStack(Material.WOODEN_HOE), EPIC, WeaponsPvE.EPIC),

    // legendary
    VOID_EDGE("Void Edge", new ItemStack(Material.VILLAGER_SPAWN_EGG), LEGENDARY, WeaponsPvE.LEGENDARY),
    FELFLAME_BLADE("Felflame Blade", new ItemStack(Material.COOKED_SALMON), LEGENDARY, WeaponsPvE.LEGENDARY),
    AMARANTH("Amaranth", new ItemStack(Material.COOKED_MUTTON), LEGENDARY, WeaponsPvE.LEGENDARY),
    ARMBLADE("Armblade", new ItemStack(Material.COOKED_BEEF), LEGENDARY, WeaponsPvE.LEGENDARY),
    GEMINI("Gemini", new ItemStack(Material.COOKED_PORKCHOP), LEGENDARY, WeaponsPvE.LEGENDARY),
    DRAKEFANG("Drakefang", new ItemStack(Material.STONE_SHOVEL), LEGENDARY, WeaponsPvE.LEGENDARY),
    ABBADON("Abbadon", new ItemStack(Material.WOODEN_PICKAXE), LEGENDARY, WeaponsPvE.LEGENDARY),
    FROSTBITE("Frostbite", new ItemStack(Material.COD), LEGENDARY, WeaponsPvE.LEGENDARY),
    BROCCOMACE("Broccomace", new ItemStack(Material.BAKED_POTATO), LEGENDARY, WeaponsPvE.LEGENDARY),
    VOID_TWIG("Void Twig", new ItemStack(Material.DIAMOND_PICKAXE), LEGENDARY, WeaponsPvE.LEGENDARY),
    RUBY_THORN("Ruby Thorn", new ItemStack(Material.POISONOUS_POTATO), LEGENDARY, WeaponsPvE.LEGENDARY),
    ENDERFIST("Enderfist", new ItemStack(Material.APPLE), LEGENDARY, WeaponsPvE.LEGENDARY),

    // wl 2 exclusive
    NEW_LEAF_SCYTHE("Daphne's Harvest", new ItemStack(Material.GHAST_TEAR), LEGENDARY, WeaponsPvE.LEGENDARY),
    NEW_LEAF_AXE("Fate of Daphne", new ItemStack(Material.LEATHER), LEGENDARY, WeaponsPvE.LEGENDARY),
    NEW_LEAF_SWORD("Canopy's Jade Edge", new ItemStack(Material.LAPIS_LAZULI), LEGENDARY, WeaponsPvE.LEGENDARY),
    NEW_LEAF_SPEAR("Daphne's Viper", new ItemStack(Material.COCOA_BEANS), LEGENDARY, WeaponsPvE.LEGENDARY),
    SILVER_PHANTASM_SCYTHE("Tenth Plague", new ItemStack(Material.PRISMARINE_CRYSTALS), LEGENDARY, WeaponsPvE.LEGENDARY),
    SILVER_PHANTASM_SWORD("Hyperion's Awakening", new ItemStack(Material.PRISMARINE_SHARD), LEGENDARY, WeaponsPvE.LEGENDARY),
    SILVER_PHANTASM_SWORD_2("Blazeguard", new ItemStack(Material.FLINT), LEGENDARY, WeaponsPvE.LEGENDARY),
    SILVER_PHANTASM_SWORD_3("Venom", new ItemStack(Material.COAL), LEGENDARY, WeaponsPvE.LEGENDARY),
    SILVER_PHANTASM_SWORD_4("Lilium", new ItemStack(Material.STICK), LEGENDARY, WeaponsPvE.LEGENDARY),
    SILVER_PHANTASM_HAMMER("Wrath of Aether", new ItemStack(Material.QUARTZ), LEGENDARY, WeaponsPvE.LEGENDARY),
    SILVER_PHANTASM_STAFF("Wit of Oblivion", new ItemStack(Material.IRON_INGOT), LEGENDARY, WeaponsPvE.LEGENDARY),
    SILVER_PHANTASM_STAFF_2("Lament", new ItemStack(Material.INK_SAC), LEGENDARY, WeaponsPvE.LEGENDARY),
    SILVER_PHANTASM_SCIMITAR("Bloodquench", new ItemStack(Material.CARROT), LEGENDARY, WeaponsPvE.LEGENDARY),
    SILVER_PHANTASM_TRIDENT("Torment", new ItemStack(Material.FROGSPAWN), LEGENDARY, WeaponsPvE.LEGENDARY),
    SILVER_PHANTASM_SAWBLADE("Slayer", new ItemStack(Material.RABBIT), LEGENDARY, WeaponsPvE.LEGENDARY),
    CANDY_CANE("Candy Slapper", new ItemStack(Material.SUGAR), LEGENDARY, WeaponsPvE.LEGENDARY),
    FABLED_HEROICS_SCYTHE("Ghostly Sickles", new ItemStack(Material.SPIDER_EYE), LEGENDARY, WeaponsPvE.LEGENDARY),
    FABLED_HEROICS_SWORD("Nichirin", new ItemStack(Material.WHEAT), LEGENDARY, WeaponsPvE.LEGENDARY),
    FABLED_HEROICS_SWORD_2("Bumbleblade", new ItemStack(Material.NETHER_BRICK), LEGENDARY, WeaponsPvE.LEGENDARY),
    FABLED_HEROICS_DRUMSTICK("Cornelius", new ItemStack(Material.SUGAR_CANE), LEGENDARY, WeaponsPvE.LEGENDARY),
    LUNAR_JUSTICE("Lunar Justice", new ItemStack(Material.MAGMA_CREAM), LEGENDARY, WeaponsPvE.LEGENDARY),
    VORPAL_SWORD("Vorpal Sword", new ItemStack(Material.FERMENTED_SPIDER_EYE), LEGENDARY, WeaponsPvE.LEGENDARY),
    VIRIDIAN_BLADE("Viridian Blade", new ItemStack(Material.COOKIE), LEGENDARY, WeaponsPvE.LEGENDARY),
    SEVENTH("Seventh", new ItemStack(Material.SKELETON_SPAWN_EGG), LEGENDARY, WeaponsPvE.LEGENDARY),

    SUMSMASH_ACTION_FIGURE("sumSmash Action Figure", new ItemStack(Material.NETHERITE_HOE), LEGENDARY, WeaponsPvE.LEGENDARY, true),
    SITH_LIGHTSABER("Sith Lightsaber", new ItemStack(Material.NETHERITE_AXE), LEGENDARY, WeaponsPvE.LEGENDARY),
    JEDI_LIGHTSABER("Jedi Lightsaber", new ItemStack(Material.NETHERITE_PICKAXE), LEGENDARY, WeaponsPvE.LEGENDARY),
    SOUL_REAVER("Soul Reaver", new ItemStack(Material.NETHERITE_SWORD), LEGENDARY, WeaponsPvE.LEGENDARY),
    TIDEBREAKER("Tidebreaker", new ItemStack(Material.DRIED_KELP), LEGENDARY, WeaponsPvE.LEGENDARY),

    WARLORDS_II_PHOENIX_GRACE("Phoenix Grace", new ItemStack(Material.OXIDIZED_COPPER_CHEST), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_ABOMINABLE_GREAT_SABER("Abominable Great Saber", new ItemStack(Material.WAXED_COPPER_CHEST), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_ABOMINABLE_SCYTHE("Abominable Scythe", new ItemStack(Material.WAXED_WEATHERED_COPPER_CHEST), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_ENDERMIGHT("Endermight", new ItemStack(Material.WAXED_EXPOSED_COPPER_CHEST), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_AMETHYST_SHURIKEN("Amethyst Shuriken", new ItemStack(Material.COPPER_GOLEM_STATUE), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_ACIDIC_CLEAVER("Acidic Cleaver", new ItemStack(Material.WAXED_OXIDIZED_COPPER_CHEST), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_ANCIENT_ROYAL_GREAT_SWORD("Ancient Royal Great Sword", new ItemStack(Material.EXPOSED_COPPER_GOLEM_STATUE), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_AQUANTIC("Aquantic", new ItemStack(Material.WEATHERED_COPPER_GOLEM_STATUE), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_OCEAN_S_RAGE("Ocean's Rage", new ItemStack(Material.OXIDIZED_COPPER_GOLEM_STATUE), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_ASHURA_S_BLADE("Ashura's Blade", new ItemStack(Material.WAXED_COPPER_GOLEM_STATUE), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_BLOOD_EDGE("Blood Edge", new ItemStack(Material.WAXED_EXPOSED_COPPER_GOLEM_STATUE), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_BLOODY_DEATH("Bloody Death", new ItemStack(Material.WAXED_WEATHERED_COPPER_GOLEM_STATUE), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_CARIAN_KNIGHT_S_SWORD("Carian Knight's Sword", new ItemStack(Material.WAXED_OXIDIZED_COPPER_GOLEM_STATUE), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_CORRUPTED_EIDO("Corrupted Eido", new ItemStack(Material.COPPER_NUGGET), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_DAINSLEIF("Dainsleif", new ItemStack(Material.ARMADILLO_SPAWN_EGG), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_DARK_BLADE("Dark Blade", new ItemStack(Material.AXOLOTL_SPAWN_EGG), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_DARK_CLEAVER("Dark Cleaver", new ItemStack(Material.BAT_SPAWN_EGG), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_DEATH_KNIGHT_S_DAGGER("Death Knight's Dagger", new ItemStack(Material.COPPER_SWORD), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_DEATH_KNIGHT_S_SWORD("Death Knight's Sword", new ItemStack(Material.COPPER_PICKAXE), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_DEMIGOD_S_UNHOLY_HALBERD("Demigod's Unholy Halberd", new ItemStack(Material.COPPER_AXE), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_DEMIGOD_S_UNHOLY_BLADE("Demigod's Unholy Blade", new ItemStack(Material.COPPER_SHOVEL), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_DEMONIC_CLEAVER("Demonic Cleaver", new ItemStack(Material.COPPER_HORSE_ARMOR), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_CONSEQUENCE("Consequence", new ItemStack(Material.COPPER_GOLEM_SPAWN_EGG), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_ERUPTION("Eruption", new ItemStack(Material.BEE_SPAWN_EGG), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_DIVINE_JUSTICE("Divine Justice", new ItemStack(Material.BLAZE_SPAWN_EGG), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_DIVINE_REAPER("Divine Reaper", new ItemStack(Material.BOGGED_SPAWN_EGG), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_DIVINE_PUNISHER("Divine Punisher", new ItemStack(Material.BREEZE_SPAWN_EGG), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_RHITTA("Rhitta", new ItemStack(Material.CAMEL_SPAWN_EGG), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_DRAGON_SLAYING_BLADE("Dragon Slaying blade", new ItemStack(Material.CAT_SPAWN_EGG), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_EPIC_SWORD("Epic Sword", new ItemStack(Material.COPPER_NAUTILUS_ARMOR), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_ESTOC("Estoc", new ItemStack(Material.GOLDEN_NAUTILUS_ARMOR), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_FALLEN_GOD_S_SPEAR("Fallen God's Spear", new ItemStack(Material.IRON_NAUTILUS_ARMOR), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_FALLEN_GOD_S_SWORD("Fallen God's Sword", new ItemStack(Material.DIAMOND_NAUTILUS_ARMOR), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_FLORAL_LONGSWORD("Floral Longsword", new ItemStack(Material.NETHERITE_NAUTILUS_ARMOR), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_FLORAL_SABRE("Floral Sabre", new ItemStack(Material.NETHERITE_HORSE_ARMOR), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_FOREST_GUARDIAN_S_GLAIVE("Forest Guardian's Glaive", new ItemStack(Material.CAMEL_HUSK_SPAWN_EGG), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_FROST_AXE("Frost Axe", new ItemStack(Material.NAUTILUS_SPAWN_EGG), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_FROST_BLADE("Frost Blade", new ItemStack(Material.PARCHED_SPAWN_EGG), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_FROST_SCYTHE("Frost Scythe", new ItemStack(Material.ZOMBIE_NAUTILUS_SPAWN_EGG), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_WRAITH_SCYTHE("Wraith Scythe", new ItemStack(Material.BUSH), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_HOLY_MOONLIGHT_SWORD("Holy Moonlight Sword", new ItemStack(Material.FIREFLY_BUSH), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_HORNET_S_NEEDLE("Hornet's Needle", new ItemStack(Material.LEAF_LITTER), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_JADE_HALBERD("Jade Halberd", new ItemStack(Material.SHORT_DRY_GRASS), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_KATANA("Katana", new ItemStack(Material.TALL_DRY_GRASS), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_LONGSWORD("Longsword", new ItemStack(Material.BLUE_EGG), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_MASAMUNE("Masamune", new ItemStack(Material.BROWN_EGG), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_MJOLNIR("Mjolnir", new ItemStack(Material.DRIED_GHAST), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_MOLTEN_BLADE("Molten Blade", new ItemStack(Material.WHITE_HARNESS), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_DAWNBREAK("Dawnbreak", new ItemStack(Material.ORANGE_HARNESS), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_MURAMASA("Muramasa", new ItemStack(Material.MAGENTA_HARNESS), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_BLOSSOM("Blossom", new ItemStack(Material.LIGHT_BLUE_HARNESS), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_EIDO("Eido", new ItemStack(Material.YELLOW_HARNESS), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_PARTISAN("Partisan", new ItemStack(Material.LIME_HARNESS), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_PHARAOH_S_TREASURE("Pharaoh's Treasure", new ItemStack(Material.PINK_HARNESS), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_POWER_FUSE_HAMMER("Power Fuse Hammer", new ItemStack(Material.GRAY_HARNESS), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_POWER_FUSE_SWORD("Power Fuse Sword", new ItemStack(Material.LIGHT_GRAY_HARNESS), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_RIVERS_OF_BLOOD("Rivers Of Blood", new ItemStack(Material.CYAN_HARNESS), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_ROYAL_CHAKRAM("Royal Chakram", new ItemStack(Material.PURPLE_HARNESS), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_ROYAL_RAPIER("Royal Rapier", new ItemStack(Material.BLUE_HARNESS), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_SABRE("Sabre", new ItemStack(Material.BROWN_HARNESS), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_SCISSOR_BLADE("Scissor Blade", new ItemStack(Material.GREEN_HARNESS), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_SILVERINE_BLADE("Silverine Blade", new ItemStack(Material.RED_HARNESS), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_SENTINEL_S_WILL("Sentinel's Will", new ItemStack(Material.BLACK_HARNESS), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_SOUL_COLLECTOR("Soul Collector", new ItemStack(Material.MUSIC_DISC_TEARS), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_SOUL_EDGE("Soul Edge", new ItemStack(Material.HAPPY_GHAST_SPAWN_EGG), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_SOUL_CLAWS("Soul Claws", new ItemStack(Material.MUSIC_DISC_LAVA_CHICKEN), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_STEEL_SWORD("Steel Sword II", new ItemStack(Material.ACACIA_SHELF), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_SOUL_HARVESTER("Soul Harvester", new ItemStack(Material.BAMBOO_SHELF), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_SUNBREAK("Sunbreak", new ItemStack(Material.CHERRY_SHELF), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_TERRA_BLADE("Terra Blade", new ItemStack(Material.DARK_OAK_SHELF), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_THOUSAND_DEMON_DAGGERS("Thousand Demon Daggers", new ItemStack(Material.JUNGLE_SHELF), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_VAMPIRIC_NEEDLE("Vampiric Needle", new ItemStack(Material.OAK_SHELF), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_WAKIZASHI("Wakizashi", new ItemStack(Material.PALE_OAK_SHELF), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_WHISPERWIND("Whisperwind", new ItemStack(Material.SPRUCE_SHELF), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_WICKPIERCER("Wickpiercer", new ItemStack(Material.WARPED_SHELF), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_THUNDERBRAND("Thunderbrand", new ItemStack(Material.COPPER_BARS), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_WATCHER_CLAYMORE("Watcher Claymore", new ItemStack(Material.EXPOSED_COPPER_BARS), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_WATCHING_WARGLAIVE("Watching Warglaive", new ItemStack(Material.WEATHERED_COPPER_BARS), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_WAXWEAVER("Waxweaver", new ItemStack(Material.OXIDIZED_COPPER_BARS), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_STORM_BRINGER("Storm Bringer", new ItemStack(Material.WAXED_EXPOSED_COPPER_BARS), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_STORM_S_EDGE("Storm's Edge", new ItemStack(Material.WAXED_WEATHERED_COPPER_BARS), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_RIBBON_CLEAVER("Ribbon Cleaver", new ItemStack(Material.WAXED_OXIDIZED_COPPER_BARS), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_RIGHTEOUS_RELIC("Righteous Relic", new ItemStack(Material.COPPER_CHAIN), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_SOUL_STEALER("Soul Stealer", new ItemStack(Material.WEATHERED_COPPER_CHAIN), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_ENIGMA("Enigma", new ItemStack(Material.OXIDIZED_COPPER_CHAIN), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_HEARTHFLAME("Hearthflame", new ItemStack(Material.WAXED_COPPER_CHAIN), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_ICEWHISPER("Icewhisper", new ItemStack(Material.WAXED_EXPOSED_COPPER_CHAIN), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_MAGI_SCYTHE("Magi Scythe", new ItemStack(Material.WAXED_WEATHERED_COPPER_CHAIN), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_BRAMBLETHORN("Bramblethorn", new ItemStack(Material.EXPOSED_COPPER_LANTERN), LEGENDARY, WeaponsPvE.LEGENDARY),
    //WARLORDS_II_BRIMSTONE_CLAYMORE("Brimstone Claymore", new ItemStack(Material.WEATHERED_COPPER_LANTERN), LEGENDARY, WeaponsPvE.LEGENDARY),

    WARLORDS_II_HERO("Hero", new ItemStack(Material.CACTUS_FLOWER), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_PLAGUE_LONGSWORD("Plague Longsword", new ItemStack(Material.MANGROVE_SHELF), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_FRACTALIZE("Fractalize", new ItemStack(Material.WILDFLOWERS), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_STAR_S_EDGE("Star's Edge", new ItemStack(Material.WAXED_COPPER_BARS), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_EMBERBLADE("Emberblade", new ItemStack(Material.OXIDIZED_COPPER_LANTERN), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_SOULRENDER("Soulrender", new ItemStack(Material.EXPOSED_COPPER_CHAIN), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_EDGE_OF_THE_ASTRAL_PLANE("Edge Of The Astral Plane", new ItemStack(Material.CAVE_SPIDER_SPAWN_EGG), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_REQUIEM_OF_THE_NINTH_ABYSS("Requiem of the Ninth Abyss", new ItemStack(Material.WAXED_EXPOSED_COPPER_LANTERN), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_ARCANETHYST("Arcanethyst", new ItemStack(Material.WAXED_OXIDIZED_COPPER_CHAIN), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_TRUE_EXCALIBUR("Blade of a Thousand Voices", new ItemStack(Material.WAXED_COPPER_LANTERN), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_AWAKENED_LICHBLADE("Awakened Potential", new ItemStack(Material.COPPER_LANTERN), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_CREATION_SPLITTER("Creation Splitter", new ItemStack(Material.ALLAY_SPAWN_EGG), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_DEMONIC_BLADE("Demonic Blade", new ItemStack(Material.COPPER_HOE), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_THUNDER_BRINGER("Thunder Bringer", new ItemStack(Material.CRIMSON_SHELF), LEGENDARY, WeaponsPvE.LEGENDARY),
    WARLORDS_II_STOP_SIGN("Stop Sign", new ItemStack(Material.BIRCH_SHELF), LEGENDARY, WeaponsPvE.LEGENDARY, true),

    ;

    public static final Weapons[] VALUES = values();
    private final String name;
    private final ItemStack item;
    public final WeaponsRarity rarity;
    public final WeaponsPvE weaponsPvE;
    public boolean isUnlocked;
    public boolean patreonExclusive = false;

    Weapons(String name, ItemStack item, WeaponsRarity rarity, WeaponsPvE weaponsPvE) {
        this.name = name;
        this.item = item;
        this.rarity = rarity;
        this.isUnlocked = rarity != ASCENDANT;
        this.weaponsPvE = weaponsPvE;
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
    }

    Weapons(String name, ItemStack item, WeaponsRarity rarity, WeaponsPvE weaponsPvE, boolean patreonExclusive) {
        this(name, item, rarity, weaponsPvE);
        this.patreonExclusive = patreonExclusive;
    }

    public WeaponsRarity getRarity() {
        return this.rarity;
    }

    public String getName() {
        return this.name;
    }

    public static Weapons getWeapon(String name) {
        if (name == null) {
            return FELFLAME_BLADE;
        }
        for (Weapons value : Weapons.VALUES) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return FELFLAME_BLADE;
    }

    public static Weapons getRandomWeaponFromRarity(WeaponsRarity rarity) {
        List<Weapons> weapons = new ArrayList<>();
        for (Weapons value : Weapons.VALUES) {
            if (value.rarity == rarity) {
                weapons.add(value);
            }
        }
        return weapons.get(ThreadLocalRandom.current().nextInt(weapons.size()));
    }

    public ItemStack getItem() {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setCustomModelData(1001);
        item.setItemMeta(itemMeta);
        return item;
    }

}
