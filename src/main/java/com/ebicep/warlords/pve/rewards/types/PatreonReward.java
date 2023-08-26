package com.ebicep.warlords.pve.rewards.types;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.general.FutureMessage;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.events.player.DatabasePlayerFirstLoadEvent;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.rewards.AbstractReward;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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

    public static final LinkedHashMap<Spendable, Long> PATREON_REWARDS = new LinkedHashMap<>() {{
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

    public Instant getTimeGiven() {
        return timeGiven;
    }

    @Field("time_given")
    private Instant timeGiven;

    public PatreonReward() {
    }

    public PatreonReward(Instant timeGiven) {
        super(PATREON_REWARDS,
                Year.from(timeGiven.atZone(ZoneOffset.UTC)).getValue() + " " +
                        Month.from(timeGiven.atZone(ZoneOffset.UTC)).getDisplayName(TextStyle.FULL, Locale.ENGLISH) +
                        " Patreon"
        );
        this.timeGiven = timeGiven;
    }

    @EventHandler
    public void onDatabasePlayerFirstLoad(DatabasePlayerFirstLoadEvent event) {
        if (!event.getPlayer().hasPermission("group.patreon") || !event.getPlayer().hasPermission("group.contentcreator")) {
            return;
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

    public static void givePatreonFutureMessage(DatabasePlayer databasePlayer, Month month, Year year) {
        databasePlayer.addFutureMessage(FutureMessage.create(
                Arrays.asList(
                        Component.text("------------------------------------------------", NamedTextColor.LIGHT_PURPLE),
                        Component.text("You received your ", NamedTextColor.GREEN)
                                 .append(Component.text(month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + year.getValue(), NamedTextColor.LIGHT_PURPLE))
                                 .append(Component.text(" Patreon reward!", NamedTextColor.GREEN)),
                        Component.text("Claim it in your Rewards Inventory", NamedTextColor.GREEN),
                        Component.text("------------------------------------------------", NamedTextColor.LIGHT_PURPLE)
                ),
                true
        ));
    }

    @Override
    public TextColor getNameColor() {
        return NamedTextColor.LIGHT_PURPLE;
    }
}
