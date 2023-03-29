package com.ebicep.warlords.pve.items;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.achievements.types.TieredAchievements;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.general.classes.DatabaseBaseGeneral;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

public class ItemsManager {

    private static final int[] TIER_ACHIEVEMENT_WEIGHTS = {5, 5, 5, 5, 5};
    private static final List<String> HI_SCORE_LEADERBOARDS = List.of(
            "Fastest Win",
            "Highest Wave Cleared",
            "Experience",
            "Clear Rate",
            "Wins"
    );

    /**
     * 5ln(x1 + 1) + ceil(50 - 50(1.55^-(x2mean / x2highest - 1) - 1)) + x3 + (x4 / x4Total) * 25 + x5* + x6
     * <p>
     * if equation > 100, set to 100. (100 Weight Cap)
     * <p>
     * Almost all of these have decimals. In such cases, Round Down
     * <p>
     * x1: Total Player Wins. Currently, Rich would have +47 weight from this.
     * <p>
     * x2: Average Player Level. Find the mean of the set of classes we have (pal, mag, war, sha, rog).
     * <p>
     * x3: Prestiges calculation
     * <p>
     * x4: Achievements. Divide Achievements Earned by Total Achievements.
     * <p>
     * x5: "Hi-Scores". This one's complicated. Will explain thoroughly after this message.
     * <p>
     * x6: Patreon Bonus. Either +5 or +10.
     * <p>
     *
     * @param databasePlayer The player to get the weight of
     * @param selectedSpec   The spec that the player is currently using
     * @return The weight of the player
     */
    public static int getMaxWeight(DatabasePlayer databasePlayer, Specializations selectedSpec) {
        int weight = 0;
        System.out.println("Weight: " + weight);
        // x1
        weight += 5 * Math.log(databasePlayer.getPveStats().getWins() + 1);
        System.out.println("Weight after x1: " + weight);
        // x2
        int totalPlayerClassLevel = 0;
        int highestPlayerClassLevel = 0;
        for (DatabaseBaseGeneral databaseBaseGeneral : databasePlayer.getClasses()) {
            int level = databaseBaseGeneral.getLevel();
            totalPlayerClassLevel += level;
            highestPlayerClassLevel = Math.max(highestPlayerClassLevel, level);
        }
        weight += Math.ceil(50 - 50 * (Math.pow(1.55, -((double) totalPlayerClassLevel / 5 / highestPlayerClassLevel - 1)) - 1));
        System.out.println("Weight after x2: " + weight);
        // x3
        weight += getPrestigeWeight(databasePlayer, selectedSpec);
        System.out.println("Weight after x3: " + weight);
        // x4
        weight += getAchievementsWeight(databasePlayer);
        System.out.println("Weight after x4: " + weight);
        // x5
        weight += getHiScoreWeight(databasePlayer);
        System.out.println("Weight after x5: " + weight);
        // x6
        if (databasePlayer.getPveStats().isCurrentlyPatreon()) {
            weight += 5;
        }
        System.out.println("Weight after x6: " + weight);
        return Math.min(weight, 100);
    }

    /**
     * 1. For every prestige you own as a player, +1 weight on exclusively that spec. The exception to this rule is +5 for obtaining the first prestige on a spec, or (P1).
     * <p>
     * 2. If an entire class has a prestige, +5 weight for every collective prestige, I.e. If I have 1  prestige on all Mage Specs, I get +10 weight on any mage spec. If I have a (P2) Cryo and Pyro and Aqua are (P1), weight on Cryo is +11, while weight on Pyro and Aqua are +10.
     * <p>
     * 3. If an all spec types have the same prestige, +5 weight for every collective prestige, I.e. If I have 1  prestige on all damage specs, I get +5 weight from the Prestige and +5 weight from all damage specs having any prestige.
     * <p>
     * 4. Caps at +25 Prestige Weight.
     * <p>
     *
     * @param databasePlayer The player to get the weight of
     * @param selectedSpec   The spec that the player is currently using
     * @return Prestige weight of the player
     */
    private static int getPrestigeWeight(DatabasePlayer databasePlayer, Specializations selectedSpec) {
        int weight = 0;
        // 1.
        int prestige = databasePlayer.getSpec(selectedSpec).getPrestige();
        if (prestige >= 1) {
            weight += 5 + prestige - 1;
        }
        // 2.
        for (Classes classes : Classes.VALUES) {
            int lowestClassPrestige = classes.subclasses.stream()
                                                        .map(spec -> databasePlayer.getSpec(spec).getPrestige())
                                                        .min(Integer::compare)
                                                        .orElse(-1);
            if (lowestClassPrestige != -1) {
                weight += (lowestClassPrestige * 5 * (classes.subclasses.contains(selectedSpec) ? 1 : .5));
            }
        }
        // 3.
        for (SpecType specType : SpecType.VALUES) {
            int lowestSpecTypePrestige = Arrays.stream(Specializations.VALUES)
                                               .filter(spec -> spec.specType == specType)
                                               .map(spec -> databasePlayer.getSpec(spec).getPrestige())
                                               .min(Integer::compare)
                                               .orElse(-1);
            if (lowestSpecTypePrestige != -1) {
                weight += (lowestSpecTypePrestige * 5 * (specType == selectedSpec.specType ? 1 : .5));
            }
        }
        // 4.
        return Math.min(25, weight);
    }

