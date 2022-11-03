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
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.*;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class PatreonReward extends AbstractReward implements Listener {

    public static final LinkedHashMap<Currencies, Long> patreonRewards = new LinkedHashMap<>() {{
        put(Currencies.FAIRY_ESSENCE, 1000L);
    }};

    public static boolean giveMonthlyPatreonRewards(DatabasePlayer databasePlayer) {
        return giveMonthlyPatreonRewards(databasePlayer, Month.from(ZonedDateTime.now()), Year.from(ZonedDateTime.now()));
    }

    public static boolean giveMonthlyPatreonRewards(DatabasePlayer databasePlayer, Month month, Year year) {
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        List<PatreonReward> patreonRewards = pveStats.getPatreonRewards();
        for (PatreonReward patreonReward : patreonRewards) {
            ZonedDateTime rewardGiven = patreonReward.getTimeGiven().atZone(ZoneOffset.UTC);
            if (Month.from(rewardGiven) == month &&
                    Year.from(rewardGiven).equals(year)
            ) {
                return false;
            }
        }
        patreonRewards.add(new PatreonReward(Instant.now()
                .atZone(ZoneOffset.UTC)
                .withMonth(month.getValue())
                .withYear(year.getValue())
                .toInstant()
        ));
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        return true;
    }

    public static void givePatreonFutureMessage(DatabasePlayer databasePlayer, Month month, Year year) {
        databasePlayer.addFutureMessage(new FutureMessage(
                Arrays.asList(
                        ChatColor.LIGHT_PURPLE + "------------------------------------------------",
                        ChatColor.GREEN + "You received your " +
                                ChatColor.LIGHT_PURPLE + year.getValue() + " " + month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) +
                                ChatColor.GREEN + " Patreon reward!",
                        ChatColor.GREEN + "Claim it in your Rewards Inventory",
                        ChatColor.LIGHT_PURPLE + "------------------------------------------------"
                ),
                true
        ));
    }

    public Instant getTimeGiven() {
        return timeGiven;
    }

    @Field("time_given")
    private Instant timeGiven;

    public PatreonReward() {
    }

    public PatreonReward(Instant timeGiven) {
        super(patreonRewards,
                Year.from(timeGiven.atZone(ZoneOffset.UTC)).getValue() + " " +
                        Month.from(timeGiven.atZone(ZoneOffset.UTC)).getDisplayName(TextStyle.FULL, Locale.ENGLISH) +
                        " Patreon"
        );
        this.timeGiven = timeGiven;
    }

    @EventHandler
    public void onDatabasePlayerFirstLoad(DatabasePlayerFirstLoadEvent event) {
        if (!event.getPlayer().hasPermission("group.patreon")) {
            //return;
        }
        DatabasePlayer databasePlayer = event.getDatabasePlayer();
        Month month = Month.from(ZonedDateTime.now());
        Year year = Year.from(ZonedDateTime.now());
        boolean given = giveMonthlyPatreonRewards(databasePlayer, month, year);
        if (given) {
            givePatreonFutureMessage(databasePlayer, month, year);
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        }
    }

    @Override
    public ChatColor getNameColor() {
        return ChatColor.LIGHT_PURPLE;
    }
}
