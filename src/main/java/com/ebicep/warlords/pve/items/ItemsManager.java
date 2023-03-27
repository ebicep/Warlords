package com.ebicep.warlords.pve.items;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

public class ItemsManager {

    /**
     * 6(x1^1/3) + ((x2 + 1) / 100) * 25 + x3 + (x4 / x4Total) * 25 + x5* + x6
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
     *
     * @param databasePlayer The player to get the weight of
     * @param selectedSpec   The spec that the player is currently using
     * @return The weight of the player
     */
    public static int getMaxWeight(DatabasePlayer databasePlayer, Specializations selectedSpec) {
        int weight = 0;
        // x1
        weight += Math.pow(databasePlayer.getPveStats().getWins(), 1.0 / 3.0) * 6;
        // x2
        int totalPlayerClassLevel = Arrays.stream(databasePlayer.getClasses())
                                          .mapToInt(AbstractDatabaseStatInformation::getLevel)
                                          .sum();
        weight += ((totalPlayerClassLevel + 1) / 5) / 4;
        // x3
        weight += getPrestigeWeight(databasePlayer, selectedSpec);
        // x4
        // TODO
        // x5
        // TODO
        // x6
        if (databasePlayer.getPveStats().isCurrentlyPatreon()) {
            weight += 5;
        }
        return Math.min(weight, 100);
    }

    private static int getPrestigeWeight(DatabasePlayer databasePlayer, Specializations selectedSpec) {
        int weight = 0;
        int prestige = databasePlayer.getSpec(selectedSpec).getPrestige();
        if (prestige >= 1) {
            weight += 5 + prestige - 1;
        }
        int lowestClassPrestige = Specializations.getClass(selectedSpec).subclasses
                .stream()
                .map(spec -> databasePlayer.getSpec(spec).getPrestige())
                .min(Integer::compare)
                .orElse(-1);
        if (lowestClassPrestige != -1) {
            weight += lowestClassPrestige * 5;
        }
        int lowestSpecTypePrestige = Arrays.stream(Specializations.VALUES)
                                           .filter(spec -> spec.specType == selectedSpec.specType)
                                           .map(spec -> databasePlayer.getSpec(spec).getPrestige())
                                           .min(Integer::compare)
                                           .orElse(-1);
        if (lowestSpecTypePrestige != -1) {
            weight += lowestSpecTypePrestige * 5;
        }
        return Math.min(25, weight);
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