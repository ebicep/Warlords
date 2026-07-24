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
                    new AnomalyRewardPool("Opex Cache I", 6_000, 150, 1),
                    new AnomalyRewardPool("Opex Cache II", 12_000, 200, 1),
                    new AnomalyRewardPool("Opex Cache III", 18_000, 250, 2)
            ),
            new AnomalyMobSet()
                    .add(Mob.ZOMBIE_LANCER)
                    .add(Mob.SKELETAL_MAGE)
                    .add(Mob.SLIMY_ANOMALY)
                    .add(Mob.HOUND)
    ),
    BRIDGE_OF_DUNESTAR(
            "Bridge of Dunestar",
            List.of(
                    Component.text("Pick up the relic to choose its carrier."),
                    Component.text("The carrier cannot attack or use abilities."),
                    Component.text("Reach each destination within 2 minutes.")
            ),
            List.of(
                    new AnomalyRewardPool("Dunestar Cache I", 10_000, 150, 1),
                    new AnomalyRewardPool("Dunestar Cache II", 20_000, 350, 1),
                    new AnomalyRewardPool("Dunestar Cache III", 30_000, 500, 2)
            ),
            new AnomalyMobSet()
                    .add(Mob.PIG_DISCIPLE)
                    .add(Mob.ARACHNO_VENARI),
            new AnomalyMobSet()
                    .add(Mob.PIG_DISCIPLE)
                    .add(Mob.ARACHNO_VENARI)
                    .add(Mob.INTERMEDIATE_WARRIOR_BERSERKER),
            new AnomalyMobSet()
                    .add(Mob.PIG_DISCIPLE)
                    .add(Mob.ARACHNO_VENARI)
                    .add(Mob.INTERMEDIATE_WARRIOR_BERSERKER)
                    .add(Mob.BLAZING_KINDLE)
    ),
    WHAT_ONCE_WAS(
            "What Once Was",
            List.of(
                    Component.text("Decipher the rune sequences of a lost civilization."),
                    Component.text("Activate each vault's pedestals in the correct order."),
                    Component.text("Wrong inputs summon additional defenders.")
            ),
            List.of(
                    new AnomalyRewardPool("Remnant Cache I", 8_000, 225, 1),
                    new AnomalyRewardPool("Remnant Cache II", 15_000, 350, 1),
                    new AnomalyRewardPool("Remnant Cache III", 25_000, 450, 2)
            ),
            new AnomalyMobSet()
                    .add(Mob.STRAY)
                    .add(Mob.FALLEN_STRAY)
                    .add(Mob.LURKING_SLIME)
                    .add(Mob.SPECTRAL_THIEF)
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