    /**
     * <h2>CHALLENGE ACHIEVEMENTS</h2>
     * <p>
     * So there are 1 star, 2 star, and 3 star challenges (for now.) (Difficulty levels)
     * <p>
     * All Challenges have a maximum of +5 weight for each tier.<p>
     * 1 Star: Every time you hit x^4 Challenges completed, you gain +1 weight.<p>
     * 2 Star: Every time you hit x^(7/2) (rounded up) Challenges completed, you gain +1 weight.<p>
     * 3 Star: Every time you hit x^3 Challenges completed, you gain +1 weight.<p>
     * <p>
     * To reiterate, 1 Star Challenges have +0 weight at 0 challenges completed, +1 weight at 1 challenges completed, +2 weight at 16 challenges completed, +3 weight at 81 challenges completed, +4 weight at 256 challenges completed, and +5 weight at 625 challenges completed,
     * <p>
     * Similarly, 3 Star Challenges have +0 weight at 0 challenges completed, +1 weight at 1 challenges completed, +2 weight at 8 challenges completed, +3 weight at 27 challenges completed, +4 weight at 64 challenges completed, and +5 weight at 125 challenges completed.
     * <p>
     * This is just an idea I am running with, no actual investigation of good values to find to properly scale the difference between 1 Star and 3 Star has been investigated with.<p>
     * 2 Star Challenges have +0 weight at 0 challenges completed, +1 weight at 1 challenges completed, +2 weight at 12 challenges completed, +3 weight at 47 challenges completed, +4 weight at 128 challenges completed, and +5 weight at 280 challenges completed.<p>
     *
     * <h2>TIERED ACHIEVEMENTS</h2>
     * <p>
     * % weight of tier  = # of ach unlocked in that tier / total # of ach of that tier<p>
     * 5-5-10-10-20 is 100% of each tier<p>
     *
     * @param databasePlayer The player to get the weight of
     * @return Achievement weight of the player
     */
    private static int getAchievementsWeight(DatabasePlayer databasePlayer) {
        int weight = 0;

        for (ChallengeAchievements challengeAchievement : ChallengeAchievements.VALUES) {
            weight += challengeAchievement.getDifficulty().weightFunction.apply(
                    (int) databasePlayer.getAchievements()
                                        .stream()
                                        .filter(achievementRecord -> achievementRecord.getAchievement() == challengeAchievement)
                                        .count()
            );
        }

        HashMap<Integer, Integer> numberOfTieredAchievements = new HashMap<>();
        HashMap<Integer, Integer> unlockedTieredAchievements = new HashMap<>();
        TieredAchievements.TIERED_ACHIEVEMENTS_GROUPS.values().forEach(tieredAchievements -> {
            for (TieredAchievements[] tieredAchievement : tieredAchievements) {
                for (int i = 0; i < tieredAchievement.length; i++) {
                    numberOfTieredAchievements.merge(i, 1, Integer::sum);
                    if (databasePlayer.hasAchievement(tieredAchievement[i])) {
                        unlockedTieredAchievements.merge(i, 1, Integer::sum);
                    }
                }

            }
        });
        for (Map.Entry<Integer, Integer> entry : numberOfTieredAchievements.entrySet()) {
            Integer tier = entry.getKey();
            Integer amount = entry.getValue();
            if (tier < 0 || tier >= TIER_ACHIEVEMENT_WEIGHTS.length) {
                ChatChannels.sendDebugMessage((CommandIssuer) null, "Invalid tier for tiered achievement weight: " + tier, true);
                continue;
            }
            double weightOfTier = unlockedTieredAchievements.getOrDefault(tier, 0) / (double) amount;
            weight += (int) (weightOfTier * TIER_ACHIEVEMENT_WEIGHTS[tier]);
        }

        return weight;
    }

