package com.ebicep.warlords.database.repositories.masterworksfair.pojos;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.general.FutureMessage;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairEntry;
import com.ebicep.warlords.pve.rewards.types.MasterworksFairReward;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.pve.weapons.weaponaddons.WeaponScore;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.*;

@Document(collection = "Masterworks_Fair")
public class MasterworksFair {

    @Id
    protected String id;

    @Field("start_date")
    private Instant startDate = Instant.now();
    @Field("common_entries")
    private List<MasterworksFairPlayerEntry> commonPlayerEntries = new ArrayList<>();
    @Field("rare_entries")
    private List<MasterworksFairPlayerEntry> rarePlayerEntries = new ArrayList<>();
    @Field("epic_entries")
    private List<MasterworksFairPlayerEntry> epicPlayerEntries = new ArrayList<>();
    @Field("item_entries")
    private List<MasterworksFairPlayerEntry> itemPlayerEntries = new ArrayList<>();
    @Field("ended")
    private boolean ended = false;
    @Field("fair_number")
    private int fairNumber;

    public MasterworksFair() {
    }

    @Override
    public String toString() {
        return "MasterworksFair{startDate=" + startDate +
                ", commonPlayerEntries=" + commonPlayerEntries.size() +
                ", rarePlayerEntries=" + rarePlayerEntries.size() +
                ", epicPlayerEntries=" + epicPlayerEntries.size() +
                ", itemPlayerEntries=" + getItemPlayerEntries().size() + '}';
    }

    public void sendRewards(boolean throughRewardsInventory) {
        Instant now = Instant.now();
        HashMap<UUID, List<MasterworksFairEntry>> playerFairResults = createResults(now);
        Warlords.newChain()
                .async(() -> playerFairResults.forEach((uuid, masterworksFairEntries) ->
                        Warlords.newChain()
                                .asyncFirst(() -> DatabaseManager.playerService.findByUUID(uuid))
                                .syncLast(optionalDatabasePlayer -> {
                                    if (optionalDatabasePlayer.isEmpty()) {
                                        return;
                                    }
                                    DatabasePlayer databasePlayer = optionalDatabasePlayer.get();
                                    DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                                    if (pveStats == null) {
                                        return;
                                    }
                                    for (MasterworksFairEntry masterworksFairEntry : masterworksFairEntries) {
                                        pveStats.addMasterworksFairEntry(masterworksFairEntry);
                                        LinkedHashMap<Spendable, Long> rewards = getRewards(masterworksFairEntry);
                                        if (throughRewardsInventory) {
                                            pveStats.addReward(new MasterworksFairReward(
                                                    rewards,
                                                    now,
                                                    masterworksFairEntry.isItemSubmission() ? "Item" : masterworksFairEntry.getRarity().name
                                            ));
                                        } else {
                                            rewards.forEach((spendable, amount) -> spendable.addToPlayer(databasePlayer, amount));
                                        }
                                    }
                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                })
                                .execute()))
                .execute();
        sendResults(playerFairResults, false);
        if (throughRewardsInventory) {
            ChatUtils.MessageType.MASTERWORKS_FAIR.sendMessage("Awarded entries through reward inventory");
        } else {
            ChatUtils.MessageType.MASTERWORKS_FAIR.sendMessage("Awarded entries directly");
        }
    }

    private HashMap<UUID, List<MasterworksFairEntry>> createResults(Instant time) {
        HashMap<UUID, List<MasterworksFairEntry>> playerFairResults = new HashMap<>();
        for (WeaponsPvE rarity : WeaponsPvE.VALUES) {
            if (rarity.getPlayerEntries == null) {
                continue;
            }
            List<MasterworksFairPlayerEntry> playerEntries = rarity.getPlayerEntries.apply(this);
            playerEntries.sort(Comparator.comparingDouble((MasterworksFairPlayerEntry entry) -> ((WeaponScore) entry.getWeapon()).getWeaponScore()).reversed());
            for (int i = 0; i < playerEntries.size(); i++) {
                MasterworksFairPlayerEntry entry = playerEntries.get(i);
                MasterworksFairEntry playerRecordEntry = new MasterworksFairEntry(
                        time,
                        rarity,
                        i + 1,
                        roundedScore(((WeaponScore) entry.getWeapon()).getWeaponScore()),
                        fairNumber
                );
                playerFairResults.computeIfAbsent(entry.getUuid(), k -> new ArrayList<>()).add(playerRecordEntry);
            }
        }

        List<MasterworksFairPlayerEntry> itemEntries = getItemPlayerEntries();
        itemEntries.sort(Comparator.comparingDouble((MasterworksFairPlayerEntry entry) -> entry.getItem().getItemScore()).reversed());
        for (int i = 0; i < itemEntries.size(); i++) {
            MasterworksFairPlayerEntry entry = itemEntries.get(i);
            MasterworksFairEntry playerRecordEntry = new MasterworksFairEntry(
                    time,
                    null,
                    true,
                    i + 1,
                    roundedScore(entry.getItem().getItemScore()),
                    fairNumber
            );
            playerFairResults.computeIfAbsent(entry.getUuid(), k -> new ArrayList<>()).add(playerRecordEntry);
        }
        return playerFairResults;
    }

