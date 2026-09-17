package com.ebicep.warlords.pve.journal;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.mobs.Mob;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class MobDiscovery {

    public static final List<Mob.MobGroup> JOURNAL_GROUPS = List.of(
            Mob.MobGroup.BASIC,
            Mob.MobGroup.INTERMEDIATE,
            Mob.MobGroup.ADVANCED,
            Mob.MobGroup.ELITE,
            Mob.MobGroup.CHAMPION,
            Mob.MobGroup.BOSS_MINIONS,
            Mob.MobGroup.BOSSES,
            Mob.MobGroup.EVENT_BOSS_MINIONS,
            Mob.MobGroup.EVENT_BOSSES
    );

    private static final Map<Mob, String> MECHANICS = new EnumMap<>(Mob.class);

    static {
        put(Mob.ZOMBIE_LANCER, "");
        put(Mob.BASIC_WARRIOR_BERSERKER, "Uses Wounding Strike.");
        put(Mob.SKELETAL_MAGE, "Shoots Fireballs.");
        put(Mob.PIG_DISCIPLE, "");
        put(Mob.SLIMY_ANOMALY, "Explodes in a Shimmer AoE when it dies.");
        put(Mob.ARACHNO_VENARI, "");
        put(Mob.IVORY_KNIGHT, "");
        put(Mob.BRINEBOUND, "");
        put(Mob.SALTBLOOD_CORSAIR, "");
        put(Mob.FUNGAL_HUSK, "");
        put(Mob.FROSTBONE_STALKER, "");

        put(Mob.HOUND, "");
        put(Mob.INTERMEDIATE_WARRIOR_BERSERKER, "Uses Wounding Strike and stays permanently Berserked, taking extra damage.");
        put(Mob.SKELETAL_WARLOCK, "Shoots Fireballs.");
        put(Mob.PIG_SHAMAN, "Periodically heals nearby allies.");
        put(Mob.BLAZING_KINDLE, "Unleashes a fiery Kindle Wave AoE.");
        put(Mob.WANDER_KNIGHTS, "");
        put(Mob.ZOMBIE_SWORDSMAN, "Melee hits drain energy.");
        put(Mob.ZOMBIE_LAMENT, "Melee hits drain energy.");
        put(Mob.IVORY_RONIN, "");
        put(Mob.GRAVE_KNIGHT, "");
        put(Mob.ASHEN_PHYSICIAN, "");
        put(Mob.VEILED_CULTIST, "");
        put(Mob.SUNKEN_DELVER, "");

        put(Mob.ILLUMINATION, "Buffs nearby allies with Last Stand; explodes for Blight damage on death.");
        put(Mob.GOLEM_APPRENTICE, "Melee hits knock enemies upward.");
        put(Mob.SCRUPULOUS_ZOMBIE, "");
        put(Mob.CELESTIAL_BOW_WIELDER, "Shoots Fireballs and takes greatly reduced melee damage.");
        put(Mob.ZOMBIE_VANGUARD, "Melee hits slow enemies.");
        put(Mob.ADVANCED_WARRIOR_BERSERKER, "Uses Wounding Strike with permanent Berserk and Blood Lust healing.");
        put(Mob.ZOMBIE_RAIDER, "Immune to debuffs and highly resistant to knockback.");
        put(Mob.SKELETAL_ENTROPY, "Shoots Fireballs.");
        put(Mob.WITCH_DEACON, "Buffs allies with speed; hitting her shortens your ability cooldowns.");
        put(Mob.PIG_ALLEVIATOR, "Periodically heals nearby allies.");
        put(Mob.PALE_SERAPH, "");
        put(Mob.DUNE_JACKAL, "");
        put(Mob.ROTVEIL_MARAUDER, "");

        put(Mob.CELESTIAL_SWORD_WIELDER, "Takes greatly reduced projectile damage.");
        put(Mob.RIFT_WALKER, "Bursts of speed, cleanses slows, and knocks back on hit.");
        put(Mob.OVERGROWN_ZOMBIE, "");
        put(Mob.SKELETAL_PYROMANCER, "Casts Fireball and Flame Burst.");
        put(Mob.SKELETAL_ANOMALY, "Melee hits knock back and Cripple.");
        put(Mob.SKELETAL_ARCHER, "Mounted Fireball caster that flees while shooting.");
        put(Mob.CREEPY_BOMBER, "Explodes when players get close; takes reduced projectile damage.");
        put(Mob.SKELETAL_MESMER, "Casts Fireball, Flame Burst, and Void Shred.");
        put(Mob.ZOMBIE_KNIGHT, "Shortens nearby players' weapon cooldowns.");
        put(Mob.VOID_ZOMBIE, "Pulses Void Shred that damages and slows nearby players.");
        put(Mob.WANDER_WALKER, "At low health, recovers HP, gains speed, and resists knockback.");
        put(Mob.SLIME_GUARD, "Melee hits apply heavy Slowness.");
        put(Mob.FIRE_SPLITTER, "");

        put(Mob.NIGHTMARE_ZOMBIE, "Reflects projectile damage, strips buffs on hit, and is knockback-immune.");
        put(Mob.PIG_PARTICLE, "Heals nearby allies and uses Prism Guard.");
        put(Mob.EXTREME_ZEALOT, "Melee hits slow enemies.");
        put(Mob.SMART_SKELETON, "Strafes while shooting Fireballs.");
        put(Mob.SKELETAL_SORCERER, "Fireballs, Blighted Scorch DoT, and wounding melee; immune to knockback and slows.");
        put(Mob.CELESTIAL_OPUS, "");
        put(Mob.OBSIDIAN_SENTINEL, "");
        put(Mob.SLIMY_CHESS, "Drains energy and slows nearby players with Blob.");
        put(Mob.SOVEREIGN_GUARDIAN, "Freezes in place while any player is looking at it.");
        put(Mob.ABYSS_WATCHER, "Watches a player and punishes them for casting too many abilities.");

        put(Mob.BOLTARO_SHADOW, "Melee hits knock back.");
        put(Mob.BOLTARO_EXLIED, "Shoots Fireballs.");
        put(Mob.TORMENTED_SOUL, "When hit, increases the attacker's ability cooldowns; periodically drops aggro.");
        put(Mob.DEPRESSED_SOUL, "When hit, slows the attacker; periodically drops aggro.");
        put(Mob.FURIOUS_SOUL, "When hit, reflects a percent of the attacker's max health as true damage; periodically drops aggro.");
        put(Mob.VOLTAIC_SOUL, "When hit, drains energy and builds a Static Shock explosion on death; periodically drops aggro.");
        put(Mob.AGONIZED_SOUL, "When hit, shortens the attacker's ability and buff durations; periodically drops aggro.");
        put(Mob.NARMER_ACOLYTE, "Killing it near Narmer triggers Death Wish; periodically drops aggro.");
        put(Mob.NARMERS_DEATH_CHARGE, "Primes, then explodes for massive damage when players or Acolytes get close.");
        put(Mob.ZENITH_LEGIONNAIRE, "Heals Zenith with Remedy and knocks back on melee.");
        put(Mob.SOUL_OF_GRADIENT, "Tormenting Mark heavily damages players who have Damage Check; periodically drops aggro.");
        put(Mob.MITHRA_EGG_SAC, "Stationary egg—destroy it before it hatches spiders.");
        put(Mob.ARACHNO_VENERATUS, "");
        put(Mob.SOUL_REAVER, "");
        put(Mob.ECHO_OF_BLADES, "Periodically heals One of Nine and grants it damage reduction.");
        put(Mob.FROST_VEIL, "");

        put(Mob.BOLTARO, "Multi-hit melee that summons Exiled Apostates and splits into Shadow Boltaros at low health.");
        put(Mob.GHOULCALLER, "Spawns Torment souls, shackles on melee, and blasts Fury AoE that hits harder the less you attack him.");
        put(Mob.NARMER, "Flame Burst and Ground Shred; protected by Acolytes whose deaths trigger Death Wish; summons Zombie Lancers.");
        put(Mob.MITHRA, "Virtue Strike knockups, flame barrages, and Egg Sacs that hatch Arachno Veneratus if left alone.");
        put(Mob.ZENITH, "Armageddon storms, Cleanse blasts, Thunder Clouds, multi-hit Uppercuts, and Zenith Legionnaire summons.");
        put(Mob.MAGMATIC_OOZE, "Stacked oozes with fiery projectiles, Flaming Slam, Heat Aura, and magma floors; death unmounts the stack.");
        put(Mob.ILLUMINA, "Bramble roots and timed-damage phases; summons Golem Apprentices, Skeletal Sorcerers, and Nightmares.");
        put(Mob.VOID, "Ground Shred, soul spawns, Thunder Clouds, immolation floors, and timed-damage enrage phases.");
        put(Mob.TORMENT, "Soul Fire pulses and suction phases; deals full damage only with Damage Check; summons Souls of Gradient.");
        put(Mob.ONE_OF_NINE, "Orbiting swords, laser barrages, arena collapse, and summons Echo of Blades and Soul Reavers.");
        put(Mob.ORBYZ, "Blizzard AoEs, permanent slow aura, rotating lasers, Heavenly Spears, and Frost Veil summons.");
        put(Mob.LILIUM, "Crystal shields cut incoming damage; rose gardens, bouquet barrages, and Enigmas that block all damage while alive.");
        put(Mob.VEILKEEPER, "Invulnerable gatekeeper that rejects challengers and cannot be damaged.");
        put(Mob.CENTURION, "");
        put(Mob.VANGUARD, "");
        put(Mob.CHESSKING, "Belches AoE slime, summons Slime Guards and Slimy Chess; heals from projectiles and shrinks as HP drops.");
        put(Mob.CHRONARCH, "Pendulum knockbacks, Cogburst clock-hand strikes, Clockbound Phantom summons, plus Overclock and Twelve Chimes phases.");
        put(Mob.PHYSIRA, "Spawns pylons—destroy them before the timer or take lethal Valerian Death true damage.");
        put(Mob.ENAVURIS, "Hurls silencing Ender Stones, imprisons players in cages, and summons Enavurites.");
        put(Mob.RAID_MITHRA, "Orbiting crystals and a royal halo; telegraphs sweeping chakram strikes.");

        put(Mob.EVENT_BOLTARO, "Multi-hit melee that splits into Shadow Boltaros at low health.");
        put(Mob.EVENT_NARMER, "Ground Shred with Acolyte Death Wish; summons Djer or Djet and Berserkers.");
        put(Mob.EVENT_MITHRA, "Virtue Strikes, entangled web phases, and Egg Sacs that hatch Poisonous Spiders if unbroken.");
        put(Mob.EVENT_ILLUSION_CORE, "Timed core that blinds and slows, summons basic mobs, and explodes for lethal true damage if not killed in time.");
        put(Mob.EVENT_EXILED_CORE, "Timed core that blinds and slows, summons mid-tier mobs, and explodes for lethal true damage if not killed in time.");
        put(Mob.EVENT_CALAMITY_CORE, "Timed core that blinds and slows, summons elite mobs, and explodes for lethal true damage if not killed in time.");
        put(Mob.EVENT_ILLUMINA, "Bramble Slowness, Prism Guard, timed-damage phases, and periodic elite summons.");
        put(Mob.EVENT_APOLLO, "Poison Arrows that leech, and summons Skeletal Mesmers.");
        put(Mob.EVENT_ARES, "Piercing wounding melee; summons Advanced Warrior Berserkers and other warriors.");
        put(Mob.EVENT_PROMETHEUS, "Burst of Flames and Fireballs; barrages distant players and summons Illumination and Fire Splitters.");
        put(Mob.EVENT_ATHENA, "Invulnerable while other mobs live; Shockwave AoE that speeds up at low health.");
        put(Mob.EVENT_CRONUS, "Heavenly Damage AoE; summons Teras minions and self-heals into an enrage at low health.");
        put(Mob.EVENT_ZEUS, "Lightning Bolt, Chain Lightning, Lightning Rod, and Healing Rain; grows stronger when Poseidon or Hades falls.");
        put(Mob.EVENT_POSEIDON, "Earthen Spike, Boulder, Ground Slam, and Last Stand; grows stronger when Zeus or Hades falls.");
        put(Mob.EVENT_HADES, "Fallen Souls, Incendiary Curse, Undying Army, and leeching melee; can resurrect fallen Zeus or Poseidon.");
        put(Mob.EVENT_THE_ARCHIVIST, "Casts a full ability kit; loses health when casting and gains damage resistance as Grimoires die.");
        put(Mob.EVENT_INQUISITEUR_EWA, "Berserker-style kit; summons Grimoires and minions, stacking damage reduction as they fall.");
        put(Mob.EVENT_INQUISITEUR_EGA, "Defender and Crusader-style kit; summons Grimoires and minions, stacking damage reduction as they fall.");
        put(Mob.EVENT_INQUISITEUR_VPA, "Aquamancer-style kit; summons Grimoires and minions, stacking damage reduction as they fall.");

        put(Mob.EVENT_BOLTARO_SHADOW, "Fireball-casting shadows that knock back and can split into stronger copies on death.");
        put(Mob.EVENT_NARMER_ACOLYTE, "Killing it near Narmer triggers Death Wish; periodically drops aggro.");
        put(Mob.EVENT_NARMER_DJER, "Ground Shred; below 75% HP gains resistance and immunity to many knockbacks.");
        put(Mob.EVENT_NARMER_DJET, "Flame Burst plus arena-wide silence and Cripple; gains damage resistance below 75% HP.");
        put(Mob.EVENT_MITHRA_FORSAKEN_FROST, "Periodically slows all players.");
        put(Mob.EVENT_MITHRA_FORSAKEN_FOLIAGE, "Fights with Earthliving Weapon active.");
        put(Mob.EVENT_MITHRA_FORSAKEN_SHRIEKER, "Blinds nearby players.");
        put(Mob.EVENT_MITHRA_FORSAKEN_RESPITE, "Applies Leech to all players.");
        put(Mob.EVENT_MITHRA_FORSAKEN_CRUOR, "Applies Wounding to all players.");
        put(Mob.EVENT_MITHRA_FORSAKEN_DEGRADER, "Melee hits Cripple.");
        put(Mob.EVENT_MITHRA_FORSAKEN_APPARITION, "On first hit, turns invisible and gains damage and resistance.");
        put(Mob.EVENT_MITHRA_POISONOUS_SPIDER, "Poisons all players.");
        put(Mob.EVENT_MITHRA_EGG_SAC, "Stationary egg—left unbroken, it hatches Poisonous Spiders and heals Mithra.");
        put(Mob.EVENT_TERAS_MINOTAUR, "Uses Ground Slam.");
        put(Mob.EVENT_TERAS_CYCLOPS, "Melee hits launch enemies with heavy knockback.");
        put(Mob.EVENT_TERAS_SIREN, "Immune to projectile damage.");
        put(Mob.EVENT_TERAS_DRYAD, "Periodically heals nearby allies.");
        put(Mob.EVENT_UNPUBLISHED_GRIMOIRE, "");
        put(Mob.EVENT_EMBELLISHED_GRIMOIRE, "");
        put(Mob.EVENT_SCRIPTED_GRIMOIRE, "While alive, upgrades other Grimoires' cast abilities.");
        put(Mob.EVENT_ROUGE_GRIMOIRE, "Periodically casts random offensive player abilities.");
        put(Mob.EVENT_VIOLETTE_GRIMOIRE, "Periodically casts random mobility and support player abilities.");
        put(Mob.EVENT_BLEUE_GRIMOIRE, "Periodically casts random defensive and healing player abilities.");
        put(Mob.EVENT_ORANGE_GRIMOIRE, "Periodically casts random ultimate-style player abilities.");
        put(Mob.EVENT_NECRONOMICON_GRIMOIRE, "Locks onto a player with a laser and Smites them for near-lethal true damage.");
    }

    private MobDiscovery() {
    }

    public static List<Mob> journalMobs() {
        List<Mob> mobs = new ArrayList<>();
        for (Mob.MobGroup group : JOURNAL_GROUPS) {
            mobs.addAll(List.of(group.mobs));
        }
        return mobs;
    }

    public static boolean isCatalogued(Mob mob) {
        return MECHANICS.containsKey(mob);
    }

    public static String getMechanics(Mob mob) {
        return MECHANICS.getOrDefault(mob, "");
    }

    public static String getDisplayName(Mob mob) {
        if (mob.name != null && !mob.name.isEmpty()) {
            return mob.name;
        }
        return mob.name();
    }

    public static long getKills(DatabasePlayer databasePlayer, Mob mob) {
        String name = mob.name;
        if (name == null || name.isEmpty()) {
            return 0;
        }
        return databasePlayer.getPveStats().getMobKillCount(name);
    }

    public static boolean isDiscovered(DatabasePlayer databasePlayer, Mob mob) {
        return getKills(databasePlayer, mob) > 0;
    }

    public static int discoveredCount(DatabasePlayer databasePlayer, Mob.MobGroup group) {
        int discovered = 0;
        for (Mob mob : group.mobs) {
            if (isDiscovered(databasePlayer, mob)) {
                discovered++;
            }
        }
        return discovered;
    }

    private static void put(Mob mob, String mechanics) {
        MECHANICS.put(mob, mechanics);
    }
}
