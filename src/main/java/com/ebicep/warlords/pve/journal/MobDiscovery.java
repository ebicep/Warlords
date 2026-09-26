package com.ebicep.warlords.pve.journal;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.mobs.Mob;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

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
    private static final Pattern PARAGRAPH_BREAK = Pattern.compile("\\n+");
    private static final Pattern SENTENCE_BREAK = Pattern.compile("(?<=[.!?])\\s+(?=[A-Z])");

    static {
        put(Mob.ZOMBIE_LANCER, "");
        put(Mob.BASIC_WARRIOR_BERSERKER, "It uses Wounding Strike.");
        put(Mob.SKELETAL_MAGE, "It shoots fireballs at players.");
        put(Mob.PIG_DISCIPLE, "");
        put(Mob.SLIMY_ANOMALY, "When it dies, it explodes and damages nearby players with Shimmer.");
        put(Mob.ARACHNO_VENARI, "");
        put(Mob.IVORY_KNIGHT, "");
        put(Mob.BRINEBOUND, "");
        put(Mob.SALTBLOOD_CORSAIR, "");
        put(Mob.FUNGAL_HUSK, "");
        put(Mob.FROSTBONE_STALKER, "");

        put(Mob.HOUND, "");
        put(Mob.INTERMEDIATE_WARRIOR_BERSERKER, "It uses Wounding Strike and stays Berserked at all times, which also makes it take extra damage.");
        put(Mob.SKELETAL_WARLOCK, "It shoots fireballs at players.");
        put(Mob.PIG_SHAMAN, "It periodically heals nearby allies.");
        put(Mob.BLAZING_KINDLE, "It unleashes Kindle Wave, a burst of fire that damages nearby players.");
        put(Mob.WANDER_KNIGHTS, "");
        put(Mob.ZOMBIE_SWORDSMAN, "Its melee hits drain energy.");
        put(Mob.ZOMBIE_LAMENT, "Its melee hits drain energy.");
        put(Mob.IVORY_RONIN, "");
        put(Mob.GRAVE_KNIGHT, "");
        put(Mob.ASHEN_PHYSICIAN, "");
        put(Mob.VEILED_CULTIST, "");
        put(Mob.SUNKEN_DELVER, "");

        put(Mob.ILLUMINATION, "It grants Last Stand to nearby allies. When it dies, it explodes and deals Blight damage.");
        put(Mob.GOLEM_APPRENTICE, "Its melee hits knock enemies into the air.");
        put(Mob.SCRUPULOUS_ZOMBIE, "");
        put(Mob.CELESTIAL_BOW_WIELDER, "It shoots fireballs and takes far less damage from melee attacks.");
        put(Mob.ZOMBIE_VANGUARD, "Its melee hits slow enemies.");
        put(Mob.ADVANCED_WARRIOR_BERSERKER, "It uses Wounding Strike, stays Berserked at all times, and heals itself with Blood Lust.");
        put(Mob.ZOMBIE_RAIDER, "It is immune to debuffs and highly resistant to knockback.");
        put(Mob.SKELETAL_ENTROPY, "It shoots fireballs at players.");
        put(Mob.WITCH_DEACON, "It speeds up nearby allies. Hitting her shortens your ability cooldowns.");
        put(Mob.PIG_ALLEVIATOR, "It periodically heals nearby allies.");
        put(Mob.PALE_SERAPH, "");
        put(Mob.DUNE_JACKAL, "");
        put(Mob.ROTVEIL_MARAUDER, "");

        put(Mob.CELESTIAL_SWORD_WIELDER, "It takes far less damage from projectiles.");
        put(Mob.RIFT_WALKER, "It periodically gains a burst of speed, clears slow effects from itself, and knocks players back when it hits.");
        put(Mob.OVERGROWN_ZOMBIE, "");
        put(Mob.SKELETAL_PYROMANCER, "It casts Fireball and Flame Burst.");
        put(Mob.SKELETAL_ANOMALY, "Its melee hits knock players back and Cripple them.");
        put(Mob.SKELETAL_ARCHER, "It rides a mount, flees from players, and shoots fireballs while running.");
        put(Mob.CREEPY_BOMBER, "It explodes when players get close. It also takes less damage from projectiles.");
        put(Mob.SKELETAL_MESMER, "It casts Fireball, Flame Burst, and Void Shred.");
        put(Mob.ZOMBIE_KNIGHT, "It shortens nearby players' weapon cooldowns.");
        put(Mob.VOID_ZOMBIE, "It pulses Void Shred, which damages and slows nearby players.");
        put(Mob.WANDER_WALKER, "At low health, it recovers health, gains speed, and resists knockback.");
        put(Mob.SLIME_GUARD, "Its melee hits apply a heavy slow.");
        put(Mob.FIRE_SPLITTER, "");
        put(Mob.CELESTIAL_OPUS, "");
        put(Mob.SOVEREIGN_GUARDIAN, "It freezes in place while any player is looking at it.");

        put(Mob.NIGHTMARE_ZOMBIE, "It reflects projectile damage, strips buffs when it hits, and cannot be knocked back.");
        put(Mob.PIG_PARTICLE, "It heals nearby allies and uses Prism Guard.");
        put(Mob.EXTREME_ZEALOT, "Its melee hits slow enemies.");
        put(Mob.SMART_SKELETON, "It strafes while shooting fireballs.");
        put(Mob.SKELETAL_SORCERER, "It shoots fireballs, applies Blighted Scorch as damage over time, and wounds players with melee attacks. It cannot be knocked back or slowed.");
        put(Mob.OBSIDIAN_SENTINEL, "");
        put(Mob.SLIMY_CHESS, "It uses Blob to drain energy and slow nearby players.");
        put(Mob.ABYSS_WATCHER, "It watches a player and punishes them for casting too many abilities.");
        put(Mob.ENDERMAN_ANOMALY, "Taine teleports around the map and can kidnap a nearby player. Its melee hits also slow you.");
        put(Mob.LANTERN_DREDGER, "Lantern Glow grants nearby allies 30% damage reduction. Blackout blinds players inside the aura.");
        put(Mob.BARNACLE_BRUTE, "It hooks the farthest player, pulls them in, and slows them.");
        put(Mob.SILTSTALKER, "It submerges, becomes untargetable, then emerges behind a player. The follow-up hit Weakens that player.");
        put(Mob.VOID_JAILER, "It seals a player in a Void Prison. Allies must damage the Jailer to break it. The trapped player deals reduced damage and is punished for leaving the circle.");
        put(Mob.SOULBINDER, "It is immune to damage while its Bound Archers are alive. Killing the archers exposes it for a short time before it summons a new set.");
        put(Mob.DEVOURING_IDOL, "It does not move. Nearby ability casts charge its Energy. At 100% Energy, it pulses heavy damage to players and shields nearby allied mobs.");
        put(Mob.ENAVURITE, "It is leashed to Enavuris, and its melee attacks heal the boss. It is immune to Crippling Strike.");
        put(Mob.VANISHING_ENAVURITE, "It stays invisible and moves faster until it takes damage. Critical hits force Enavuris to target that player. It cannot be stunned.");

        put(Mob.BOLTARO_SHADOW, "Its melee hits knock players back.");
        put(Mob.BOLTARO_EXLIED, "It shoots fireballs at players.");
        put(Mob.TORMENTED_SOUL, "When it is hit, it increases the attacker's ability cooldowns. It also periodically loses its current target.");
        put(Mob.DEPRESSED_SOUL, "When it is hit, it slows the attacker. It also periodically loses its current target.");
        put(Mob.FURIOUS_SOUL, "When it is hit, it reflects a portion of the attacker's maximum health as true damage. It also periodically loses its current target.");
        put(Mob.VOLTAIC_SOUL, "When it is hit, it drains energy. On death, it explodes with Static Shock. It also periodically loses its current target.");
        put(Mob.AGONIZED_SOUL, "When it is hit, it shortens the attacker's ability and buff durations. It also periodically loses its current target.");
        put(Mob.NARMER_ACOLYTE, "Killing it near Narmer can trigger Death Wish. It also periodically loses its current target.");
        put(Mob.NARMERS_DEATH_CHARGE, "It primes itself, then explodes for massive damage when players or Acolytes get close.");
        put(Mob.ZENITH_LEGIONNAIRE, "It heals Zenith with Remedy and knocks players back with melee attacks.");
        put(Mob.SOUL_OF_GRADIENT, "Its Tormenting Mark heavily damages players who have Damage Check. It also periodically loses its current target.");
        put(Mob.MITHRA_EGG_SAC, "This stationary egg hatches spiders if it is not destroyed in time.");
        put(Mob.ARACHNO_VENERATUS, "");
        put(Mob.SOUL_REAVER, "");
        put(Mob.ECHO_OF_BLADES, "It periodically heals One of Nine and grants it damage reduction.");
        put(Mob.FROST_VEIL, "");

        put(Mob.BOLTARO, "Boltaro starts by summoning Exiled Apostates. He then uses Multi Hit, a chain of three rapid melee strikes that knock players back, with the last hit dealing true damage. Around half health, he splits: the original boss dies and is replaced by Shadow Boltaros and more Exiled Apostates. The fight then becomes a swarm of shadows instead of a single target.");
        put(Mob.GHOULCALLER, "Ghoulcaller fills the arena with Tormented Souls and other souls that punish whoever hits them. His melee attack, Soul Shackles, silences you and nearby allies. From time to time he uses Fury, an area attack that hits much harder if the party has not been attacking him. Clear souls when the floor gets crowded, and deal damage to him between Fury casts.");
        put(Mob.NARMER, "Narmer mixes Flame Burst with Ground Shred quakes that knock players away. He continually summons Zombie Lancers and Acolytes, and fallen minions can heal him. Below 40% health he becomes immune until all Acolytes are dead. Clear them, but not too quickly: killing several in a short window triggers Death Wish, a massive blast across the arena. Space out Acolyte kills, then finish him.");
        put(Mob.ECHO_OF_GRADIENT, "Echo of Gradient stacks Wedge with each melee hit. At four stacks, Settle deals heavy damage and Seated slows her. Wedge falls off if she stops hitting you. Below half health she cracks, and two stacks are enough to Settle. Load targets the healthiest player, deals heavy damage, and heals her.");
        put(Mob.MITHRA, "Echo of Mithra knocks players into the air with Virtue Strike, then slows herself to fire Flame Burst barrages. Hibernating Egg Sacs hatch Arachno Veneratus if they are not destroyed in time, so break them early. At about 70% and 35% health she locks in place for Immolation, an expanding flame ring that damages players and heals her. Dodge the ring, clear eggs and spiders, then resume damage.");
        put(Mob.ZENITH, "Zenith opens with Armageddon lightning rings, Cleanse blasts, Thunder Clouds, and Legionnaire summons that heal him. His melee Uppercuts are a multi-hit knockback combo. Thunder Line Barrages also sweep the floor between casts. In Endless, health thresholds unlock Shattering Chains phases and a final enrage that speeds him up and halves incoming damage. Spread out for storms, interrupt Legionnaire healing, and deal heavy damage before enrage.");
        put(Mob.MAGMATIC_OOZE, "Six Magmatic Oozes stack as a mounted tower. Riders deal reduced damage until the stack breaks. Each ooze fires Fiery Projectiles, uses Flaming Slams, and has a Heat Aura. The lead ooze lays Molten Fissures that turn the floor into magma, which deals ticking true damage if you stand on it. When one dies, the others unmount and speed up. Spread out and kill the stack pieces without standing on magma.");
        put(Mob.ILLUMINA, "Illumina roots groups of players with Bramble and slows them with Bramble Slowness. She summons Skeletal Sorcerers and opens with Golem Apprentices. At key health thresholds she starts timed Damage Checks: deal enough damage before the timer ends, or Vampiric Leash drains the party and Death Ray nearly kills everyone while she heals. Later phases add Nightmare Zombies. Deal heavy damage during Damage Checks and clear extra enemies between them.");
        put(Mob.VOID, "Void shreds the ground, spawns souls, and drops Thunder Clouds between Armageddon-style lightning waves. At high health he uses Augmented Immolation, which expands flame across the floor. Mid-fight he demands large timed Damage Checks with Golem Apprentice spawns. If you fail, the party is crushed. A second immolation and a harder Damage Check follow, and near death he summons Boltaros. Save cooldowns for Damage Checks and stay off the flame floors.");
        put(Mob.TORMENT, "Torment only takes full damage from players who have Damage Check. Without it, your hits are heavily reduced. Soul Fire pulses and Tormenting Mark force a marked player to stay away from allies, and Souls of Gradient make that mark worse. During health phases, he pulls everyone into Whispers suction zones of true damage, spawns Celestial Opus, and later marks a Divine Protector whose aura shields nearby allies while Torment rains Divine Punishment. Keep Damage Check up, draw marked players away from the group, and protect the chosen player.");
        put(Mob.ONE_OF_NINE, "One of Nine is mostly immune and reflects damage taken outside short vulnerability windows. Wait for the marked vulnerable halo before dealing heavy damage. Orbiting swords, a Reaving Blades aura, Giant Lasers, and chasing orbs punish standing still. Later phases bring laser barrages with Rift Walkers, a timed Nine Crystal check that kills the party with Valerian Death if it fails, spinning walls with meteors, triple lasers, and then arena collapse. Kill Echo of Blades and Soul Reavers, and only deal heavy damage during damage windows.");
        put(Mob.ORBYZ, "Orbyz permanently slows anyone nearby and periodically uses Blizzard Impendus to slow and damage the group. He takes very little damage unless someone holds the Empowering Relic and shares Empowering Allies, so pick up relics and stay near the holder. Frost Veils spawn, Heavenly Spears rain ice, and health phases fire rotating radial lasers or long spear barrages. Stay mobile for spears and lasers, rotate the relic, and deal damage while allies are buffed.");
        put(Mob.LILIUM, "Lilium's Petal Crystals reduce the damage she takes until they are destroyed. Rose Gardens, Bouquet barrages, and blade waltzes force constant movement. Liliath Enigmas make her fully invulnerable while they are alive, so kill them immediately. Later phases include crystal shields, Enigma traps, sky conduit platform sequences, arena splits with Echoes, champion protectors during spear rain, and Crystalline Petal extras. Clear crystals and Enigmas first, then deal damage between dances.");
        put(Mob.VEILKEEPER, "Veilkeeper is an invulnerable gatekeeper. He cancels all damage, freezes in place, and after a warning monologue begins striking the party with unavoidable true damage until you leave. There is no kill phase. Treat him as a rejection encounter, not a damage check.");
        put(Mob.CENTURION, "Centurion is a Nameless Crown bruiser with enormous health and steady crushing melee. He does not have special phases yet. Survive his raw pressure, keep healers ready, and wear him down.");
        put(Mob.VANGUARD, "Vanguard hits even harder than Centurion, trading some speed for devastating melee. Expect a straightforward fight with massive health and no special phases. Coordinate defensive abilities and wear him down.");
        put(Mob.CHESSKING, "Chessking belches heavy slime around him and floods the map with Slime Guards and Slimy Chess. There is a cap on how many extra slimes can spawn. Projectile hits heal him through Blob Heal, so favor melee and abilities over bows. As his health drops, he shrinks, gains speed and jump, tightens Belch range, and spawns faster. Kill the extra slimes, avoid feeding him projectiles, and finish the smaller, quicker form.");
        put(Mob.CHRONARCH, "Chronarch resists knockback with his Clockwork Frame and pulses Mainspring damage on nearby players. Pendulum knocks players back and slows them. Cogburst telegraphs clock-hand beams, so step off the hands before they strike. He summons Clockbound Phantoms throughout the fight. Below 65% health, Overclock speeds him up and adds Chrono Wardens. Below 30% health, Twelve Chimes expands ringing circles; leave the circle each chime. Clear phantoms and stay off the clock hands.");
        put(Mob.PHYSIRA, "Physira forces a race to destroy pylons. Around 75% health she rings the arena with six crystals. Destroy every pylon before the countdown ends, or the whole party takes lethal Valerian Death true damage. Stay mobile, split damage across pylons, and call out when the timer is tight.");
        put(Mob.ENAVURIS, "Enavuris cannot be silenced or stunned. He volleys Ender Stones that damage, teleport, silence, and cut your outgoing damage. Imprisonment locks a player in an obsidian cage with further silence and weakened damage. Enavurites and Vanishing Enavurites also join the fight. Free caged allies quickly, dodge stone volleys, and clear extras so the boss does not overwhelm the party.");
        put(Mob.RAID_MITHRA, "Raid Mithra is a colossal raid boss framed by orbiting crystals and a spinning royal halo. When she attacks, she telegraphs a multi-stage chakram cleave into a royal impact. Watch the sweep and step out before the final hit. Between strikes she paces with chess-like steps and queen flourishes. Learn the cleave timing, keep the raid spread for impacts, and treat the fight as a long endurance battle.");

        put(Mob.EVENT_BOLTARO, "Event Boltaro uses the same Multi Hit melee knockback combo as his dungeon counterpart. He does not open with Exiled Apostates. Instead, near half health he splits into two Event Boltaro Shadows. Finish the shadows after the split, because the original will not remain as a single target.");
        put(Mob.EVENT_NARMER, "Event Narmer is rooted in place and opens with either Djer or Djet, plus Acolytes, Berserkers, and Lancers. He is immune while the ancestor lives, then becomes immune again below 40% health while Acolytes remain. Ally deaths heal him, and killing Acolytes too quickly still risks Death Wish. Kill the ancestor first, space out Acolyte deaths, and endure Ground Shred quakes.");
        put(Mob.EVENT_MITHRA, "Event Mithra uses Virtue Strike on the group, then at 75% health enters Entangled. She stuns herself behind cobwebs while Egg Sacs hatch. During Entangled, almost all damage is blocked except Ground Slam, so slam the webs and smash sacs before they hatch Poisonous Spiders that heal her. Afterward she enrages, and later Immolation expands flame across the floor. Slam during Entangled, break eggs, then deal damage during the enrage.");
        put(Mob.EVENT_ILLUSION_CORE, "Illusion Core is a stationary timed bomb with a 30-second fuse. It periodically blinds and slows the party with Chaos while summoning basic enemies each second. Kill it before the timer ends, or Core Explosion deals massive true damage to everyone. Ignore extra enemies if needed. The timer is the real threat.");
        put(Mob.EVENT_EXILED_CORE, "Exiled Core works like Illusion Core, but with a longer 45-second fuse and tougher mid-tier summons. Chaos still blinds and slows on a cycle while the countdown ticks. Deal damage to the core quickly. Failing the timer ends in the same lethal Core Explosion.");
        put(Mob.EVENT_CALAMITY_CORE, "Calamity Core has the longest fuse at 60 seconds and summons elite packs while Chaos debuffs the party. Stay on the core and only stop to fight extra enemies when you must. Finish before Core Explosion deals true damage to everyone.");
        put(Mob.EVENT_ILLUMINA, "Event Illumina slows players with Bramble Slowness, can use Prism Guard, and opens with Golem Apprentices. At 70%, 40%, and 10% health she runs timed Damage Checks. Deal the required damage or die to the drain and Death Ray. Between checks she periodically spawns elite mixes. Deal heavy damage during every Damage Check and keep the floor clear enough to move.");
        put(Mob.EVENT_APOLLO, "Apollo stays rooted and opens by surrounding himself with Skeletal Mesmers, then keeps summoning Skeletal Entropy. Poison Arrow links to a player, applies strong Leech, and deals direct damage. Kill Mesmers early, cleanse or outheal Leech, and deal damage to Apollo between arrow casts.");
        put(Mob.EVENT_ARES, "Ares is an aggressive God of War whose melee always Pierces and applies heavy Wounding. He continually summons Intermediate and Advanced Warrior Berserkers and opens with a ring of Advanced Berserkers. Cut down the warrior swarm so you are not overwhelmed, then focus Ares through the wounding pressure.");
        put(Mob.EVENT_PROMETHEUS, "Prometheus opens with Illumination extras and fights with Burst of Flames plus fireballs. If any player stands more than 20 blocks away, he winds up a Barrage of Flames. Stay in range or lure it out safely. At half health he summons a ring of Fire Splitters. Stay loosely grouped in melee range, kill Illumination and Splitters, and deal damage after barrages.");
        put(Mob.EVENT_ATHENA, "Athena is fully invulnerable while any other mob lives. Linked enchant particles show the shield. She continually summons Vanguards, Lancers, and other zombies, and uses Shockwave across the arena. Below 25% health, Shockwave cools down much faster. Clear every extra enemy before damaging her, then finish through the faster Shockwaves.");
        put(Mob.EVENT_CRONUS, "Cronus opens with Teras Cyclops, Minotaur, Siren, and Dryad packs and pulses piercing Heavenly Damage. Near 30% health he Rejuvenates with repeated Ground Slams and self-heals, then permanently increases Heavenly Damage and speed. Kill the Teras before the heal phase if you can. Interrupt or outpace the rejuvenation, and expect a stronger Cronus afterward.");
        put(Mob.EVENT_ZEUS, "Zeus uses piercing Lightning Bolt, Chain Lightning, Lightning Rod damage buffs, and Healing Rain. When Hades dies, Zeus steals a large heal. When Poseidon dies, Lightning Rod hits harder. Killing blows grant him temporary speed. Coordinate the trio so you do not feed Zeus free power, and deal heavy damage when Lightning Rod and Healing Rain are down.");
        put(Mob.EVENT_POSEIDON, "Poseidon cripples players with multi-target Earthen Spikes, hurls Boulders, uses Ground Slam, and uses Last Stand. When Hades dies, Poseidon heals. When Zeus dies, Boulder damage increases. He also gains speed on killing blows. Keep spikes and boulders away from the group, and time the trio kill order so Poseidon does not grow too strong.");
        put(Mob.EVENT_HADES, "Hades casts Fallen Souls, Incendiary Curse, and Undying Army, and his melee applies Leech. If Zeus or Poseidon dies and his resurrection is ready, he raises them at half health after a dig animation, with a two-minute cooldown. Killing blows speed him up. Deal damage to Hades during the cooldown window, or kill the trio close together so he cannot resurrect them.");
        put(Mob.EVENT_THE_ARCHIVIST, "The Archivist uses Crippling Strike, Chain Lightning, Ground Slam, Prism Guard, and Inferno, and cannot be silenced. Each ability he casts permanently reduces his maximum health. Every four Grimoire deaths grant him more damage resistance, up to a cap, and incoming critical hits against him are reduced. Force him to cast abilities, then kill Grimoires carefully so you do not make him too resistant too quickly.");
        put(Mob.EVENT_INQUISITEUR_EWA, "Inquisiteur-EWA fights with a Berserker kit: Wounding Strike, Incendiary Curse, Ground Slam, Blood Lust, and Inferno. He opens with Golems, Shamans, and Swordsmen, then periodically spawns color Grimoires, Scripted Grimoires, and Necronomicon lasers. If too many extras are alive, he uses Killing Blow on all of them and stacks damage reduction. Keep the extra enemy count under control, interrupt Necronomicon targets, and deal damage during safe windows.");
        put(Mob.EVENT_INQUISITEUR_EGA, "Inquisiteur-EGA uses a Defender and Crusader mix: Righteous Strike, Freezing Breath, Ground Slam, Mystical Barrier, and Inspiring Presence. He has the same Grimoire and Necronomicon spawn pattern and Killing Blow rule as his siblings. If too many extras live, he gains stacked damage reduction. Control spawns, break barriers, and focus the boss when the floor is clear.");
        put(Mob.EVENT_INQUISITEUR_VPA, "Inquisiteur-VPA uses an Aquamancer kit: Impaling Strike, Water Breath, Vitality Liquor, Sanctified Beacon, and Healing Rain. Expect the same Grimoire and Necronomicon cadence, and the same Killing Blow purge that stacks damage reduction if extras overrun the room. Clear beacons and rain windows, call out Necronomicon lasers, and never let the extra enemy count spike into a free Killing Blow.");

        put(Mob.EVENT_BOLTARO_SHADOW, "It shoots fireballs, knocks players back, and can split into stronger copies on death.");
        put(Mob.EVENT_NARMER_ACOLYTE, "Killing it near Narmer can trigger Death Wish. It also periodically loses its current target.");
        put(Mob.EVENT_NARMER_DJER, "It uses Ground Shred. Below 75% health, it gains resistance and immunity to many knockbacks.");
        put(Mob.EVENT_NARMER_DJET, "It uses Flame Burst along with arena-wide silence and Cripple. Below 75% health, it gains damage resistance.");
        put(Mob.EVENT_MITHRA_FORSAKEN_FROST, "It periodically slows all players.");
        put(Mob.EVENT_MITHRA_FORSAKEN_FOLIAGE, "It fights with Earthliving Weapon active.");
        put(Mob.EVENT_MITHRA_FORSAKEN_SHRIEKER, "It blinds nearby players.");
        put(Mob.EVENT_MITHRA_FORSAKEN_RESPITE, "It applies Leech to all players.");
        put(Mob.EVENT_MITHRA_FORSAKEN_CRUOR, "It applies Wounding to all players.");
        put(Mob.EVENT_MITHRA_FORSAKEN_DEGRADER, "Its melee hits Cripple players.");
        put(Mob.EVENT_MITHRA_FORSAKEN_APPARITION, "On the first hit, it turns invisible and gains extra damage and resistance.");
        put(Mob.EVENT_MITHRA_POISONOUS_SPIDER, "It poisons all players.");
        put(Mob.EVENT_MITHRA_EGG_SAC, "This stationary egg hatches Poisonous Spiders and heals Mithra if it is left unbroken.");
        put(Mob.EVENT_TERAS_MINOTAUR, "It uses Ground Slam.");
        put(Mob.EVENT_TERAS_CYCLOPS, "Its melee hits launch enemies with heavy knockback.");
        put(Mob.EVENT_TERAS_SIREN, "It is immune to projectile damage.");
        put(Mob.EVENT_TERAS_DRYAD, "It periodically heals nearby allies.");
        put(Mob.EVENT_UNPUBLISHED_GRIMOIRE, "");
        put(Mob.EVENT_EMBELLISHED_GRIMOIRE, "");
        put(Mob.EVENT_SCRIPTED_GRIMOIRE, "While it is alive, it upgrades the abilities other Grimoires cast.");
        put(Mob.EVENT_ROUGE_GRIMOIRE, "It periodically casts random offensive player abilities.");
        put(Mob.EVENT_VIOLETTE_GRIMOIRE, "It periodically casts random mobility and support player abilities.");
        put(Mob.EVENT_BLEUE_GRIMOIRE, "It periodically casts random defensive and healing player abilities.");
        put(Mob.EVENT_ORANGE_GRIMOIRE, "It periodically casts random ultimate-style player abilities.");
        put(Mob.EVENT_NECRONOMICON_GRIMOIRE, "It locks onto a player with a laser and Smites them for near-lethal true damage.");

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

    public static List<String> getMechanicsParagraphs(Mob mob) {
        String mechanics = getMechanics(mob);
        if (mechanics == null || mechanics.isBlank()) {
            return List.of();
        }
        if (mechanics.indexOf('\n') >= 0) {
            List<String> paragraphs = new ArrayList<>();
            for (String paragraph : PARAGRAPH_BREAK.split(mechanics)) {
                String trimmed = paragraph.trim();
                if (!trimmed.isEmpty()) {
                    paragraphs.add(trimmed);
                }
            }
            return paragraphs;
        }
        String[] sentences = SENTENCE_BREAK.split(mechanics.trim());
        if (sentences.length <= 2) {
            return List.of(mechanics.trim());
        }
        List<String> paragraphs = new ArrayList<>();
        int index = 0;
        while (index < sentences.length) {
            int remaining = sentences.length - index;
            int take = remaining == 3 ? 2 : Math.min(2, remaining);
            paragraphs.add(String.join(" ", List.of(sentences).subList(index, index + take)).trim());
            index += take;
        }
        return paragraphs;
    }

    public static String getDisplayName(Mob mob) {
        if (mob.name != null && !mob.name.isEmpty()) {
            return mob.name;
        }
        return mob.name();
    }

    public static long getKills(DatabasePlayer databasePlayer, Mob mob) {
        long kills = databasePlayer.getPveStats().getMobKillCount(mob.name());
        String displayName = mob.name;
        if (displayName == null || displayName.isEmpty() || displayName.equals(mob.name()) || ambiguousDisplayNames().contains(displayName)) {
            return kills;
        }
        return kills + databasePlayer.getPveStats().getMobKillCount(displayName);
    }

    private static volatile Set<String> ambiguousDisplayNames;

    private static Set<String> ambiguousDisplayNames() {
        Set<String> cached = ambiguousDisplayNames;
        if (cached != null) {
            return cached;
        }
        Map<String, Integer> counts = new HashMap<>();
        for (Mob value : Mob.VALUES) {
            if (value.name == null || value.name.isEmpty()) {
                continue;
            }
            counts.merge(value.name, 1, Integer::sum);
        }
        if (counts.isEmpty()) {
            return Set.of();
        }
        Set<String> ambiguous = new HashSet<>();
        counts.forEach((name, count) -> {
            if (count > 1) {
                ambiguous.add(name);
            }
        });
        cached = Set.copyOf(ambiguous);
        ambiguousDisplayNames = cached;
        return cached;
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