    private float roundedScore(double score) {
        return Float.parseFloat(NumberFormat.formatOptionalHundredths(score));
    }

    public LinkedHashMap<Spendable, Long> getRewards(MasterworksFairEntry masterworksFairEntry) {
        if (masterworksFairEntry.isItemSubmission()) {
            return getItemRewards(masterworksFairEntry);
        }
        int placement = masterworksFairEntry.getPlacement();
        float score = masterworksFairEntry.getScore();
        WeaponsPvE rarity = masterworksFairEntry.getRarity();
        LinkedHashMap<Spendable, Long> rewards = new LinkedHashMap<>();
        if (placement <= 3) {
            rewards.put(rarity.starPieceCurrency, 1L);
            switch (placement) {
                case 1 -> {
                    switch (rarity) {
                        case COMMON -> rewards.put(Currencies.SUPPLY_DROP_TOKEN, 100L);
                        case RARE -> rewards.put(Currencies.SUPPLY_DROP_TOKEN, 150L);
                        case EPIC -> rewards.put(Currencies.SUPPLY_DROP_TOKEN, 200L);
                    }
                }
                case 2 -> {
                    switch (rarity) {
                        case COMMON -> rewards.put(Currencies.SUPPLY_DROP_TOKEN, 50L);
                        case RARE -> rewards.put(Currencies.SUPPLY_DROP_TOKEN, 75L);
                        case EPIC -> rewards.put(Currencies.SUPPLY_DROP_TOKEN, 100L);
                    }
                }
                case 3 -> {
                    switch (rarity) {
                        case COMMON -> rewards.put(Currencies.SUPPLY_DROP_TOKEN, 30L);
                        case RARE -> rewards.put(Currencies.SUPPLY_DROP_TOKEN, 50L);
                        case EPIC -> rewards.put(Currencies.SUPPLY_DROP_TOKEN, 70L);
                    }
                }
            }
        } else {
            if (placement <= 10 ||
                    (rarity == WeaponsPvE.COMMON && score > 90) ||
                    (rarity == WeaponsPvE.RARE && score > 85) ||
                    (rarity == WeaponsPvE.EPIC && score > 75)
            ) {
                switch (rarity) {
                    case COMMON -> rewards.put(Currencies.SUPPLY_DROP_TOKEN, 20L);
                    case RARE -> rewards.put(Currencies.SUPPLY_DROP_TOKEN, 35L);
                    case EPIC -> rewards.put(Currencies.SUPPLY_DROP_TOKEN, 50L);
                }
            } else if (placement <= 20) {
                switch (rarity) {
                    case COMMON -> rewards.put(Currencies.SUPPLY_DROP_TOKEN, 10L);
                    case RARE -> rewards.put(Currencies.SUPPLY_DROP_TOKEN, 20L);
                    case EPIC -> rewards.put(Currencies.SUPPLY_DROP_TOKEN, 30L);
                }
            } else {
                switch (rarity) {
                    case COMMON -> rewards.put(Currencies.SUPPLY_DROP_TOKEN, 5L);
                    case RARE -> rewards.put(Currencies.SUPPLY_DROP_TOKEN, 10L);
                    case EPIC -> rewards.put(Currencies.SUPPLY_DROP_TOKEN, 20L);
                }
            }
        }
        applyFairMultiplier(rewards);
        return rewards;
    }

    private LinkedHashMap<Spendable, Long> getItemRewards(MasterworksFairEntry masterworksFairEntry) {
        int placement = masterworksFairEntry.getPlacement();
        float score = masterworksFairEntry.getScore();
        LinkedHashMap<Spendable, Long> rewards = new LinkedHashMap<>();
        if (placement == 1) {
            rewards.put(Currencies.SUPPLY_DROP_TOKEN, 100L);
        } else if (placement == 2) {
            rewards.put(Currencies.SUPPLY_DROP_TOKEN, 50L);
        } else if (placement == 3) {
            rewards.put(Currencies.SUPPLY_DROP_TOKEN, 30L);
        } else if (placement <= 10 || score > 90) {
            rewards.put(Currencies.SUPPLY_DROP_TOKEN, 20L);
        } else if (placement <= 20) {
            rewards.put(Currencies.SUPPLY_DROP_TOKEN, 10L);
        } else {
            rewards.put(Currencies.SUPPLY_DROP_TOKEN, 5L);
        }
        applyFairMultiplier(rewards);
        return rewards;
    }

