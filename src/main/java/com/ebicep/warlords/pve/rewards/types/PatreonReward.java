package com.ebicep.warlords.pve.rewards.types;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.general.FutureMessage;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.events.player.DatabasePlayerFirstLoadEvent;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.rewards.AbstractReward;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import static javax.swing.UIManager.put;

public class PatreonReward extends AbstractReward implements Listener {

    public static final LinkedHashMap<Currencies, Long> patreonRewards = new LinkedHashMap<>() {{
        put(Currencies.FAIRY_ESSENCE, 1L);
    }};

    public static boolean giveMonthlyPatreonRewards(DatabasePlayer databasePlayer, Month month, Year year) {
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        List<PatreonReward> patreonRewards = pveStats.getPatreonRewards();
        for (PatreonReward patreonReward : patreonRewards) {
            Instant rewardGiven = patreonReward.getTimeGiven();
            if (Month.from(rewardGiven) == month &&
                    Year.from(rewardGiven) == year
            ) {
                return false;
            }
        }
        patreonRewards.add(new PatreonReward(Instant.now()));
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        return true;
    }

    public static boolean giveMonthlyPatreonRewards(DatabasePlayer databasePlayer) {
        return giveMonthlyPatreonRewards(databasePlayer, Month.from(Instant.now()), Year.from(Instant.now()));
    }

    @EventHandler
    public void onDatabasePlayerFirstLoad(DatabasePlayerFirstLoadEvent event) {
        DatabasePlayer databasePlayer = event.getDatabasePlayer();
        Month month = Month.from(Instant.now());
        Year year = Year.from(Instant.now());
        boolean given = giveMonthlyPatreonRewards(databasePlayer, month, year);
        if (given) {
            databasePlayer.addFutureMessage(new FutureMessage(
                    Arrays.asList(
                            ChatColor.LIGHT_PURPLE + "------------------------------------------------",
                            ChatColor.GREEN + "You received your " +
                                    ChatColor.LIGHT_PURPLE + month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) + year.getValue() +
                                    ChatColor.GREEN + " Patreon reward!",
                            ChatColor.GREEN + "Claim it in your Rewards Inventory",
                            ChatColor.LIGHT_PURPLE + "------------------------------------------------"
                    ),
                    true
            ));
        }
    }


    @Field("time_given")
    private Instant timeGiven;

    public PatreonReward() {
    }

    @Override
    public void giveToPlayer(DatabasePlayer databasePlayer) {

    }

    public PatreonReward(Instant timeGiven) {
        super(patreonRewards, "Patreon Reward");
        this.timeGiven = timeGiven;
    }

    @Override
    public List<String> getLore() {
        return super.getLore();
    }

    @Override
    public ItemStack getItem() {
        return super.getItem();
    }

    public Instant getTimeGiven() {
        return timeGiven;
    }

}
