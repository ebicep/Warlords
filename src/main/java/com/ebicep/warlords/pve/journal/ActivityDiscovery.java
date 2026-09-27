package com.ebicep.warlords.pve.journal;

import com.ebicep.warlords.game.GameMode;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class ActivityDiscovery {

    public enum Category {
        PVP("Player vs Player", NamedTextColor.AQUA),
        PVE("Player vs Environment", NamedTextColor.GOLD),
        OTHER("Other", NamedTextColor.GRAY),
        ;

        public final String displayName;
        public final TextColor textColor;

        Category(String displayName, TextColor textColor) {
            this.displayName = displayName;
            this.textColor = textColor;
        }
    }

    public record Entry(GameMode gameMode, Category category, Material icon, String summary) {
    }

    private static final List<Entry> ENTRIES;

    static {
        List<Entry> entries = new ArrayList<>();
        for (GameMode gameMode : GameMode.VALUES) {
            if (!include(gameMode)) {
                continue;
            }
            entries.add(new Entry(gameMode, categoryFor(gameMode), iconFor(gameMode), summaryFor(gameMode)));
        }
        entries.sort(Comparator.comparingInt(entry -> entry.category().ordinal()));
        ENTRIES = Collections.unmodifiableList(entries);
    }

    private ActivityDiscovery() {
    }

    public static List<Entry> entries() {
        return ENTRIES;
    }

    private static boolean include(GameMode gameMode) {
        return switch (gameMode) {
            case CAPTURE_THE_FLAG, INTERCEPTION, TEAM_DEATHMATCH, DUEL, SIEGE,
                 WAVE_DEFENSE, ONSLAUGHT, TREASURE_HUNT, ANOMALY, RAID,
                 EVENT_WAVE_DEFENSE, TOWER_DEFENSE, EFFIGY_TRIALS, TUTORIAL -> true;
            case LOBBY, DEBUG, PVE_DEBUG, WHACK_A_MOLE -> false;
        };
    }

    private static Category categoryFor(GameMode gameMode) {
        return switch (gameMode) {
            case CAPTURE_THE_FLAG, INTERCEPTION, TEAM_DEATHMATCH, DUEL, SIEGE -> Category.PVP;
            case WAVE_DEFENSE, ONSLAUGHT, TREASURE_HUNT, ANOMALY, RAID,
                 EVENT_WAVE_DEFENSE, TOWER_DEFENSE, EFFIGY_TRIALS -> Category.PVE;
            case TUTORIAL -> Category.OTHER;
            case LOBBY, DEBUG, PVE_DEBUG, WHACK_A_MOLE -> throw unexpected(gameMode);
        };
    }

    private static Material iconFor(GameMode gameMode) {
        return switch (gameMode) {
            case CAPTURE_THE_FLAG -> Material.BLACK_BANNER;
            case INTERCEPTION -> Material.BEACON;
            case TEAM_DEATHMATCH -> Material.DIAMOND_HORSE_ARMOR;
            case DUEL -> Material.DIAMOND_SWORD;
            case SIEGE -> Material.SCULK;
            case WAVE_DEFENSE -> Material.ZOMBIE_HEAD;
            case ONSLAUGHT -> Material.BLAZE_POWDER;
            case TREASURE_HUNT -> Material.CRYING_OBSIDIAN;
            case ANOMALY -> Material.RESPAWN_ANCHOR;
            case RAID -> Material.END_CRYSTAL;
            case EVENT_WAVE_DEFENSE -> Material.FIREWORK_ROCKET;
            case TOWER_DEFENSE -> Material.OAK_PLANKS;
            case EFFIGY_TRIALS -> Material.TRIAL_SPAWNER;
            case TUTORIAL -> Material.BOOK;
            case LOBBY, DEBUG, PVE_DEBUG, WHACK_A_MOLE -> throw unexpected(gameMode);
        };
    }

    private static String summaryFor(GameMode gameMode) {
        return switch (gameMode) {
            case CAPTURE_THE_FLAG ->
                    "Steal and capture the enemy flag to score 250 points. The first team to 1000 points wins.";
            case INTERCEPTION ->
                    "Capture and hold marked points to earn score over time. The first team to 1500 points wins, with overtime if the match is close.";
            case TEAM_DEATHMATCH ->
                    "Eliminate players on the enemy team to score. The first team to 1000 points wins.";
            case DUEL ->
                    "A 1v1 fight. The first player to kill their opponent 5 times wins the duel.";
            case SIEGE ->
                    "Score by capturing a point, escorting a payload, or defending it. The first team to 4 points wins.";
            case WAVE_DEFENSE ->
                    "Survive waves of monsters across Easy through Endless difficulties. Clear the set of waves, or last as long as you can in Endless.";
            case ONSLAUGHT ->
                    "Endless survival. Kill as many monsters as you can; every 5 minutes you earn special reward pouches.";
            case TREASURE_HUNT ->
                    "Explore hidden hallways of Cryptic Conquest for treasure. Bring only one specialization per player, or void instability slashes your healing and damage by 90%.";
            case ANOMALY ->
                    "Investigate a rotating hourly anomaly with 2–6 players. Complete its objective for unique caches and a featured Legendary set.";
            case RAID ->
                    "High-end PvE trials such as Regnum of Two Crowns. Coordinate through rooms and bosses where every mistake is costly.";
            case EVENT_WAVE_DEFENSE ->
                    "Limited-time event wave defense with unique maps, bosses, and event rewards while the current event is live.";
            case TOWER_DEFENSE ->
                    "Build and defend your castle against waves of enemies. The last castle standing wins.";
            case EFFIGY_TRIALS ->
                    "Defeat the Effigies, charge through their trials, and take down the final boss.";
            case TUTORIAL ->
                    "A guided introduction to Warlords combat, abilities, and the basics of the game.";
            case LOBBY, DEBUG, PVE_DEBUG, WHACK_A_MOLE -> throw unexpected(gameMode);
        };
    }

    private static IllegalStateException unexpected(GameMode gameMode) {
        return new IllegalStateException("Uncatalogued activity: " + gameMode);
    }
}