    /**
     * On the following leaderboards, players in 1st get +5 Weight, 2nd get +3 weight, and 3rd gets +1 weight:
     * <p>
     * 1. Fastest Win<p>
     * 2. Highest Wave Cleared<p>
     * 3. Lifetime EXP<p>
     * 4. Clear Rate<p>
     * 5. Top Wins<p>
     * <p>
     * Now, some of these could get super inflated, but at the point you're competing at thousands of wins (hypothetically) you probably will have a high enough weight that it doesn't matter if you get a leaderboard bonus. Essentially, it's more bragging than anything.
     * <p>
     * I'm sure there are some other (likely better) leaderboards that you could rank 1st thru 3rd for +weight bonuses, so if any stat comes to mind you'd like, the more the merrier with this specific value in the calculation.
     * <p>
     * Also, this category stacks, up to +9 weight (1 of each position). However, you do not need a 1st, 2nd, and 3rd position; you are fine with 2 LB's in 1st, or 3 LB's in 2nd.
     * <p>
     *
     * @param databasePlayer The player to get the weight of
     * @return Leaderboard weight of the player
     */
    private static int getHiScoreWeight(DatabasePlayer databasePlayer) {
        int weight = 0;
        List<StatsLeaderboard> statsLeaderboards = StatsLeaderboardManager.STATS_LEADERBOARDS
                .get(StatsLeaderboardManager.GameType.PVE)
                .getCategories()
                .get(0)
                .getStatsLeaderboards();
        for (StatsLeaderboard statsLeaderboard : statsLeaderboards) {
            if (!HI_SCORE_LEADERBOARDS.contains(statsLeaderboard.getTitle())) {
                continue;
            }
            int position = statsLeaderboard.getSortedPlayers(PlayersCollections.WEEKLY).indexOf(databasePlayer);
            switch (position) {
                case 0:
                    weight += 5;
                    break;
                case 1:
                    weight += 3;
                    break;
                case 2:
                    weight += 1;
                    break;
            }
        }

        return Math.min(weight, 9);
    }

    @Field("item_inventory")
    private List<AbstractItem<?, ?, ?>> itemInventory = new ArrayList<>();
    private List<ItemLoadout> loadouts = new ArrayList<>() {{
        add(new ItemLoadout("Default"));
    }};
    @Field("blessings_found")
    private int blessingsFound;
    @Field("blessings_bought")
    private Map<Integer, Integer> blessingsBought = new HashMap<>();

    public ItemsManager() {
    }

    public List<AbstractItem<?, ?, ?>> getItemInventory() {
        return itemInventory;
    }

    public void addItem(AbstractItem<?, ?, ?> item) {
        this.itemInventory.add(item);
    }

    public void removeItem(AbstractItem<?, ?, ?> item) {
        this.itemInventory.remove(item);
    }

    public List<ItemLoadout> getLoadouts() {
        return loadouts;
    }

    public int getBlessingsFound() {
        return blessingsFound;
    }

    public void addBlessingsFound(int amount) {
        this.blessingsFound += amount;
    }

    public void subtractBlessingsFound(int amount) {
        this.blessingsFound -= amount;
    }

    public Map<Integer, Integer> getBlessingsBought() {
        return blessingsBought;
    }

    public Integer getBlessingBoughtAmount(int tier) {
        return blessingsBought.getOrDefault(tier, 0);
    }

    public void addBlessingBought(int tier) {
        blessingsBought.merge(tier, 1, Integer::sum);
    }

    public void subtractBlessingBought(int tier) {
        blessingsBought.merge(tier, -1, Integer::sum);
    }

}