package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.pve.mobs.Mob;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;

public enum Anomalies {

    OPEX_ANOMALY(
            "Opex Anomaly",
            List.of(
                    Component.text("Stabilize the fractured Opex conduits."),
                    Component.text("Defend each relic for 2 minutes.")
            ),
            List.of(
                    new AnomalyRewardPool("Opex Cache I", 4_000, 50, 1, 0.10),
                    new AnomalyRewardPool("Opex Cache II", 5_000, 100, 1, 0.20),
                    new AnomalyRewardPool("Opex Cache III", 6_000, 150, 2, 0.30)
            ),
            new AnomalyMobSet()
                    //basic
                    .add(0.4, Mob.ZOMBIE_LANCER)
                    .add(0.2, Mob.SLIMY_ANOMALY)
                    .add(0.2, Mob.ARACHNO_VENARI)
                    //elite
                    .add(0.5, Mob.ZOMBIE_SWORDSMAN)
                    .add(0.1, Mob.SKELETAL_WARLOCK)
                    .add(0.2, Mob.PIG_SHAMAN)
                    .add(0.02, Mob.ILLUMINATION)
                    .add(0.15, Mob.GOLEM_APPRENTICE)
                    .add(0.06, Mob.WITCH_DEACON)
                    .add(0.02, Mob.ADVANCED_WARRIOR_BERSERKER)
                    //envoy
                    .add(0.05, Mob.ZOMBIE_VANGUARD)
                    .add(0.05, Mob.SKELETAL_ENTROPY)
                    .add(0.01, Mob.PIG_ALLEVIATOR)
                    //elite
                    .add(0.04, Mob.VOID_ZOMBIE)
                    .add(0.04, Mob.SKELETAL_MESMER)
                    .add(0.04, Mob.FIRE_SPLITTER)
                    .add(0.02, Mob.RIFT_WALKER)
                    // champion
                    .add(0.01, Mob.LANTERN_DREDGER)
                    .add(0.01, Mob.BARNACLE_BRUTE)
                    .add(0.01, Mob.ABYSS_WATCHER)
    ),
    BRIDGE_OF_DUNESTAR(
            "Bridge of Dunestar",
            List.of(
                    Component.text("Pick up the relic to choose its carrier."),
                    Component.text("The carrier cannot attack or use abilities."),
                    Component.text("Reach each destination within 2 minutes.")
            ),
            List.of(
                    new AnomalyRewardPool("Dunestar Cache I", 3_000, 50, 1, 0.10),
                    new AnomalyRewardPool("Dunestar Cache II", 5_000, 100, 1, 0.20),
                    new AnomalyRewardPool("Dunestar Cache III", 7_000, 200, 2, 0.30)
            ),
            new AnomalyMobSet()
                    //basic
                    .add(0.4, Mob.ZOMBIE_LANCER)
                    .add(0.2, Mob.SLIMY_ANOMALY)
                    .add(0.2, Mob.ARACHNO_VENARI)
                    //elite
                    .add(0.5, Mob.ZOMBIE_SWORDSMAN)
                    .add(0.1, Mob.SKELETAL_WARLOCK)
                    .add(0.2, Mob.PIG_SHAMAN)
                    .add(0.02, Mob.ILLUMINATION)
                    .add(0.15, Mob.GOLEM_APPRENTICE)
                    .add(0.06, Mob.WITCH_DEACON)
                    .add(0.02, Mob.ADVANCED_WARRIOR_BERSERKER)
                    //envoy
                    .add(0.05, Mob.ZOMBIE_VANGUARD)
                    .add(0.05, Mob.SKELETAL_ENTROPY)
                    .add(0.01, Mob.PIG_ALLEVIATOR)
                    //elite
                    .add(0.04, Mob.VOID_ZOMBIE)
                    .add(0.04, Mob.SKELETAL_MESMER)
                    .add(0.04, Mob.FIRE_SPLITTER)
                    .add(0.02, Mob.RIFT_WALKER)
                    // champion
                    .add(0.01, Mob.LANTERN_DREDGER)
                    .add(0.01, Mob.BARNACLE_BRUTE)
                    .add(0.01, Mob.ABYSS_WATCHER),
            new AnomalyMobSet()
                    //basic
                    .add(0.4, Mob.ZOMBIE_LANCER)
                    .add(0.2, Mob.SLIMY_ANOMALY)
                    .add(0.2, Mob.ARACHNO_VENARI)
                    //elite
                    .add(0.5, Mob.ZOMBIE_SWORDSMAN)
                    .add(0.1, Mob.SKELETAL_WARLOCK)
                    .add(0.2, Mob.PIG_SHAMAN)
                    .add(0.02, Mob.ILLUMINATION)
                    .add(0.15, Mob.GOLEM_APPRENTICE)
                    .add(0.06, Mob.WITCH_DEACON)
                    .add(0.02, Mob.ADVANCED_WARRIOR_BERSERKER)
                    //envoy
                    .add(0.05, Mob.ZOMBIE_VANGUARD)
                    .add(0.05, Mob.SKELETAL_ENTROPY)
                    .add(0.01, Mob.PIG_ALLEVIATOR)
                    //elite
                    .add(0.04, Mob.VOID_ZOMBIE)
                    .add(0.04, Mob.SKELETAL_MESMER)
                    .add(0.04, Mob.FIRE_SPLITTER)
                    .add(0.02, Mob.RIFT_WALKER)
                    // champion
                    .add(0.01, Mob.LANTERN_DREDGER)
                    .add(0.01, Mob.BARNACLE_BRUTE)
                    .add(0.01, Mob.ABYSS_WATCHER),
            new AnomalyMobSet()
                    //basic
                    .add(0.4, Mob.ZOMBIE_LANCER)
                    .add(0.2, Mob.SLIMY_ANOMALY)
                    .add(0.2, Mob.ARACHNO_VENARI)
                    //elite
                    .add(0.5, Mob.ZOMBIE_SWORDSMAN)
                    .add(0.1, Mob.SKELETAL_WARLOCK)
                    .add(0.2, Mob.PIG_SHAMAN)
                    .add(0.02, Mob.ILLUMINATION)
                    .add(0.15, Mob.GOLEM_APPRENTICE)
                    .add(0.06, Mob.WITCH_DEACON)
                    .add(0.02, Mob.ADVANCED_WARRIOR_BERSERKER)
                    //envoy
                    .add(0.05, Mob.ZOMBIE_VANGUARD)
                    .add(0.05, Mob.SKELETAL_ENTROPY)
                    .add(0.01, Mob.PIG_ALLEVIATOR)
                    //elite
                    .add(0.04, Mob.VOID_ZOMBIE)
                    .add(0.04, Mob.SKELETAL_MESMER)
                    .add(0.04, Mob.FIRE_SPLITTER)
                    .add(0.02, Mob.RIFT_WALKER)
                    // champion
                    .add(0.01, Mob.LANTERN_DREDGER)
                    .add(0.01, Mob.BARNACLE_BRUTE)
                    .add(0.01, Mob.ABYSS_WATCHER)
    ),
    WHAT_ONCE_WAS(
            "What Once Was",
            List.of(
                    Component.text("Decipher the rune sequences of a lost civilization."),
                    Component.text("Activate each vault's pedestals in the correct order."),
                    Component.text("Wrong inputs summon additional defenders.")
            ),
            List.of(
                    new AnomalyRewardPool("Remnant Cache I", 2_000, 100, 1, 0.10),
                    new AnomalyRewardPool("Remnant Cache II", 4_000, 150, 1, 0.20),
                    new AnomalyRewardPool("Remnant Cache III", 6_000, 200, 2, 0.30)
            ),
            new AnomalyMobSet()
                    //basic
                    .add(0.4, Mob.ZOMBIE_LANCER)
                    .add(0.2, Mob.SLIMY_ANOMALY)
                    .add(0.2, Mob.ARACHNO_VENARI)
                    //elite
                    .add(0.5, Mob.ZOMBIE_SWORDSMAN)
                    .add(0.1, Mob.SKELETAL_WARLOCK)
                    .add(0.2, Mob.PIG_SHAMAN)
                    .add(0.15, Mob.GOLEM_APPRENTICE)
                    .add(0.06, Mob.WITCH_DEACON)
                    .add(0.02, Mob.ADVANCED_WARRIOR_BERSERKER)
                    //envoy
                    .add(0.05, Mob.ZOMBIE_VANGUARD)
                    .add(0.05, Mob.SKELETAL_ENTROPY)
                    .add(0.01, Mob.PIG_ALLEVIATOR)
    );

