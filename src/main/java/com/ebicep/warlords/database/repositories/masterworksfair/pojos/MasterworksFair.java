package com.ebicep.warlords.database.repositories.masterworksfair.pojos;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.FutureMessage;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairEntry;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.pve.weapons.weaponaddons.WeaponScore;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;
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

    public void sendResults() {
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
        sendResults(playerFairResults);
    }

    public void sendResults(HashMap<UUID, List<MasterworksFairEntry>> playerFairResults) {
        playerFairResults.forEach((uuid, masterworksFairEntries) -> {
            Warlords.newChain()
                    .asyncFirst(() -> DatabaseManager.playerService.findByUUID(uuid))
                    .syncLast(databasePlayer -> {
                        if (databasePlayer == null) {
                            return;
                        }
                        List<String> message = new ArrayList<>();
                        message.add(ChatColor.GOLD + "------------------------------------------------");
                        message.add(ChatColor.GREEN + "Masterworks Fair #" + fairNumber + " Results");
                        for (WeaponsPvE rarity : WeaponsPvE.VALUES) {
                            if (rarity.getPlayerEntries == null) {
                                continue;
                            }
                            Optional<MasterworksFairEntry> masterworksFairEntry = masterworksFairEntries.stream()
                                    .filter(entry -> entry.getRarity() == rarity)
                                    .findAny();
                            if (masterworksFairEntry.isPresent()) {
                                MasterworksFairEntry fairEntry = masterworksFairEntry.get();
                                message.add(rarity.getChatColorName() + ChatColor.GRAY + ": " +
                                        ChatColor.YELLOW + NumberFormat.formatOptionalHundredths(fairEntry.getScore()) + "% " +
                                        ChatColor.GRAY + "(" + ChatColor.AQUA + "#" + fairEntry.getPlacement() + ChatColor.GRAY + ")"
                                );
                            } else {
                                message.add(rarity.getChatColorName() + ChatColor.GRAY + ": " + ChatColor.YELLOW + "Not Submitted");
                            }
                        }
                        message.add("");
                        message.add(ChatColor.GREEN + "Claim your rewards through your");
                        message.add(ChatColor.GREEN + "Reward Inventory in your 9th slot!");
                        message.add(ChatColor.GOLD + "------------------------------------------------");
                        databasePlayer.addFutureMessage(new FutureMessage(message, true));
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                    })
                    .execute();
        });
    }

    public String getId() {
        return id;
    }

    public Instant getStartDate() {
        return startDate;
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
