package com.ebicep.warlords.database.repositories.masterworksfair.pojos;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
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
    @Field("ended")
    private boolean ended = false;
    @Field("fair_number")
    private int fairNumber;

    public MasterworksFair() {
    }

    public void sendRewards(boolean throughRewardsInventory) {
        Instant now = Instant.now();
        HashMap<UUID, List<MasterworksFairEntry>> playerFairResults = new HashMap<>();
        for (WeaponsPvE rarity : WeaponsPvE.VALUES) {
            if (rarity.getPlayerEntries == null) {
                continue;
            }

            List<MasterworksFairPlayerEntry> playerEntries = rarity.getPlayerEntries.apply(this);
            playerEntries.sort(Comparator.comparingDouble(o -> ((WeaponScore) o.getWeapon()).getWeaponScore()));
            Collections.reverse(playerEntries);

            for (int i = 0; i < playerEntries.size(); i++) {
                MasterworksFairPlayerEntry entry = playerEntries.get(i);
                MasterworksFairEntry playerRecordEntry = new MasterworksFairEntry(now,
                        rarity,
                        i + 1,
                        Float.parseFloat(NumberFormat.formatOptionalHundredths(((WeaponScore) entry.getWeapon()).getWeaponScore())),
                        fairNumber
                );
                playerFairResults.computeIfAbsent(entry.getUuid(), k -> new ArrayList<>()).add(playerRecordEntry);
            }
        }
        Warlords.newChain()
                .async(() -> playerFairResults.forEach((uuid, masterworksFairEntries) -> Warlords.newChain()
                                                                                                 .asyncFirst(() -> DatabaseManager.playerService.findByUUID(uuid))
                                                                                                 .syncLast(databasePlayer -> {
                                                                                                     if (databasePlayer == null) {
                                                                                                         return;
                                                                                                     }
                                                                                                     DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                                                                                                     if (pveStats == null) {
                                                                                                         return;
                                                                                                     }
                                                                                                     for (MasterworksFairEntry masterworksFairEntry : masterworksFairEntries) {
                                                                                                         WeaponsPvE rarity = masterworksFairEntry.getRarity();
                                                                                                         pveStats.addMasterworksFairEntry(masterworksFairEntry);
                                                                                                         LinkedHashMap<Spendable, Long> rewards = getRewards(
                                                                                                                 masterworksFairEntry);
                                                                                                         if (throughRewardsInventory) {
                                                                                                             pveStats.addReward(new MasterworksFairReward(
                                                                                                                     rewards,
                                                                                                                     now,
                                                                                                                     rarity
                                                                                                             ));
                                                                                                         } else {
                                                                                                             rewards.forEach((spendable, amount) -> spendable.addToPlayer(
                                                                                                                     databasePlayer,
                                                                                                                     amount
                                                                                                             ));
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

    public LinkedHashMap<Spendable, Long> getRewards(MasterworksFairEntry masterworksFairEntry) {
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
                switch (masterworksFairEntry.getRarity()) {
                    case COMMON -> rewards.put(Currencies.SUPPLY_DROP_TOKEN, 5L);
                    case RARE -> rewards.put(Currencies.SUPPLY_DROP_TOKEN, 10L);
                    case EPIC -> rewards.put(Currencies.SUPPLY_DROP_TOKEN, 20L);
                }
            }
        }
        if (fairNumber != 0 && fairNumber % 5 == 0) {
            rewards.forEach((currency, amount) -> rewards.put(currency, amount * 2));
        }
        return rewards;
    }

    public void sendResults(HashMap<UUID, List<MasterworksFairEntry>> playerFairResults, boolean inCaseYouMissedIt) {
        playerFairResults.forEach((uuid, masterworksFairEntries) ->
                Warlords.newChain()
                        .asyncFirst(() -> DatabaseManager.playerService.findByUUID(uuid))
                        .syncLast(databasePlayer -> {
                            if (databasePlayer == null) {
                                return;
                            }
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
                                                                                                            .filter(entry -> entry.getRarity() == rarity)
                                                                                                            .findAny();
                                if (masterworksFairEntry.isPresent()) {
                                    MasterworksFairEntry fairEntry = masterworksFairEntry.get();
                                    message.add(
                                            Component.textOfChildren(
                                                    rarity.getTextColoredName(),
                                                    Component.text(": ", NamedTextColor.GRAY),
                                                    Component.text(NumberFormat.formatOptionalHundredths(fairEntry.getScore()) + "% ", NamedTextColor.YELLOW),
                                                    Component.text("(", NamedTextColor.GRAY),
                                                    Component.text("#" + fairEntry.getPlacement(), NamedTextColor.AQUA),
                                                    Component.text(")", NamedTextColor.GRAY)
                                            )
                                    );
                                } else {
                                    message.add(
                                            Component.textOfChildren(
                                                    rarity.getTextColoredName(),
                                                    Component.text(": ", NamedTextColor.GRAY),
                                                    Component.text("Not Submitted", NamedTextColor.YELLOW)
                                            )
                                    );
                                }
                            }
                            message.add(Component.empty());
                            message.add(Component.text("Claim your rewards through your", NamedTextColor.GREEN));
                            message.add(Component.text("Reward Inventory!", NamedTextColor.GREEN));
                            message.add(Component.text("------------------------------------------------", NamedTextColor.GOLD));
                            databasePlayer.addFutureMessage(new FutureMessage(message, true));
                            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                        })
                        .execute());
    }

    public void sendResults(boolean inCaseYouMissedIt) {
        HashMap<UUID, List<MasterworksFairEntry>> playerFairResults = new HashMap<>();
        for (WeaponsPvE rarity : WeaponsPvE.VALUES) {
            if (rarity.getPlayerEntries == null) {
                continue;
            }

            List<MasterworksFairPlayerEntry> playerEntries = rarity.getPlayerEntries.apply(this);
            playerEntries.sort(Comparator.comparingDouble(o -> ((WeaponScore) o.getWeapon()).getWeaponScore()));
            Collections.reverse(playerEntries);

            for (int i = 0; i < playerEntries.size(); i++) {
                MasterworksFairPlayerEntry entry = playerEntries.get(i);
                MasterworksFairEntry playerRecordEntry = new MasterworksFairEntry(null,
                        rarity,
                        i + 1,
                        Float.parseFloat(NumberFormat.formatOptionalHundredths(((WeaponScore) entry.getWeapon()).getWeaponScore())),
                        fairNumber
                );
                playerFairResults.computeIfAbsent(entry.getUuid(), k -> new ArrayList<>()).add(playerRecordEntry);

            }
        }
        sendResults(playerFairResults, inCaseYouMissedIt);
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

    @Override
    public String toString() {
        return "MasterworksFair{startDate=" + startDate + ", commonPlayerEntries=" + commonPlayerEntries.size() + ", rarePlayerEntries=" + rarePlayerEntries.size() + ", epicPlayerEntries=" + epicPlayerEntries.size() + '}';
    }
}