    public static final Anomalies[] VALUES = values();
    public static final Anomalies[] ROTATING = values();

    private final String name;
    private final List<Component> description;
    private final List<AnomalyRewardPool> rewardPools;
    private final List<AnomalyMobSet> mobSets;

    Anomalies(String name, List<Component> description, List<AnomalyRewardPool> rewardPools, AnomalyMobSet... mobSets) {
        this.name = name;
        this.description = description;
        this.rewardPools = rewardPools;
        this.mobSets = List.copyOf(Arrays.asList(mobSets));
    }

    public GameMap getMap() {
        return switch (this) {
            case OPEX_ANOMALY -> GameMap.OPEX_ANOMALY;
            case BRIDGE_OF_DUNESTAR -> GameMap.PLAINS_OF_DUNESTAR;
            case WHAT_ONCE_WAS -> GameMap.WHAT_ONCE_WAS;
        };
    }

    public String getCacheObjective(int cacheIndex) {
        return switch (this) {
            case OPEX_ANOMALY -> "Defend Relic " + (cacheIndex + 1);
            case BRIDGE_OF_DUNESTAR -> cacheIndex < 2
                    ? "Reach Checkpoint " + (cacheIndex + 1)
                    : "Deliver the relic to the sanctuary";
            case WHAT_ONCE_WAS -> "Unlock Vault " + (cacheIndex + 1);
        };
    }

    public AnomalyMobSet getMobSet(int index) {
        return mobSets.get(Math.min(Math.max(index, 0), mobSets.size() - 1));
    }

    public String getName() {
        return name;
    }

    public List<Component> getDescription() {
        return description;
    }

    public List<AnomalyRewardPool> getRewardPools() {
        return rewardPools;
    }

    public Mob[] getSpawnableMobs() {
        return mobSets.stream()
                .flatMap(mobSet -> mobSet.getMobs().stream())
                .distinct()
                .toArray(Mob[]::new);
    }
}