    private void applyFairMultiplier(LinkedHashMap<Spendable, Long> rewards) {
        if (fairNumber != 0 && fairNumber % 5 == 0) {
            rewards.replaceAll((currency, amount) -> amount * 2);
        }
    }

    public void sendResults(HashMap<UUID, List<MasterworksFairEntry>> playerFairResults, boolean inCaseYouMissedIt) {
        playerFairResults.forEach((uuid, masterworksFairEntries) ->
                Warlords.newChain()
                        .asyncFirst(() -> DatabaseManager.playerService.findByUUID(uuid))
                        .syncLast(optionalDatabasePlayer -> {
                            if (optionalDatabasePlayer.isEmpty()) {
                                return;
                            }
                            DatabasePlayer databasePlayer = optionalDatabasePlayer.get();
                            List<Component> message = new ArrayList<>();
                            message.add(Component.text("------------------------------------------------", NamedTextColor.GOLD));
                            if (inCaseYouMissedIt) {
                                message.add(Component.text("In case you missed it!", NamedTextColor.AQUA));
                            }
                            message.add(Component.text("Masterworks Fair #" + fairNumber + " Results", NamedTextColor.GREEN));
                            for (WeaponsPvE rarity : WeaponsPvE.VALUES) {
                                if (rarity.getPlayerEntries == null) {
                                    continue;
                                }
                                Optional<MasterworksFairEntry> masterworksFairEntry = masterworksFairEntries.stream()
                                                                                                             .filter(entry -> !entry.isItemSubmission() && entry.getRarity() == rarity)
                                                                                                             .findAny();
                                message.add(getResultComponent(rarity.getTextColoredName(), masterworksFairEntry));
                            }
                            Optional<MasterworksFairEntry> itemEntry = masterworksFairEntries.stream()
                                                                                           .filter(MasterworksFairEntry::isItemSubmission)
                                                                                           .findAny();
                            message.add(getResultComponent(Component.text("Item", NamedTextColor.LIGHT_PURPLE), itemEntry));
                            message.add(Component.empty());
                            message.add(Component.text("Claim your rewards through your", NamedTextColor.GREEN));
                            message.add(Component.text("Reward Inventory!", NamedTextColor.GREEN));
                            message.add(Component.text("------------------------------------------------", NamedTextColor.GOLD));
                            databasePlayer.addFutureMessage(FutureMessage.create(message, true));
                            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                        })
                        .execute());
    }

    private Component getResultComponent(Component category, Optional<MasterworksFairEntry> entry) {
        if (entry.isEmpty()) {
            return Component.textOfChildren(
                    category,
                    Component.text(": ", NamedTextColor.GRAY),
                    Component.text("Not Submitted", NamedTextColor.YELLOW)
            );
        }
        MasterworksFairEntry fairEntry = entry.get();
        return Component.textOfChildren(
                category,
                Component.text(": ", NamedTextColor.GRAY),
                Component.text(NumberFormat.formatOptionalHundredths(fairEntry.getScore()) + "% ", NamedTextColor.YELLOW),
                Component.text("(", NamedTextColor.GRAY),
                Component.text("#" + fairEntry.getPlacement(), NamedTextColor.AQUA),
                Component.text(")", NamedTextColor.GRAY)
        );
    }

    public void sendResults(boolean inCaseYouMissedIt) {
        sendResults(createResults(null), inCaseYouMissedIt);
    }

    public String getId() {
        return id;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public List<MasterworksFairPlayerEntry> getCommonPlayerEntries() {
        return commonPlayerEntries;
    }

    public List<MasterworksFairPlayerEntry> getRarePlayerEntries() {
        return rarePlayerEntries;
    }

    public List<MasterworksFairPlayerEntry> getEpicPlayerEntries() {
        return epicPlayerEntries;
    }

    public List<MasterworksFairPlayerEntry> getItemPlayerEntries() {
        if (itemPlayerEntries == null) {
            itemPlayerEntries = new ArrayList<>();
        }
        return itemPlayerEntries;
    }

    public boolean isEnded() {
        return ended;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    public int getFairNumber() {
        return fairNumber;
    }

    public void setFairNumber(int fairNumber) {
        this.fairNumber = fairNumber;
    }
}
