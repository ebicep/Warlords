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

        put(Mob.BOLTARO, "Boltaro opens by summoning Exiled Apostates, then chains Multi Hit melee—three rapid strikes with knockback, the last as true damage. Keep pressure on him while clearing his adds. Around half health he forcibly splits: the boss dies and spawns Shadow Boltaros plus more Exiled Apostates. Expect the fight to shift from one bruiser into a swarm of shadows.");
        put(Mob.GHOULCALLER, "Ghoulcaller floods the arena with Tormented Souls and other soul types that punish whoever hits them. His melee Soul Shackles silence you and nearby allies. Periodically he unleashes Fury—an AoE that hits much harder if the party has not been attacking him, so keep up sustained pressure. Focus souls when they clutter the floor, then burn the boss between Fury casts.");
        put(Mob.NARMER, "Narmer mixes Flame Burst with Ground Shred quakes that knock players away. He continually summons Zombie Lancers and Acolytes; falling minions can heal him. Below 40% health he becomes immune until all Acolytes are dead—clear them, but not too fast: killing several in a short window triggers Death Wish, a massive arena blast. Space Acolyte kills, then finish the execute.");
        put(Mob.MITHRA, "Echo of Mithra knocks players skyward with Virtue Strike, then slows herself to unload Flame Burst barrages. Hibernating Egg Sacs hatch Arachno Veneratus if not destroyed in time—break them early. At about 70% and 35% health she locks in place for Immolation: an expanding flame ring that damages players and heals her. Dodge the ring, clear eggs and spiders, then resume damage.");
        put(Mob.ZENITH, "Zenith opens with Armageddon lightning rings, Cleanse blasts, Thunder Clouds, and Legionnaire summons that heal him. Melee Uppercuts chain multi-hit knockback. Thunder Line Barrages sweep the floor between casts. In Endless, health thresholds unlock Shattering Chains phases and a final enrage that speeds him up and halves incoming damage. Spread for storms, interrupt Legionnaire healing, and burn hard before enrage.");
        put(Mob.MAGMATIC_OOZE, "Six Magmatic Oozes stack as a mounted tower; riders deal reduced damage until the stack breaks. Each ooze fires Fiery Projectiles, Flaming Slams, and a Heat Aura. The lead ooze lays Molten Fissures that turn floor into magma—standing on it deals ticking true damage. When one dies, passengers unmount and speed up. Spread and kill the stack pieces without camping magma.");
        put(Mob.ILLUMINA, "Illumina roots packs with Bramble and slows with Bramble Slowness while summoning Skeletal Sorcerers and opening with Golem Apprentices. At key health thresholds she starts timed Damage Checks: burn a damage quota before the timer or Vampiric Leash drains the party and Death Ray nearly one-shots everyone while she heals. Later phases add Nightmare Zombies. Commit bursts during Damage Checks and clear adds between them.");
        put(Mob.VOID, "Void shreds the ground, spawns souls, and drops Thunder Clouds between Armageddon-style lightning waves. At high health he runs Augmented Immolation expanding floors; mid-fight he demands huge timed Damage Checks with Golem Apprentice spawns—fail and the party is crushed. A second immolation and harder Damage Check follow, and near death he summons Boltaros. Save cooldowns for Damage Checks and respect the flame floors.");
        put(Mob.TORMENT, "Torment only takes full damage from players carrying Damage Check; without it your hits are heavily reduced. Soul Fire pulses and Tormenting Mark force a marked player to stay away from allies—Souls of Gradient amplify that mark. Health phases pull everyone into Whispers suction zones of true damage, spawn Celestial Opus, and later mark a Divine Protector whose aura shields nearby allies while Torment rains Divine Punishment. Keep Damage Check up, bait marks out, and protect the chosen player.");
        put(Mob.ONE_OF_NINE, "One of Nine is mostly immune and reflects greed damage outside short vulnerability windows—wait for the marked vulnerable halo before dumping. Orbiting swords, Reaving Blades aura, Giant Lasers, and chasing orbs punish standing still. Phases bring laser barrages with Rift Walkers, a timed Nine Crystal ornament check (fail = Valerian Death), spinning walls with meteors, triple lasers, then arena collapse. Kill Echo of Blades and Soul Reavers, and only burst during damage windows.");
        put(Mob.ORBYZ, "Orbyz permanently slows anyone nearby and periodically Blizzard Impendus-slows and damages the pack. He takes tiny damage unless someone holds the Empowering Relic and shares Empowering Allies—pick up relics and stack near the holder. Frost Veils spawn, Heavenly Spears rain ice, and health phases fire rotating radial lasers or long spear barrages. Stay mobile for spears and lasers, rotate the relic, and burn while allies are buffed.");
        put(Mob.LILIUM, "Lilium's Petal Crystals slash her damage taken until destroyed. Rose Gardens, Bouquet barrages, and blade waltzes force constant movement. Liliath Enigmas make her fully invulnerable while alive—kill them immediately. Phases include crystal shields, Enigma traps, sky conduit platform sequences, arena splits with Echoes, champion protectors during spear rain, and Crystalline Petal adds. Clear crystals and Enigmas first, then exploit openings between dances.");
        put(Mob.VEILKEEPER, "Veilkeeper is an invulnerable gatekeeper. He cancels all damage, freezes in place, and after a warning monologue begins striking the party with unavoidable true damage until you leave. There is no kill phase—treat him as a rejection encounter, not a DPS check.");
        put(Mob.CENTURION, "Centurion is a Nameless Crown bruiser with enormous health and steady crushing melee. He has no flashy phase kit yet—survive his raw pressure, keep healers ready, and burn him down like a pure tank-and-spank.");
        put(Mob.VANGUARD, "Vanguard hits even harder than Centurion, trading some speed for devastating melee. Expect a straightforward slugfest with massive health and no published special phases—coordinate defensives and attrition him down.");
        put(Mob.CHESSKING, "Chessking belches heavy slime AoE and floods the map with Slime Guards and Slimy Chess (capped adds). Projectile hits heal him via Blob Heal, so favor melee and abilities over bows. As his health drops he shrinks, gains speed and jump, and tightens Belch range while spawning faster. Kill slime adds, avoid feeding him projectiles, and finish the smaller, quicker form.");
        put(Mob.CHRONARCH, "Chronarch resists knockback behind his Clockwork Frame and pulses Mainspring damage on nearby players. Pendulum knocks and slows; Cogburst telegraphs clock-hand beams—step off the hands before they strike. He summons Clockbound Phantoms throughout. Below 65% Overclock speeds him up and adds Chrono Wardens; below 30% Twelve Chimes expands ringing circles—leave the circle each chime. Clear phantoms and respect the clock hands.");
        put(Mob.PHYSIRA, "Physira is a pylon race boss. Around 75% health she rings the arena with Six crystals—destroy every pylon before the countdown ends or the whole party takes lethal Valerian Death true damage. Stay mobile, split damage across pylons, and call when the timer is tight.");
        put(Mob.ENAVURIS, "Enavuris cannot be silenced or stunned. He volleys Ender Stones that damage, teleport, silence, and cut your outgoing damage, and Imprisonment locks a player in an obsidian cage with further silence and weakened damage. Enavurites and Vanishing Enavurites pad the fight. Free caged allies quickly, dodge stone volleys, and clear adds so the boss does not overwhelm the party.");
        put(Mob.RAID_MITHRA, "Raid Mithra is a colossal raid boss framed by orbiting crystals and a spinning royal halo. When she attacks she telegraphs a multi-stage chakram cleave into a royal impact—watch the sweep and step out before the final hit. Between strikes she paces with chess-like steps and queen flourishes. Learn the cleave timing, keep the raid spread for impacts, and treat her as a long endurance burn.");

        put(Mob.EVENT_BOLTARO, "Event Boltaro chains the same Multi Hit melee knockback combo as his dungeon twin. He does not open with Exiled Apostates; instead, near half health he splits into two Event Boltaro Shadows. Finish the shadows after the split—the original will not stay as a single target.");
        put(Mob.EVENT_NARMER, "Event Narmer is rooted in place and opens with either Djer or Djet plus Acolytes, Berserkers, and Lancers. He is immune while the ancestor lives, then again below 40% while Acolytes remain. Ally deaths heal him; rapid Acolyte kills still risk Death Wish. Kill the ancestor first, space Acolyte deaths, and endure Ground Shred quakes.");
        put(Mob.EVENT_MITHRA, "Event Mithra Virtue Strikes the pack, then at 75% enters Entangled: she stuns herself behind cobwebs while Egg Sacs hatch. During Entangled almost all damage is blocked except Ground Slam—slam the webs and smash sacs before they hatch Poisonous Spiders that heal her. Afterward she enrages, and later Immolation expands flame floors. Slam during Entangled, break eggs, then burn the enrage.");
        put(Mob.EVENT_ILLUSION_CORE, "Illusion Core is a stationary timed bomb with a 30-second fuse. It periodically blinds and slows the party under Chaos while summoning basic trash each second. Kill it before the timer or Core Explosion deals massive true damage to everyone. Ignore optional adds if needed—the timer is the real threat.");
        put(Mob.EVENT_EXILED_CORE, "Exiled Core mirrors Illusion Core with a longer 45-second fuse and tougher mid-tier summons. Chaos still blinds and slows on a cycle while the countdown ticks. Burn the core hard; failing the timer ends in the same lethal Core Explosion.");
        put(Mob.EVENT_CALAMITY_CORE, "Calamity Core is the longest fuse at 60 seconds and summons elite packs while Chaos debuffs the party. Stay on the core, peel only what you must, and finish before Core Explosion true-damages the raid.");
        put(Mob.EVENT_ILLUMINA, "Event Illumina slows with Bramble Slowness, can Prism Guard, and opens with Golem Apprentices. At 70%, 40%, and 10% she runs timed Damage Checks—deal the quota or die to the drain and Death Ray. Between checks she periodically spawns elite mixes. Burst every Damage Check and keep the floor clear enough to move.");
        put(Mob.EVENT_APOLLO, "Apollo stays rooted and opens by surrounding himself with Skeletal Mesmers, then keeps summoning Skeletal Entropy. Poison Arrow links to a player, applies strong Leech, and deals direct damage. Kill Mesmers early, cleanse or outheal Leech, and burn Apollo between arrow casts.");
        put(Mob.EVENT_ARES, "Ares is an aggressive God of War whose melee always Pierces and applies heavy Wounding. He continually summons Intermediate and Advanced Warrior Berserkers and opens with a ring of Advanced Berserkers. Cleave the warrior swarm so you are not overwhelmed, then tank-and-spank Ares through the wounding pressure.");
        put(Mob.EVENT_PROMETHEUS, "Prometheus opens with Illumination adds and fights with Burst of Flames plus Fireballs. If any player stands more than 20 blocks away he winds up a Barrage of Flames—stay in range or bait it safely. At half health he summons a ring of Fire Splitters. Stack loosely in melee range, kill Illumination and Splitters, and punish after barrages.");
        put(Mob.EVENT_ATHENA, "Athena is fully invulnerable while any other mob lives—linked enchant particles show the shield. She continually summons Vanguards, Lancers, and other zombies, and Shockwaves the arena. Below 25% health Shockwave cools down much faster. Clear every add before damaging her, then finish through the faster Shockwaves.");
        put(Mob.EVENT_CRONUS, "Cronus opens with Teras Cyclops, Minotaur, Siren, and Dryad packs and pulses piercing Heavenly Damage. Near 30% he Rejuvenates: repeated Ground Slams and self-heals, then permanently buffs Heavenly Damage and speed. Kill Teras before the heal phase if you can, interrupt or outpace the rejuvenation, and expect a meaner Cronus afterward.");
        put(Mob.EVENT_ZEUS, "Zeus wields Lightning Bolt (piercing), Chain Lightning, Lightning Rod damage buffs, and Healing Rain. When Hades dies he steals a large heal; when Poseidon dies his Lightning Rod hits harder. Killing blows grant him temporary speed. Coordinate the trio so you do not feed Zeus free power, and burst him when Rod and Rain are down.");
        put(Mob.EVENT_POSEIDON, "Poseidon cripples with multi-target Earthen Spikes, hurls Boulders, Ground Slams, and Last Stands. Hades dying heals him; Zeus dying upgrades Boulder damage. He also gains speed on killing blows. Keep spikes and boulders baited away from the stack, and time the trio kill order so Poseidon does not snowball.");
        put(Mob.EVENT_HADES, "Hades casts Fallen Souls, Incendiary Curse, and Undying Army while melee applies Leech. If Zeus or Poseidon dies and his resurrection is ready, he raises them at half health after a dig animation (two-minute cooldown). Killing blows speed him up. Burn Hades during the cooldown window, or kill the trio close together so he cannot chain-rez.");
        put(Mob.EVENT_THE_ARCHIVIST, "The Archivist mixes Crippling Strike, Chain Lightning, Ground Slam, Prism Guard, and Inferno, and cannot be silenced. Each ability cast permanently chips his max health, but every four Grimoire deaths grants him more damage resistance (up to a cap) and incoming crits against him are reduced. Force ability casts, then kill Grimoires carefully so you do not stack him too tanky too fast.");
        put(Mob.EVENT_INQUISITEUR_EWA, "Inquisiteur-EWA fights with a Berserker kit—Wounding Strike, Incendiary Curse, Ground Slam, Blood Lust, and Inferno. He opens with Golems, Shamans, and Swordsmen, then periodically spawns color Grimoires, Scripted Grimoires, and Necronomicon lasers. If too many adds live he Killing Blows them all and stacks damage reduction. Keep add count managed, interrupt Necronomicon targets, and burn during safe windows.");
        put(Mob.EVENT_INQUISITEUR_EGA, "Inquisiteur-EGA uses a Defender/Crusader mix: Righteous Strike, Freezing Breath, Ground Slam, Mystical Barrier, and Inspiring Presence. Same Grimoire and Necronomicon spawn pattern and Killing Blow add-cap rule as his siblings—overflowing adds grants him stacked damage reduction. Control spawns, break barriers, and focus the boss when the floor is clear.");
        put(Mob.EVENT_INQUISITEUR_VPA, "Inquisiteur-VPA runs an Aquamancer kit—Impaling Strike, Water Breath, Vitality Liquor, Sanctified Beacon, and Healing Rain. Expect the same Grimoire/Necronomicon cadence and Killing Blow purge that stacks damage reduction if adds overrun the room. Purge beacons and rain windows, keep Necronomicon lasers called out, and never let the add count spike into a free Killing Blow.");

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

        List<Mob> missing = new ArrayList<>();
        for (Mob.MobGroup group : JOURNAL_GROUPS) {
            for (Mob mob : group.mobs) {
                if (!MECHANICS.containsKey(mob)) {
                    missing.add(mob);
                }
            }
        }
        if (!missing.isEmpty()) {
            throw new IllegalStateException("Missing Discovery Journal mechanics for: " + missing);
        }
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
